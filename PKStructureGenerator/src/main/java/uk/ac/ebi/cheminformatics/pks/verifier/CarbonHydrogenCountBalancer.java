package uk.ac.ebi.cheminformatics.pks.verifier;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Balances the implicit hydrogen count with the sum of bond order sums of the bonds of the carbon
 * on the specified molecule.
 */
public class CarbonHydrogenCountBalancer {

    public void balanceImplicitHydrogens(IAtomContainer molecule, IAtom atom) {
        if(atom.getSymbol().equals("C")) {
            atom.setImplicitHydrogenCount(4-(int)molecule.getBondOrderSum(atom));
        }
    }

}
