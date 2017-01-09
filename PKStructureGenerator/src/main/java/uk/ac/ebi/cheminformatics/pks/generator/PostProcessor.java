package uk.ac.ebi.cheminformatics.pks.generator;

import uk.ac.ebi.cheminformatics.pks.monomer.PKMonomer;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 5/8/13
 * Time: 16:48
 * To change this template use File | Settings | File Templates.
 */
public interface PostProcessor {
    void process(PKStructure structure, PKMonomer monomer);
}
