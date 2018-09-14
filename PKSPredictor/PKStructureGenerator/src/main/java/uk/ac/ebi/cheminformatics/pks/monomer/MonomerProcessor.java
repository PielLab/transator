package uk.ac.ebi.cheminformatics.pks.monomer;

/**
 * Process the monomer (local changes) to reflect the influence of sub features (domains other that KS
 * or NRPS). This should be used on the monomer prior to connecting it with the chain structure,
 * to incorporate any changes of the monomer into the chain.
 */
public interface MonomerProcessor {

    /**
     * Process the structure within the given monomer. This should be applied before connecting
     * the monomer to the chain.
     *
     * @param monomer
     */
    public void modify(PKMonomer monomer);
}
