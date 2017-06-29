package uk.ac.ebi.cheminformatics.pks.generator;

import com.google.common.collect.Range;
import org.apache.log4j.Logger;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureSelection;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.DomainSeqFeature;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.KSDomainSeqFeature;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.SequenceFeature;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.toList;
import static uk.ac.ebi.cheminformatics.pks.parser.FeatureParser.parse;

@SuppressWarnings("Convert2MethodRef")
public class StructureGenerator {

    private static final Logger LOGGER = Logger.getLogger(StructureGenerator.class);

    private PKStructure structure;

    private final List<SequenceFeature> sequenceFeatures;

    public StructureGenerator(Path featuresFile) {
        sequenceFeatures = parse(featuresFile);
    }

    public void run() {

        PKSAssembler assembler = new PKSAssembler();

        List<SequenceFeature> sequenceFeatures = this.sequenceFeatures.stream()
                // apply a cut-off to filter out false positives
                .filter(seq -> seq.isSignificant())
                .collect(toList());

        List<List<SequenceFeature>> clustered = FeatureSelection.clusterByAlignment(sequenceFeatures, 20);

        String clustering = clustered.stream().map(group ->
                group.stream()
                        .map(sequenceFeature -> {
                            String name = sequenceFeature.getName();
                            Range<Integer> range = sequenceFeature.getRange();
                            return String.format("%s(%d..%d)", name, range.lowerEndpoint(), range.upperEndpoint());
                        })
                        .collect(Collectors.joining("\n")))
                .collect(Collectors.joining("\n\n"));

        String picks = clustered.stream().map(group ->
                group.stream()
                        .filter(KSDomainSeqFeature.class::isInstance)
                        .map(KSDomainSeqFeature.class::cast)
                        .sorted(comparingDouble(KSDomainSeqFeature::getEvalue))
                        .limit(5)
                        .map(sequenceFeature -> {
                            String name = sequenceFeature.getName();
                            Range<Integer> range = sequenceFeature.getRange();
                            String label = sequenceFeature.getOriginatingFeatureFileLine().getLabel();
                            return String.format("%s %s (%6.3e) [%d..%d]", name, label, sequenceFeature.getEvalue(), range.lowerEndpoint(), range.upperEndpoint());
                        })
                        .collect(Collectors.joining("\n")))
                .collect(Collectors.joining("\n\n"));

        LOGGER.info(picks);

        System.out.println(sequenceFeatures.stream().filter(seq -> seq instanceof KSDomainSeqFeature).map(seq -> seq.getName() + "->" + seq.getMonomer().getMolFilename()).collect(Collectors.joining("\n")));

        sequenceFeatures.forEach(feature -> {
            assembler.addMonomer(feature);
        });

        SequenceFeature finalizer = new DomainSeqFeature(0, 0, "finalExtension", "0");
        assembler.addMonomer(finalizer);
        assembler.postProcess();

        this.structure = assembler.getStructure();
    }

    public PKStructure getStructure() {
        return this.structure;
    }
}
