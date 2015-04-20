package uk.ac.ebi.cheminformatics.pks.monomer;

import uk.ac.ebi.cheminformatics.pks.sequence.feature.*;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 20/4/15
 * Time: 16:17
 * To change this template use File | Settings | File Templates.
 */
public class MonomerProcessorFactory {
    public static MonomerProcessor getMonomerProcessor(SequenceFeature feat) {
        if(feat.getClass().isInstance(ERDomainSeqFeature.class)) {
            return new ERMonomerProcessor();
        } else if(feat.getClass().isInstance(MTDomainSeqFeature.class)) {
            return new MTMonomerProcessor();
        } else if(feat.getClass().isInstance(OMTDomainSeqFeature.class)) {
            return new OMTMonomerProcessor();
        } else if(feat.getClass().isInstance(KRDomainSeqFeature.class)) {
            return new KRMonomerProcessor();
        }
        return new NoActionMonomerProcessor();
    }
}
