package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLine;

public class SequenceFeatureFactory {
    public static SequenceFeature makeSequenceFeature(FeatureFileLine parser) {
        return makeSequenceFeature(parser, 0);
    }

    public static SequenceFeature makeSequenceFeature(FeatureFileLine parser, int ranking) {
        if (parser.getType().equalsIgnoreCase("domain")) {
            DomainSeqFeature nseqFeat;
            switch (parser.getSubtype()) {
                case "AL":
                    nseqFeat = new ALDomainSeqFeature(parser);
                    break;
                case "B":
                    nseqFeat = new BRDomainSeqFeature(parser);
                    break;
                case "C":
                    nseqFeat = new CDomainSeqFeature(parser);
                    break;
                case "Cyc":
                    nseqFeat = new CycDomainSeqFeature(parser);
                    break;
                case "DH":
                    nseqFeat = new DHDomainSeqFeature(parser);
                    break;
                case "PS":
                    nseqFeat = new PSDomainSeqFeature(parser);
                    break;
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
                    nseqFeat = new KSDomainSeqFeature(parser, ranking);
                    break;
                case "OMT":
                    nseqFeat = new OMTDomainSeqFeature(parser);
                    break;
                case "NRPS2":
                    nseqFeat = new NRPSDomSeqFeature(parser);
                    break;
                default:
                    nseqFeat = new DomainSeqFeature(parser, ranking);
                    break;
            }
            return nseqFeat;
        } else if (parser.getType().equalsIgnoreCase("pattern")) {
            return PatternSeqFeatureFactory.makeSequenceFeature(parser);
        }
        return null;
    }
}
