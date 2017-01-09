package uk.ac.ebi.cheminformatics.pks.verifier;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.IStereoElement;
import uk.ac.ebi.cheminformatics.pks.generator.PKStructure;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 11/6/15
 * Time: 23:31
 * To change this template use File | Settings | File Templates.
 */
public class StereoElementsVerifier implements Verifier {

    private List<IStereoElement> stereoElementsToDel;
    private PKStructure structure;

    @Override
    public boolean verify(PKStructure struc) {
        IAtomContainer mol = struc.getMolecule();
        this.stereoElementsToDel = new LinkedList<>();
        this.structure = struc;
        for(IStereoElement element : mol.stereoElements()) {
            if (element instanceof IDoubleBondStereochemistry) {
                for(IBond bondInStereo : ((IDoubleBondStereochemistry)element).getBonds() ) {
                    if(!struc.getMolecule().contains(bondInStereo)) {
                        stereoElementsToDel.add(element);
                    }
                }
            }
        }  //To change body of implemented methods use File | Settings | File Templates.
        return stereoElementsToDel.size()>0;
    }

    @Override
    public String descriptionMessage() {
        return "Unsynced stereo elements found";  //To change body of implemented methods use File | Settings | File Templates.
    }
}
