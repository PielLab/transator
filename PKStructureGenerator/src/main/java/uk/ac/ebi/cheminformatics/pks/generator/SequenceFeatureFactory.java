package uk.ac.ebi.cheminformatics.pks.generator;

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
        if(parser.getType().equalsIgnoreCase("domain")) {
            return new DomainSeqFeature(parser.getStart(),parser.getStop(),parser.getName(),parser.getEvalue());
        } else if(parser.getType().equalsIgnoreCase("pattern")) {
            return PatternSeqFeatureFactory.makeSequenceFeature(parser);
        }
        return null;
    }
}
