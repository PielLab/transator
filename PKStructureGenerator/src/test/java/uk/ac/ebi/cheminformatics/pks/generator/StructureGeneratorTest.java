package uk.ac.ebi.cheminformatics.pks.generator;

import org.apache.log4j.Logger;
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

    private static final Logger LOGGER = Logger.getLogger(PKStructure.class);

    @Test
    public void testGetStructure() throws Exception {
        LOGGER.info("Starting cosmid sequence features test");
        runGenerator("Cosmidsequence.features", "/tmp/cosmid.mol");
    }

    @Test
    public void testGetStructureConcat() throws Exception {
        LOGGER.info("Starting concat features test");
        String features = "concatenated.features";
        String molFileOut = "/tmp/concat.mol";

        runGenerator(features, molFileOut);
    }

    @Test
    public void testGetStructureOnnamide() throws Exception {
        LOGGER.info("Starting onnamide sequence features test");
        runGenerator("onnamid.features","/tmp/onnamidePK.mol");
    }

    @Test
    public void testExtenderClade27ShortOnnamide() throws Exception {
        LOGGER.info("Starting extender features test");
        runGenerator("onnamidShortExtender27Test.features","/tmp/onnamideExtenderShort.mol");
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
