package uk.ac.ebi.cheminformatics.pks.parser;

import clustering.DBSCANClusterer;
import uk.ac.ebi.cheminformatics.pks.generator.DistanceMetricSequenceFeatures;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.SequenceFeature;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Streams.mapWithIndex;
import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.toList;
import static uk.ac.ebi.cheminformatics.pks.sequence.feature.SequenceFeatureFactory.makeSequenceFeature;

public final class FeatureSelection {


    public static Stream<SequenceFeature> bestMatchCascade(Stream<SequenceFeature> features, int cascadeHeight) {
        return bestMatchCascade(features, cascadeHeight, 5, 20);
    }

    public static Stream<SequenceFeature> bestMatchCascade(Stream<SequenceFeature> features, int cascadeHeight, int minCluster, int maxDistance) {

        List<List<SequenceFeature>> clusteredByPosition = FeatureSelection.clusterByAlignment(features.collect(toList()), minCluster, maxDistance);

        return clusteredByPosition.stream().flatMap(group -> {

            Stream<SequenceFeature> cascade = group.stream()
                    .sorted(comparingDouble((SequenceFeature feature) -> feature.getEValue().orElseThrow(() -> new IllegalStateException("EValue is required for clustering"))))
                    .limit(cascadeHeight);

            String debugGroup = group.stream()
                    .sorted(comparingDouble((SequenceFeature feature) -> feature.getEValue().orElseThrow(() -> new IllegalStateException("EValue is required for clustering"))))
                    .limit(5)
                    .map(seq -> seq.getName() + " " + seq.getLabel() + " " + seq.getRange() + " " + seq.getEValue().orElse(0.0))
                    .collect(Collectors.joining("\n"));

            System.out.println(debugGroup + "\n");

            return mapWithIndex(cascade,
                    (seq, index) -> makeSequenceFeature(seq.getOriginatingFeatureFileLine(), (int) index + 1));
        });
    }

    private static List<List<SequenceFeature>> clusterByAlignment(List<SequenceFeature> sequenceFeatures, int minCluster, int maxDistance) {
        try {

            DBSCANClusterer<SequenceFeature> clusterer = new DBSCANClusterer<>(sequenceFeatures, minCluster, maxDistance, new DistanceMetricSequenceFeatures());

            return clusterer.performClustering();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
