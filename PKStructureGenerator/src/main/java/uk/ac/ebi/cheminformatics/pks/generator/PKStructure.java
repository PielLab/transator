package uk.ac.ebi.cheminformatics.pks.generator;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a growing PK chain.
 *
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 3/7/13
 * Time: 21:29
 * To change this template use File | Settings | File Templates.
 */
public class PKStructure {

    private IAtomContainer chain;
    private IAtom connectionAtom;
    private List<PKMonomer> monomers;

    public PKStructure() {
        this.monomers = new ArrayList<PKMonomer>();
        this.chain = SilentChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
    }

    /**
     * Returns the atom of the growing PK chain where the next monomer should be connected to. This atom will be connected
     * to a generic atom R2 which should be stripped before adding a monomer.
     *
     * @return connection atom.
     */
    protected IAtom getConnectionAtom() {
        return connectionAtom;
    }

    /**
     * Adds the monomer to PKStructure. The necessary steps of deleting generic atoms and linking underlying molecules
     * are handled by the {@link PKSAssembler} class.
     *
     * @param monomer to be added.
     */
    protected void add(PKMonomer monomer) {
        this.connectionAtom = monomer.getPosConnectionAtom();
        monomers.add(monomer);
        this.chain.add(monomer.getMolecule());
    }

    /**
     * Returns the growing chain in its current status, including generic atoms that have not been removed.
     *
     * @return current CDK object for the chain.
     */
    public IAtomContainer getMolecule() {
        return chain;
    }

    public int getMonomerCount() {
        return monomers.size();
    }
}
