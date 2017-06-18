package uk.ac.ebi.cheminformatics.pks.generator;

import com.google.common.collect.Range;
import org.apache.log4j.Logger;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureSelection;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.DomainSeqFeature;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.SequenceFeature;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

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

        List<SequenceFeature> sequenceFeatures = FeatureSelection.keepSignificant(this.sequenceFeatures);

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

        System.out.println(clustering);

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
