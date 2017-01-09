package uk.ac.ebi.cheminformatics.pks.generator;

import uk.ac.ebi.cheminformatics.pks.parser.FeatureParser;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.DomainSeqFeature;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.SequenceFeature;

import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 3/7/13
 * Time: 21:23
 * To change this template use File | Settings | File Templates.
 */
public class StructureGenerator {

    private PKStructure structure;
    private FeatureParser parser;

    public StructureGenerator(InputStream inputFeatures) {
        parser = new FeatureParser(inputFeatures);
    }

    public StructureGenerator(String path) {
        parser = new FeatureParser(path);
    }

    public void run() {


        PKSAssembler assembler = new PKSAssembler();
        while(parser.hasNext()) {
            assembler.addMonomer(parser.next());
        }

        SequenceFeature finalizer = new DomainSeqFeature(0,0,"finalExtension","0");
        assembler.addMonomer(finalizer);
        assembler.postProcess();

        this.structure = assembler.getStructure();
    }

    public PKStructure getStructure() {
        return this.structure;
    }
}
