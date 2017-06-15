package uk.ac.ebi.cheminformatics.pks.generator;

import org.apache.log4j.Logger;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLineParser;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.DomainSeqFeature;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.KSDomainSeqFeature;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.SequenceFeature;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.SequenceFeatureFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 3/7/13
 * Time: 21:23
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("Convert2MethodRef")
public class StructureGenerator {

    private static final Logger LOGGER = Logger.getLogger(StructureGenerator.class);


    private PKStructure structure;

    private final List<SequenceFeature> sequenceFeatures;

    public StructureGenerator(Path featuresFile) {
        sequenceFeatures = readFeaturesFromFile(featuresFile);
    }

    private List<SequenceFeature> readFeaturesFromFile(Path featuresFile) {
        try (Stream<String> stream = Files.lines(featuresFile)) {
            return stream
                    .filter(line -> !line.startsWith("#"))
                    .map(FeatureFileLineParser::new)
                    .map(SequenceFeatureFactory::makeSequenceFeature)
                    .collect(toList());
        } catch (IOException e) {
            LOGGER.error("Problems reading in features file ", e);
            throw new RuntimeException(e);
        }
    }

    public void run() {

        PKSAssembler assembler = new PKSAssembler();

        // we pick the KS which are highest in their stack
        sequenceFeatures.stream()
                .filter(seq -> !(seq instanceof KSDomainSeqFeature) || ((KSDomainSeqFeature) seq).getRanking() == 1)
                .forEach(feature -> {
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
