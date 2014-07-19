package uk.ac.ebi.cheminformatics.pks.generator;

import org.apache.log4j.Logger;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IPseudoAtom;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 3/7/13
 * Time: 21:37
 * To change this template use File | Settings | File Templates.
 */
public class PKSAssembler {

    private static final Logger LOGGER = Logger.getLogger(PKSAssembler.class);

    private PKStructure structure;

    private List<SequenceFeature> toBePostProcessed;

    public PKSAssembler() {
        this.structure = new PKStructure();
        this.toBePostProcessed = new LinkedList<SequenceFeature>();
    }

    /**
     * Given a sequenceFeature, it adds the next monomer to the PKS structure. The monomer is obtained from the sequence
     * feature.
     *
     * @param sequenceFeature
     */
    public void addMonomer(SequenceFeature sequenceFeature) {
        if(sequenceFeature.getMonomer().getMolecule().getAtomCount()==0) {
            // empty molecule for advancing only
            return;
        }
        else if(structure.getMonomerCount()==0) {
            structure.add(sequenceFeature.getMonomer());
            checkNumberOfConnectedComponents(sequenceFeature);
        }
        else
        {
            IAtom connectionAtomInChain = structure.getConnectionAtom();
            IBond connectioBondInMonomer = sequenceFeature.getMonomer().getConnectionBond();

            IAtomContainer structureMol = structure.getMolecule();

            int order = removeGenericConnection(connectionAtomInChain, structureMol);
            int orderNew = connectioBondInMonomer.getOrder().ordinal();

            int hydrogensToAdd = order - orderNew;

            IAtomContainer monomer = sequenceFeature.getMonomer().getMolecule();
            if(monomer.getAtomCount()>0) {
                int indexToRemove = connectioBondInMonomer.getAtom(0) instanceof IPseudoAtom ? 0 : 1;

                monomer.removeAtom(connectioBondInMonomer.getAtom(indexToRemove));
                connectioBondInMonomer.setAtom(connectionAtomInChain,indexToRemove);

                structure.add(sequenceFeature.getMonomer());

                // adjust implicit hydrogens
                for (int i=0;i<Math.abs(hydrogensToAdd);i++) {
                    int current = connectionAtomInChain.getImplicitHydrogenCount();
                    connectionAtomInChain.setImplicitHydrogenCount(current+1*Integer.signum(hydrogensToAdd));
                }
            }
            checkNumberOfConnectedComponents(sequenceFeature);

            // here we do post processing specific to the particular clade just added
            if(sequenceFeature.hasPostProcessor()) {
                toBePostProcessed.add(sequenceFeature);
            }
        }
    }

    private void checkNumberOfConnectedComponents(SequenceFeature feature) {
        IAtomContainer mol = structure.getMolecule();
        if(!ConnectivityChecker.isConnected(mol)) {
            LOGGER.error("Newest feature "+feature.getName()+" produced disconnection");
        }
    }

    public void postProcess() {
        for (SequenceFeature toPP : this.toBePostProcessed) {
            PostProcessor proc = toPP.getPostProcessor();
            proc.process(structure,toPP.getMonomer());
        }
    }


    /**
     * Removes the generic atom connected to the connectionAtomInChain, and the bond connecting them. Number of
     * hydrogens connected to the connectionAtomInChain is not modified. The order of the bond removed is obtained.
     *
     * @param connectionAtomInChain
     * @param structureMol
     * @return order of the bond removed.
     */
    private int removeGenericConnection(IAtom connectionAtomInChain, IAtomContainer structureMol) {
        IAtom toRemoveA=null;
        for (IBond connected : structureMol.getConnectedBondsList(connectionAtomInChain)) {
            for(IAtom atomCon : connected.atoms()) {
               if(atomCon.equals(connectionAtomInChain))
                   continue;
               if(atomCon instanceof IPseudoAtom && ((IPseudoAtom)atomCon).getLabel().equals("R2")) {
                   toRemoveA = atomCon;
                   break;
               }
            }
        }
        int order = 0;
        if(toRemoveA!=null) {
            order = structureMol.getBond(connectionAtomInChain,toRemoveA).getOrder().ordinal();
            structureMol.removeBond(connectionAtomInChain,toRemoveA);
            structureMol.removeAtom(toRemoveA);
        }
        return order;
    }

    public PKStructure getStructure() {
        return structure;
    }

}
