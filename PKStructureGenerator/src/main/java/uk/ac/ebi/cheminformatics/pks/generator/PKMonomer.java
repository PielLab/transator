package uk.ac.ebi.cheminformatics.pks.generator;

import org.apache.log4j.Logger;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 4/7/13
 * Time: 11:09
 * To change this template use File | Settings | File Templates.
 */
public class PKMonomer {

    private static final Logger LOGGER = Logger.getLogger(PKMonomer.class);

    private IAtom preConnectionAtom;
    private IBond preConnectionBond;
    private IAtom posConnectionAtom;

    private String cladeName;

    public boolean isNonElongating() {
        return isNonElongating;
    }

    private boolean isNonElongating =false;

    private IAtomContainer monomerMol;

    public PKMonomer(String name) {
        cladeName = name;
        this.monomerMol = loadStructure(name);
        setConnectionPoints();
        //if(this.monomerMol.getProperty("EXTENDER")!=null) {
        Set<String> nonElongatingClades = //new HashSet<>(Arrays.asList("Clade_27"));
                new HashSet<>(Arrays.asList("Clade_27", "Clade_8a", "Clade_8b",
                        "Clade_8", "Clade_10", "Clade_28"));
        if(nonElongatingClades.contains(name)) {
            this.isNonElongating =true;
        }
    }

    private IAtomContainer loadStructure(String name) {
        try {
            MDLV2000Reader reader =
                    new MDLV2000Reader(PKMonomer.class.getResourceAsStream("/uk/ac/ebi/cheminformatics/structures/"+name+".mol"));
            IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newInstance(IAtomContainer.class));
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
            CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance()).addImplicitHydrogens(mol);
            return mol;
        } catch (CDKException e) {
            throw new RuntimeException("Could not read molecule for "+cladeName,e);
        } catch (NullPointerException e) {
            return SilentChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        }
    }

    private void setConnectionPoints() {
        for(IAtom atom : monomerMol.atoms()) {
            if(atom instanceof IPseudoAtom) {
                if(((IPseudoAtom) atom).getLabel().equals("R1")) {
                    if(preConnectionAtom==null) {
                        preConnectionAtom =
                                monomerMol.getConnectedAtomsCount(atom) > 0 ? monomerMol.getConnectedAtomsList(atom).get(0) : null;
                        if(preConnectionAtom!=null)
                            preConnectionBond = monomerMol.getBond(atom,preConnectionAtom);
                    } else {
                        throw new RuntimeException("Molecule for  "+cladeName+" has more than one R1 atom");
                    }
                } else if(((IPseudoAtom) atom).getLabel().equals("R2")) {
                    if(posConnectionAtom==null) {
                        posConnectionAtom = monomerMol.getConnectedAtomsCount(atom) > 0 ? monomerMol.getConnectedAtomsList(atom).get(0) : null;
                    } else {
                        throw new RuntimeException("Molecule for  "+cladeName+" has more than one R1 atom");
                    }
                }
            }
        }
    }

    /**
     * Returns Atom connected to R2 originally.
     * @return
     */
    public IAtom getPosConnectionAtom() {
        return posConnectionAtom;
    }

    /**
     * Returns Atom connected to R1 originally.
     *
     * @return
     */
    public IAtom getPreConnectionAtom() {
        return preConnectionAtom;
    }

    public IAtomContainer getMolecule() {
        return monomerMol;
    }

    public IBond getConnectionBond() {
        return preConnectionBond;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PKMonomer pkMonomer = (PKMonomer) o;

        if (cladeName != null ? !cladeName.equals(pkMonomer.cladeName) : pkMonomer.cladeName != null) return false;
        if (posConnectionAtom != null ? !posConnectionAtom.equals(pkMonomer.posConnectionAtom) : pkMonomer.posConnectionAtom != null)
            return false;
        if (preConnectionAtom != null ? !preConnectionAtom.equals(pkMonomer.preConnectionAtom) : pkMonomer.preConnectionAtom != null)
            return false;
        if (preConnectionBond != null ? !preConnectionBond.equals(pkMonomer.preConnectionBond) : pkMonomer.preConnectionBond != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = preConnectionAtom != null ? preConnectionAtom.hashCode() : 0;
        result = 31 * result + (preConnectionBond != null ? preConnectionBond.hashCode() : 0);
        result = 31 * result + (posConnectionAtom != null ? posConnectionAtom.hashCode() : 0);
        result = 31 * result + (cladeName != null ? cladeName.hashCode() : 0);
        return result;
    }
}
