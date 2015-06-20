package uk.ac.ebi.cheminformatics.pks.verifier;

import org.openscience.cdk.interfaces.IBond;
import uk.ac.ebi.cheminformatics.pks.generator.PKStructure;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 11/6/15
 * Time: 23:31
 * To change this template use File | Settings | File Templates.
 */
public class MissingBondOrderVerifier implements Verifier {
    @Override
    public boolean verify(PKStructure struc) {
        for (IBond bond : struc.getMolecule().bonds()) {
            if(bond.getOrder()==null || bond.getOrder().equals(IBond.Order.UNSET))
                return true;
        }
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String descriptionMessage() {
        return "Bonds found with missing order";  //To change body of implemented methods use File | Settings | File Templates.
    }
}
