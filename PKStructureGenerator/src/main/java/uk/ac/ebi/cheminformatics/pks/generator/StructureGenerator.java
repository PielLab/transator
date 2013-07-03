package uk.ac.ebi.cheminformatics.pks.generator;

import uk.ac.ebi.cheminformatics.pks.parser.FeatureParser;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 3/7/13
 * Time: 21:23
 * To change this template use File | Settings | File Templates.
 */
public class StructureGenerator {

    private PKStructure structure;
    private String path;

    public void run() {
        FeatureParser parser = new FeatureParser(this.path);

        PKSAssembler assembler = new PKSAssembler();
        while(parser.hasNext()) {
            assembler.addMonomer(parser.next());
        }

        this.structure = assembler.getStructure();
    }

    public PKStructure getStructure() {
        return this.structure;
    }
}
