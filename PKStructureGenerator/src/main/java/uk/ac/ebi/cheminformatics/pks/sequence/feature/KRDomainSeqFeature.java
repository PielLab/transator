package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.monomer.KRMonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.MonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLineParser;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 20/4/15
 * Time: 18:16
 * To change this template use File | Settings | File Templates.
 */
public class KRDomainSeqFeature extends DomainSeqFeature implements SequenceFeature{
    public KRDomainSeqFeature(FeatureFileLineParser parser) {
        super(parser);
    }

    @Override
    public MonomerProcessor getMonomerProcessor() {
        return new KRMonomerProcessor();
    }
}
