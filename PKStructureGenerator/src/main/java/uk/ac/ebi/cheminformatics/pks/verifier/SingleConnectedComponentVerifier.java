package uk.ac.ebi.cheminformatics.pks.verifier;

import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import uk.ac.ebi.cheminformatics.pks.generator.PKStructure;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 11/6/15
 * Time: 23:31
 * To change this template use File | Settings | File Templates.
 */
public class SingleConnectedComponentVerifier implements Verifier {
    @Override
    public boolean verify(PKStructure struc) {
        IAtomContainer mol = struc.getMolecule();
        return !ConnectivityChecker.isConnected(mol);
    }

    @Override
    public String descriptionMessage() {
        return "Disconnected components found";  //To change body of implemented methods use File | Settings | File Templates.
    }
}
