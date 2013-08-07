package uk.ac.ebi.cheminformatics.pks.generator;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 5/8/13
 * Time: 17:20
 * To change this template use File | Settings | File Templates.
 */
public class Clade3PostProcessor implements PostProcessor {
    @Override
    public void process(PKStructure structure, PKMonomer monomer) {
        //Connect C atom connected to R6 to C atom connected to R1 two modules upstream. R6 goes away.
        Integer currentIndex = structure.getMonomerIndex(monomer);
        PKMonomer upstream2 = structure.getMonomer(currentIndex - 2);
        IBond r6toCbond = getR6toCBond(monomer);
        IAtom cR1upstream2 = upstream2.getPreConnectionAtom();
        if(r6toCbond!=null) {
            Integer r6Index = r6toCbond.getAtom(0) instanceof IPseudoAtom ? 0 : 1;
            IAtom r6 =  r6toCbond.getAtom(r6Index);
            //IAtom cBoundR6 = r6toCbond.getConnectedAtom(r6);

            r6toCbond.setAtom(cR1upstream2,r6Index);
            structure.getMolecule().removeAtom(r6);
            monomer.getMolecule().removeAtom(r6);
        }

        //Connect C atom connected to R5 to C atom connected to R2 two modules upstream. R5 goes away.
        IBond r5toCbond = getR5toCBond(monomer);
        IAtom cR2upstream2 = upstream2.getPosConnectionAtom();
        if(r5toCbond!=null) {
            Integer r5Index = r5toCbond.getAtom(0) instanceof IPseudoAtom ? 0 : 1;
            IAtom r5 = r5toCbond.getAtom(r5Index);
            structure.getMolecule().removeBond(r5toCbond);
            monomer.getMolecule().removeBond(r5toCbond);
            structure.getMolecule().removeAtom(r5);
            monomer.getMolecule().removeAtom(r5);
            //IAtom cBoundR5 = r5toCbond.getConnectedAtom(r5);

            r5toCbond.setAtom(cR2upstream2,r5Index);
            structure.getMolecule().addBond(r5toCbond);
            monomer.getMolecule().addBond(r5toCbond);
        }
        //Connect C atom connected to R2 two modules upstream to C atom connected to R1 one module upstream.
        PKMonomer upstream1 = structure.getMonomer(currentIndex - 1);
        IAtom cR1upstream1 = upstream1.getPreConnectionAtom();
        IBond bond_CR2ups2_CR1ups1 = SilentChemObjectBuilder.getInstance().newInstance(IBond.class);
        bond_CR2ups2_CR1ups1.setAtoms(new IAtom[]{cR2upstream2,cR1upstream1});
        bond_CR2ups2_CR1ups1.setOrder(IBond.Order.SINGLE);
        structure.getMolecule().addBond(bond_CR2ups2_CR1ups1);

        //Connect C atom connected to R1 of upstream module to C atom connected to R2 of upstream module.
        // R4 and R3 go away.
        IBond bond_CR1ups1_CR2ups1 = SilentChemObjectBuilder.getInstance().newInstance(IBond.class);
        IAtom cR2upstream1 = upstream1.getPosConnectionAtom();
        bond_CR1ups1_CR2ups1.setAtoms(new IAtom[]{cR1upstream1,cR2upstream1});
        bond_CR1ups1_CR2ups1.setOrder(IBond.Order.SINGLE);
        structure.getMolecule().addBond(bond_CR1ups1_CR2ups1);
    }

    private IBond getR6toCBond(PKMonomer monomer) {
        IAtomContainer struct = monomer.getMolecule();
        for (IAtom atom : struct.atoms()) {
            if(atom instanceof IPseudoAtom && ((IPseudoAtom)atom).getLabel().equalsIgnoreCase("R6") ) {
                List<IBond> bonds = struct.getConnectedBondsList(atom);
                if(bonds.size()==1) {
                    return bonds.get(0);
                }
            }
        }
        return null;
    }

    private IBond getR5toCBond(PKMonomer monomer) {
        IAtomContainer struct = monomer.getMolecule();
        for (IAtom atom : struct.atoms()) {
            if(atom instanceof IPseudoAtom && ((IPseudoAtom)atom).getLabel().equalsIgnoreCase("R5") ) {
                List<IBond> bonds = struct.getConnectedBondsList(atom);
                if(bonds.size()==1) {
                    return bonds.get(0);
//                    for (IBond bond : bonds) {
//                        int pseudoAtoms = 0;
//                        for (IAtom atomInBond : bond.atoms()) {
//                            if(atomInBond instanceof IPseudoAtom)
//                                pseudoAtoms++;
//                        }
//                        if(pseudoAtoms==1) {
//                            return bond;
//                        }
//                    }
                }
            }
        }
        return null;
    }
}
