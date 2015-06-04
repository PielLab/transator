package uk.ac.ebi.cheminformatics.pks.monomer;

import org.apache.log4j.Logger;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IPseudoAtom;
import uk.ac.ebi.cheminformatics.pks.io.AminoAcidStructLoader;
import uk.ac.ebi.cheminformatics.pks.io.StructureLoader;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 10/5/15
 * Time: 00:58
 * To change this template use File | Settings | File Templates.
 */
public class NRPSMonomerProcessor implements MonomerProcessor {

    static final Logger LOGGER = Logger.getLogger(NRPSMonomerProcessor.class);
    String aminoAcid;

    public NRPSMonomerProcessor(String aminoAcid) {
        this.aminoAcid = aminoAcid;
    }

    @Override
    public void modify(PKMonomer monomer) {
        // Read mol file according for the amino acid,
        StructureLoader loader = new AminoAcidStructLoader(this.aminoAcid);
        try {
            IAtomContainer aaMol = loader.loadStructure();
            //Join K in amino acid mol file to the K in the Monomer

            IAtom K_monomer=null;
            IAtom N_monomer = null;
            IAtom c_conn_K_monomer=null;

            IAtom K_aa=null;
            IAtom L_aa = null;
            IAtom atom_conn_K_aa=null;

            for(IAtom atom_monomer : monomer.getMolecule().atoms()) {
                if(atom_monomer.getSymbol().equals("K")) {
                    K_monomer = atom_monomer;
                    c_conn_K_monomer = monomer.getMolecule().getConnectedAtomsList(atom_monomer).get(0);
                } else if(atom_monomer.getSymbol().equals("N")) {
                    N_monomer = atom_monomer;
                }
            }

            for(IAtom atom_aa : aaMol.atoms()) {
                if(atom_aa.getSymbol().equals("K")) {
                    K_aa = atom_aa;
                    atom_conn_K_aa = aaMol.getConnectedAtomsList(atom_aa).get(0);
                } else if(atom_aa instanceof IPseudoAtom && ((IPseudoAtom) atom_aa).getLabel().equals("L")) {
                    L_aa = atom_aa;
                }
            }

            if(K_aa!=null && K_monomer!=null) {
                IBond c_k_monomer = monomer.getMolecule().getBond(K_monomer,c_conn_K_monomer);
                IBond conn_k_aa   = aaMol.getBond(K_aa,atom_conn_K_aa);

                c_k_monomer.setOrder(conn_k_aa.getOrder());
                c_k_monomer.setAtom(c_conn_K_monomer,0);
                c_k_monomer.setAtom(atom_conn_K_aa,1);

                aaMol.removeAtom(K_aa);
                aaMol.removeBond(conn_k_aa);

                monomer.getMolecule().add(aaMol);
                monomer.getMolecule().removeAtom(K_monomer);

                //If L is present in the aminoacid, join it with N of the monomer.
                if(L_aa!=null && N_monomer!=null) {
                    IBond L_bond = monomer.getMolecule().getConnectedBondsList(L_aa).get(0);
                    IAtom atom_conn_L = monomer.getMolecule().getConnectedAtomsList(L_aa).get(0);
                    L_bond.setAtom(N_monomer,0);
                    L_bond.setAtom(atom_conn_L,1);
                    monomer.getMolecule().removeAtom(L_aa);
                }
            }

        } catch (Exception e) {
            LOGGER.error("Issues when processing monomer:",e);
        }
    }
}
