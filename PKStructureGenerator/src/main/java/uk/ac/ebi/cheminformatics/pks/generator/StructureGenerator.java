package uk.ac.ebi.cheminformatics.pks.generator;

import org.apache.log4j.Logger;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureSelection;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.DomainSeqFeature;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.SequenceFeature;

import java.nio.file.Path;
import java.util.List;

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

        List<SequenceFeature> filteredSequences = FeatureSelection.byScore(this.sequenceFeatures);

        // TODO: this is just for debugging purposes, remove it eventually
        filteredSequences.forEach(seq -> {
            LOGGER.info(seq.getName());
        });

        filteredSequences.forEach(feature -> {
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
