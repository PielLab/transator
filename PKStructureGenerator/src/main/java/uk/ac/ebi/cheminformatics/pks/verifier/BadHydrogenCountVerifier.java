package uk.ac.ebi.cheminformatics.pks.verifier;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import uk.ac.ebi.cheminformatics.pks.generator.PKStructure;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 21/6/15
 * Time: 21:58
 * To change this template use File | Settings | File Templates.
 */
public class BadHydrogenCountVerifier implements Verifier {

    Set<IAtom> seen;

    public BadHydrogenCountVerifier() {
        this.seen = new HashSet<>();
    }

    @Override
    public boolean verify(PKStructure struc) {
        for (IAtom atom : struc.getMolecule().atoms()) {
            if(atom.getSymbol().equals("C") && !seen.contains(atom)) {
                if(atom.getBondOrderSum()!=null && atom.getBondOrderSum()+atom.getImplicitHydrogenCount()>4.0d) {
                    this.seen.add(atom);
                    return true;
                }
            }
        }
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String descriptionMessage() {
        return "Carbon Atoms with bad hydrogen count found";  //To change body of implemented methods use File | Settings | File Templates.
    }
}
