package uk.ac.ebi.cheminformatics.pks.generator;

import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.MDLV2000Writer;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureParserTest;

import java.io.FileWriter;
import java.io.IOException;

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
        runGenerator("Cosmidsequence.features", "/tmp/cosmid.mol");
    }

    @Test
    public void testGetStructureConcat() throws Exception {
        String features = "concatenated.features";
        String molFileOut = "/tmp/concat.mol";

        runGenerator(features, molFileOut);
    }

    @Test
    public void testGetStructureOnnamide() throws Exception {
        runGenerator("onnamid.features","/tmp/onnamidePK.mol");
    }

    private void runGenerator(String features, String molFileOut) throws IOException, CDKException {
        StructureGenerator generator = new StructureGenerator(FeatureParserTest.class.getResourceAsStream(features));

        generator.run();

        PKStructure struct = generator.getStructure();

        MDLV2000Writer writer = new MDLV2000Writer(new FileWriter(molFileOut));
        writer.write(struct.getMolecule());
        writer.close();
    }
}
