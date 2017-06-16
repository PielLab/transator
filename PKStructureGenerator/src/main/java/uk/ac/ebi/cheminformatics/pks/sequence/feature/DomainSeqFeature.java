package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.generator.PostProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.MonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.NoActionMonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLine;

import java.util.Optional;

public class DomainSeqFeature extends AbstractSeqFeature {

    PostProcessor postProcessor;

    private Optional<Double> eValue;

    private Double threshold = Double.parseDouble("1E-3");

    private FeatureFileLine featureFileLine;

    public DomainSeqFeature(FeatureFileLine featureFileLine) {
        super(featureFileLine.getStart(), featureFileLine.getStop(), featureFileLine.getName());
        this.eValue = parseEValue(featureFileLine.getEvalue());
        this.featureFileLine = featureFileLine;
    }

    private Optional<Double> parseEValue(String string) {
        try {
            return Optional.of(Double.parseDouble(string));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public DomainSeqFeature(Integer start, Integer stop, String name, String label) {
        super(start, stop, name);
    }

    @Override
    public boolean hasPostProcessor() {
        return postProcessor != null;
    }

    @Override
    public PostProcessor getPostProcessor() {
        return postProcessor;
    }

    @Override
    public MonomerProcessor getMonomerProcessor() {
        return new NoActionMonomerProcessor();
    }

    public boolean isSignificant() {
        return eValue.map(value -> value < threshold).orElse(false);
    }

    @Override
    public FeatureFileLine getOriginatingFeatureFileLine() {
        return featureFileLine;
    }

}
