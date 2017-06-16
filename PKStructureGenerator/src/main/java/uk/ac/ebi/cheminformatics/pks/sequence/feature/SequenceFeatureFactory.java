package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLine;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 4/7/13
 * Time: 10:20
 * To change this template use File | Settings | File Templates.
 */
public class SequenceFeatureFactory {


    public static SequenceFeature makeSequenceFeature(FeatureFileLine parser) {
        if (parser.getType().equalsIgnoreCase("domain")) {
            DomainSeqFeature nseqFeat;
            switch (parser.getSubtype()) {
                case "ER":
                    nseqFeat = new ERDomainSeqFeature(parser);
                    break;
                case "MT":
                    nseqFeat = new MTDomainSeqFeature(parser);
                    break;
                case "KR":
                    nseqFeat = new KRDomainSeqFeature(parser);
                    break;
                case "KS":
                    nseqFeat = new KSDomainSeqFeature(parser);
                    break;
                case "OMT":
                    nseqFeat = new OMTDomainSeqFeature(parser);
                    break;
                case "NRPS2":
                    nseqFeat = new NRPSDomSeqFeature(parser);
                    break;
                default:
                    nseqFeat = new DomainSeqFeature(parser);
                    break;
            }
            return nseqFeat;
        } else if (parser.getType().equalsIgnoreCase("pattern")) {
            return PatternSeqFeatureFactory.makeSequenceFeature(parser);
        }
        return null;
    }
}
