package uk.ac.ebi.cheminformatics.pks.generator;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.Molecule;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 4/7/13
 * Time: 11:09
 * To change this template use File | Settings | File Templates.
 */
public class PKMonomer {

    private IAtom preConnectionAtom;
    private IBond preConnectionBond;
    private IAtom posConnectionAtom;

    private String cladeName;

    private IAtomContainer monomerMol;

    public PKMonomer(String name) {
        cladeName = name;
        this.monomerMol = loadStructure(name);
        setConnectionPoints();
    }

    private IAtomContainer loadStructure(String name) {
        try {
            MDLV2000Reader reader =
                    new MDLV2000Reader(PKMonomer.class.getResourceAsStream("/uk/ac/ebi/cheminformatics/structures/"+name+".mol"));
            return reader.read(SilentChemObjectBuilder.getInstance().newInstance(IAtomContainer.class));
        } catch (CDKException e) {
            throw new RuntimeException("Could not read molecule for "+cladeName,e);
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

    public IAtom getPosConnectionAtom() {
        return posConnectionAtom;
    }

    public IAtomContainer getMolecule() {
        return monomerMol;
    }

    public IBond getConnectionBond() {
        return preConnectionBond;
    }
}
