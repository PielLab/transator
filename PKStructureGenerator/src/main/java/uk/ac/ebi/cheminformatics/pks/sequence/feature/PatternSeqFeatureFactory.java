package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLine;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 4/7/13
 * Time: 15:06
 * To change this template use File | Settings | File Templates.
 */
public class PatternSeqFeatureFactory {
    public static SequenceFeature makeSequenceFeature(FeatureFileLine featureFileLine) {
        return new ProteinPatternSeqFeature(featureFileLine);
    }
}
