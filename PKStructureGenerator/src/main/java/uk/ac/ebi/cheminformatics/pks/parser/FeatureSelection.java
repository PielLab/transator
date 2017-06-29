package uk.ac.ebi.cheminformatics.pks.parser;

import clustering.DBSCANClusterer;
import uk.ac.ebi.cheminformatics.pks.generator.DistanceMetricSequenceFeatures;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.SequenceFeature;

import java.util.List;

public final class FeatureSelection {

    public static List<SequenceFeature> keepBestOfSubfeatures(List<SequenceFeature> sequenceFeatures) {
        // TODO YOU WERE HERE: filter the clustering
        return null;
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
