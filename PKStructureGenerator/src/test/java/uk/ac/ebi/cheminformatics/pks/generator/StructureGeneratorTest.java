package uk.ac.ebi.cheminformatics.pks.generator;

import org.junit.Test;
import org.openscience.cdk.io.MDLV2000Writer;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureParserTest;

import java.io.FileWriter;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 31/7/13
 * Time: 22:31
 * To change this template use File | Settings | File Templates.
 */
public class StructureGeneratorTest {
    @Test
    public void testGetStructure() throws Exception {
        StructureGenerator generator = new StructureGenerator(FeatureParserTest.class.getResourceAsStream("Cosmidsequence.features"));

        generator.run();

        PKStructure struct = generator.getStructure();

        MDLV2000Writer writer = new MDLV2000Writer(new FileWriter("/tmp/cosmid.mol"));
        writer.write(struct.getMolecule());
        writer.close();
    }

    @Test
    public void testGetStructureConcat() throws Exception {
        StructureGenerator generator = new StructureGenerator(FeatureParserTest.class.getResourceAsStream("concatenated.features"));

        generator.run();

        PKStructure struct = generator.getStructure();

        MDLV2000Writer writer = new MDLV2000Writer(new FileWriter("/tmp/concat.mol"));
        writer.write(struct.getMolecule());
        writer.close();
    }
}
