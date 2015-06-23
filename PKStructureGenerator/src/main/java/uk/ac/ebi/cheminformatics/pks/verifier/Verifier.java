package uk.ac.ebi.cheminformatics.pks.verifier;

import uk.ac.ebi.cheminformatics.pks.generator.PKStructure;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 11/6/15
 * Time: 23:29
 * To change this template use File | Settings | File Templates.
 */
public interface Verifier {

    /**
     * Runs implemented verification, producing true if the error searched for
     * is present.
     *
     * @param struc
     * @return true if the feature is present.
     */
    public boolean verify(PKStructure struc);

    public String descriptionMessage();

}
