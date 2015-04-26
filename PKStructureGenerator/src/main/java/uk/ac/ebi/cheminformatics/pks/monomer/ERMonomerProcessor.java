package uk.ac.ebi.cheminformatics.pks.monomer;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

/**
 * ER domain signals that any double bond in backbone of the monomer to be added should be changed to a single bond.
 * This processor handles that.
 */
public class ERMonomerProcessor implements MonomerProcessor {

    @Override
    public void modify(PKMonomer monomer) {
        for(IBond bond : monomer.getMolecule().bonds()) {
            /**
             * TODO a better version of this would be to be limited between
             * C1 and C2, or them and the connecting atoms.
             */
            if(bond.getOrder().equals(IBond.Order.DOUBLE)
                    && bond.getAtom(0).getSymbol().equals("C")
                    && bond.getAtom(1).getSymbol().equals("C")) {
                bond.setOrder(IBond.Order.SINGLE);
                // add 1 hydrogen to both atoms in the bond.
                for(IAtom atom : bond.atoms()) {
                    atom.setImplicitHydrogenCount(atom.getImplicitHydrogenCount()+1);
                }
            }
        }
    }
}
