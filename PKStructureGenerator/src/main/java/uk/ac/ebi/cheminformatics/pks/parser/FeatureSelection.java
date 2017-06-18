package uk.ac.ebi.cheminformatics.pks.parser;

import clustering.DBSCANClusterer;
import uk.ac.ebi.cheminformatics.pks.generator.DistanceMetricSequenceFeatures;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.KSDomainSeqFeature;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.SequenceFeature;

import java.util.List;

import static java.util.stream.Collectors.toList;

public final class FeatureSelection {

    // TODO: we need to separate rank based selection from significance selection
    public static List<SequenceFeature> keepSignificant(List<SequenceFeature> sequenceFeatures) {
        return sequenceFeatures.stream()
                // we pick the KS which are highest in their stack. Other Seq domains pass through
                .filter(seq -> ifKsOnlyHighest(seq))
                // apply a cut-off to filter out false positives
                .filter(seq -> seq.isSignificant()).collect(toList());
    }

    private static boolean ifKsOnlyHighest(SequenceFeature seq) {
        return !(seq instanceof KSDomainSeqFeature) || ((KSDomainSeqFeature) seq).getRanking() == 1;
    }


    public static List<SequenceFeature> keepBestOfSubfeatures(List<SequenceFeature> sequenceFeatures) {
        // TODO YOU WERE HERE: filter the clustering
    }

    public static List<List<SequenceFeature>> clusterByAlignment(List<SequenceFeature> sequenceFeatures, int maxDistance) {
        try {

            // Apparently, this is the same hierarchical clustering SLM
            // I could use hierarchical clustering, but this code was easiest to use.
            int minCluster = 2;

            DBSCANClusterer<SequenceFeature> clusterer = new DBSCANClusterer<>(sequenceFeatures, minCluster, maxDistance, new DistanceMetricSequenceFeatures());

            return clusterer.performClustering();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
