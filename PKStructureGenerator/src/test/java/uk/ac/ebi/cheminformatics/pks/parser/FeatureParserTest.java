package uk.ac.ebi.cheminformatics.pks.parser;

import org.junit.Test;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.SequenceFeature;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 31/7/13
 * Time: 22:16
 * To change this template use File | Settings | File Templates.
 */
public class FeatureParserTest {
    @Test
    public void testGetNext() throws Exception {
        FeatureParser parser = new FeatureParser(
                FeatureParserTest.class.getResourceAsStream("Cosmidsequence.features"));

        while (parser.hasNext()) {
            SequenceFeature feat = parser.next();
        }

    }
}
