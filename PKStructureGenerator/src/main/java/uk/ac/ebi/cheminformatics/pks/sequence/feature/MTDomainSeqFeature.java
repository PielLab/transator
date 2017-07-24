package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.monomer.MonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.NoActionMonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLine;

public class MTDomainSeqFeature extends DomainSeqFeature {
    public MTDomainSeqFeature(FeatureFileLine parser) {
        super(parser);
    }

    @Override
    public MonomerProcessor getMonomerProcessor() {
        // TODO: For which clades do we still need this processor?
        // return new MTMonomerProcessor()
        return new NoActionMonomerProcessor();
    }
}
