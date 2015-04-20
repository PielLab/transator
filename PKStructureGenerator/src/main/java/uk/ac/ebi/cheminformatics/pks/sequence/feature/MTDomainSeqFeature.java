package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLineParser;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.DomainSeqFeature;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.SequenceFeature;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 20/4/15
 * Time: 18:13
 * To change this template use File | Settings | File Templates.
 */
public class MTDomainSeqFeature extends DomainSeqFeature implements SequenceFeature{
    public MTDomainSeqFeature(FeatureFileLineParser parser) {
        super(parser);
    }
}
