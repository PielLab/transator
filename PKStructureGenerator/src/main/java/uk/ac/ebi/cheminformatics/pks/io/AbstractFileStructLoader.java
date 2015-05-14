package uk.ac.ebi.cheminformatics.pks.io;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.io.FileReader;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 12/5/15
 * Time: 23:55
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractFileStructLoader {

    String path;
    String cladeName;
    String errorLabel = "Could not read molecule for PKS Clade monomer: ";

    public IAtomContainer loadStructure() {
        try {
            MDLV2000Reader reader =
                    new MDLV2000Reader(new FileReader(this.path));
            IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newInstance(IAtomContainer.class));
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
            CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance()).addImplicitHydrogens(mol);
            return mol;
        } catch (CDKException |IOException e) {
            throw new RuntimeException(errorLabel+cladeName,e);
        } catch (NullPointerException e) {
            return SilentChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        }
    }
}
