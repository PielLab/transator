package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLineParser;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 4/7/13
 * Time: 15:06
 * To change this template use File | Settings | File Templates.
 */
public class PatternSeqFeatureFactory {
    public static SequenceFeature makeSequenceFeature(FeatureFileLineParser parser) {
        return new ProteinPatternSeqFeature(parser.getStart(),parser.getStop(),parser.getName(),parser.getEvalue());
    }
}
