package uk.ac.ebi.cheminformatics.pks.generator;

import com.google.common.collect.Streams;
import org.apache.log4j.Logger;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureSelection;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.DomainSeqFeature;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.KSDomainSeqFeature;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.SequenceFeature;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Comparator.comparingInt;
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

        List<SequenceFeature> significantFeatures = sequenceFeatures.stream()
                // apply a cut-off to filter out false positives
                .filter(SequenceFeature::isSignificant)
                .collect(toList());

        // TODO: this code is currently duplicated
        Stream<SequenceFeature> nonKs = significantFeatures.stream().filter(seq -> !(seq instanceof KSDomainSeqFeature));

        Stream<SequenceFeature> ks = significantFeatures.stream().filter(KSDomainSeqFeature.class::isInstance);

        Stream<SequenceFeature> bestKs = FeatureSelection.bestMatchCascade(ks, 1);

        List<SequenceFeature> selectedSequenceFeatures = Streams.concat(nonKs, bestKs)
                .sorted(comparingInt(feature -> feature.getRange().lowerEndpoint()))
                .collect(toList());

        selectedSequenceFeatures.forEach(feature -> {
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
