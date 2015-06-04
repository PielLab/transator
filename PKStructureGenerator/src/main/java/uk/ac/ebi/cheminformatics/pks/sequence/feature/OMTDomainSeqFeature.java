package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.monomer.MonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.OMTMonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLineParser;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 20/4/15
 * Time: 18:15
 * To change this template use File | Settings | File Templates.
 */
public class OMTDomainSeqFeature extends DomainSeqFeature implements SequenceFeature{
    public OMTDomainSeqFeature(FeatureFileLineParser parser) {
        super(parser);
    }

    @Override
    public MonomerProcessor getMonomerProcessor() {
        return new OMTMonomerProcessor();
    }
}
