package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.monomer.MonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.OMTMonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLineParser;

public class OMTDomainSeqFeature extends DomainSeqFeature {
    public OMTDomainSeqFeature(FeatureFileLineParser parser) {
        super(parser);
    }

    @Override
    public MonomerProcessor getMonomerProcessor() {
        return new OMTMonomerProcessor();
    }
}
