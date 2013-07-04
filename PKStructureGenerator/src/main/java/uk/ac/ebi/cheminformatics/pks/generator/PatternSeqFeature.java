package uk.ac.ebi.cheminformatics.pks.generator;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 4/7/13
 * Time: 14:26
 * To change this template use File | Settings | File Templates.
 */
public interface PatternSeqFeature extends SequenceFeature {

    /**
     * Sets the monomer that the implementing PatternSeqFeature will modify according to its rules. This will be the same
     * monomer later retrieved from this feature.
     *
     * @param monomer
     */
    void setPKMonomer(PKMonomer monomer);
}
