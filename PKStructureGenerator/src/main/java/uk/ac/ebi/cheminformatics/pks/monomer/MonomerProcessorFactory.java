package uk.ac.ebi.cheminformatics.pks.monomer;

import uk.ac.ebi.cheminformatics.pks.sequence.feature.SequenceFeature;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 20/4/15
 * Time: 16:17
 * To change this template use File | Settings | File Templates.
 */
public class MonomerProcessorFactory {
    public static MonomerProcessor getMonomerProcessor(SequenceFeature feat) {
        if(feat.getClass().isInstance(ERDomainSeqFeature)) {
            return new ERMonomerProcessor();
        } else if(feat.getClass().isInstance(MTDomainSeqFeature)) {
            return new MTMonomerProcessor();
        } else if(feat.getClass().isInstance(OMTDomainSeqFeature)) {
            return new OMTMonomerProcessor();
        } else if(feat.getClass().isInstance(KRDomainSeqFeature)) {
            return new KRMonomerProcessor();
        }
        return new NoActionMonomerProcessor();
    }
}
