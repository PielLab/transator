package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.generator.PostProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.MonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.NoActionMonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.PKMonomer;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLine;


public class ProteinPatternSeqFeature extends AbstractSeqFeature {


    private final FeatureFileLine featureFileLine;

    public ProteinPatternSeqFeature(FeatureFileLine featureFileLine) {
        super(featureFileLine.getStart(), featureFileLine.getStop(), featureFileLine.getName());
        this.monomer = new PKMonomer("");
        this.featureFileLine = featureFileLine;
    }

    @Override
    public boolean hasPostProcessor() {
        return false;
    }

    @Override
    public PostProcessor getPostProcessor() {
        return null;
    }

    @Override
    public MonomerProcessor getMonomerProcessor() {
        return new NoActionMonomerProcessor();
    }

    @Override
    public boolean isSignificant() {
        return true;
    }

    @Override
    public FeatureFileLine getOriginatingFeatureFileLine() {
        return this.featureFileLine;
    }

}
