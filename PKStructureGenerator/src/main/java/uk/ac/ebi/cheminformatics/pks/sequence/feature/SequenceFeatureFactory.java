package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLineParser;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 4/7/13
 * Time: 10:20
 * To change this template use File | Settings | File Templates.
 */
public class SequenceFeatureFactory {


    public static SequenceFeature makeSequenceFeature(FeatureFileLineParser parser) {
        if(parser.getType().equalsIgnoreCase("domain")
                && parser.getSubtype().equalsIgnoreCase("KS")) {
            return new KSDomainSeqFeature(parser);
        }
        else if(parser.getType().equalsIgnoreCase("domain")) {
            return new DomainSeqFeature(parser);
        } else if(parser.getType().equalsIgnoreCase("pattern")) {
            return PatternSeqFeatureFactory.makeSequenceFeature(parser);
        }
        return null;
    }
}
