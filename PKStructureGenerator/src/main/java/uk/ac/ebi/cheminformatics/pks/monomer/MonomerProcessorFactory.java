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
        if(feat instanceof ERDomainSeqFeature) {
            return new ERMonomerProcessor();
        } else if(feat instanceof MTDomainSeqFeature) {
            return new MTMonomerProcessor();
        } else if(feat instanceof OMTDomainSeqFeature) {
            return new OMTMonomerProcessor();
        } else if(feat instanceof KRDomainSeqFeature) {
            return new KRMonomerProcessor();
        }
        return new NoActionMonomerProcessor();
    }
}
