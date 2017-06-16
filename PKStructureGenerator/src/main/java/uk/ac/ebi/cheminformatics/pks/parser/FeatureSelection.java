package uk.ac.ebi.cheminformatics.pks.parser;

import uk.ac.ebi.cheminformatics.pks.sequence.feature.KSDomainSeqFeature;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.SequenceFeature;

import java.util.List;

import static java.util.stream.Collectors.toList;

public final class FeatureSelection {

    public static List<SequenceFeature> byScore(List<SequenceFeature> sequenceFeatures) {
        return sequenceFeatures.stream()
                // we pick the KS which are highest in their stack. Other Seq domains pass through
                .filter(seq -> ifKsOnlyHighest(seq))
                // apply a cut-off to filter out false positives
                .filter(seq -> seq.isSignificant()).collect(toList());
    }

    private static boolean ifKsOnlyHighest(SequenceFeature seq) {
        return !(seq instanceof KSDomainSeqFeature) || ((KSDomainSeqFeature) seq).getRanking() == 1;
    }


}
