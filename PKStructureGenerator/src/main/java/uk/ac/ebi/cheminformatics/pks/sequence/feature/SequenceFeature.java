package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.generator.PostProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.MonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.PKMonomer;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 3/7/13
 * Time: 21:31
 * To change this template use File | Settings | File Templates.
 */
public interface SequenceFeature {

    /**
     * Returns the PKMonomer associated with this sequence feature.
     * This PKMonomer in turns contains the structure of the monomer
     * associated to this sequence feature.
     *
     * @return PKMonomer with structure associated to this sequence feature.
     */
    public PKMonomer getMonomer();

    /**
     * The name of the sequence feature, which in the case of the clade would
     * correspond to the clade identifier.
     *
     * @return the name of the sequence feature.
     */
    public String getName();

    /**
     * Whether the is a post processor assigned to this sequence feature.
     *
     * @return true if there is a post processor assigned.
     */
    boolean hasPostProcessor();

    /**
     * Obtains the post processor (if any) associated to this clade.
     *
     * @return
     */
    PostProcessor getPostProcessor();

    /**
     * Sets the sub feature list with the provided collection. This is used to add
     * as sub features the a domains/patterns found before the KS.
     *
     * @param subfeatures to set.
     */
    void setSubFeatures(Collection<SequenceFeature> subfeatures);

    /**
     * Returns the monomer processor for this sequence feature. Sequence features that do not
     * require any monomer processing should return {@link uk.ac.ebi.cheminformatics.pks.monomer.NoActionMonomerProcessor}.
     *
     * @return the monomer processor for this sequence feature.
     */
    MonomerProcessor getMonomerProcessor();

    /**
     * If true, the seq has enough evidence (for example from a low e-score) to justify its inclusion in the final structure
     *
     * @return
     */
    boolean isSignificant();

}
