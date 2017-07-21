package uk.ac.ebi.cheminformatics.pks.generator;

import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import org.openscience.cdk.interfaces.*;
import uk.ac.ebi.cheminformatics.pks.monomer.MonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.PKMonomer;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.KSDomainSeqFeature;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.SequenceFeature;
import uk.ac.ebi.cheminformatics.pks.verifier.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles the assembly of the polyketide molecule, through the subsequent processing of the sequence features read.
 * <p>
 * TODO Deal gracefully with clades placed in the middle that can only be placed at the start because they lack an R1.
 */
public class PKSAssembler {

    private static final Logger LOGGER = Logger.getLogger(PKSAssembler.class);

    private PKStructure structure;

    private List<SequenceFeature> toBePostProcessed;
    private List<SequenceFeature> subFeaturesForNextKS;
    private List<Verifier> verifiers;
    private CarbonHydrogenCountBalancer hydrogenCountBalancer;

    public PKSAssembler() {
        this.structure = new PKStructure();
        this.toBePostProcessed = new LinkedList<SequenceFeature>();
        this.subFeaturesForNextKS = new LinkedList<>();
        this.verifiers = new LinkedList<>();
        this.verifiers.addAll(
                Arrays.asList(
                        new MissingBondOrderVerifier(), new SingleConnectedComponentVerifier(),
                        new StereoElementsVerifier()));
        this.hydrogenCountBalancer = new CarbonHydrogenCountBalancer();
    }

    /**
     * Given a sequenceFeature, it adds the next monomer to the PKS structure. The monomer is obtained from the sequence
     * feature. According to the sub-features found upstream, modifications can be exerted on the monomer.
     *
     * @param sequenceFeature
     */
    public void addMonomer(SequenceFeature sequenceFeature) {
        if (!(sequenceFeature instanceof KSDomainSeqFeature)) {
            this.subFeaturesForNextKS.add(sequenceFeature);
            return;
        }
        // From here, we are only looking at KS domains seq features.
        if (sequenceFeature.getMonomer().isNonElongating()) {
            return;
        }
        LOGGER.info("Adding monomer " + sequenceFeature.getName());
        if (sequenceFeature.getMonomer().getMolecule().getAtomCount() == 0) {
            // empty molecule for advancing only
            return;
        }

        // TODO: something in the sub-feature processing of Cade_28 is broken
//        if (!sequenceFeature.getName().equals("Clade_28")) {
        processSubFeatures(sequenceFeature.getMonomer());
//        }

        if (structure.getMonomerCount() == 0) {
            // Starting nascent polyketide
            structure.add(sequenceFeature.getMonomer());
            IAtom posConnectionAtomMonomer = sequenceFeature.getMonomer().getPosConnectionAtom();
            IAtom preConnectionAtomMonomer = sequenceFeature.getMonomer().getPreConnectionAtom();
            for (IAtom atomToCorrect : Arrays.asList(posConnectionAtomMonomer, preConnectionAtomMonomer)) {
                // on the starter, some monomers might not have the pre connection atom
                if (atomToCorrect == null)
                    continue;
                hydrogenCountBalancer.balanceImplicitHydrogens(structure.getMolecule(), atomToCorrect);
            }
            runVerifiersForFeature(sequenceFeature, "initial part");
        } else {
            IAtom connectionAtomInChain = structure.getConnectionAtom();
            IBond connectionBondInMonomer = sequenceFeature.getMonomer().getConnectionBond();

            IAtomContainer structureMolecule = structure.getMolecule();

            removeGenericConnection(connectionAtomInChain, structureMolecule);

            IAtomContainer monomer = sequenceFeature.getMonomer().getMolecule();
            int indexToRemove = connectionBondInMonomer.getAtom(0) instanceof IPseudoAtom ? 0 : 1;

            // TODO: we cannot remove R1 from a pyran/furan ring as we get problems with verification.
            if (monomer.getConnectedAtomsCount(connectionBondInMonomer.getAtom(indexToRemove)) == 1) {
                monomer.removeAtom(connectionBondInMonomer.getAtom(indexToRemove));
            }

            connectionBondInMonomer.setAtom(connectionAtomInChain, indexToRemove);

            structure.add(sequenceFeature.getMonomer());

            // adjust implicit hydrogen atoms
            hydrogenCountBalancer.balanceImplicitHydrogens(structure.getMolecule(), connectionBondInMonomer.getAtom(0));
            hydrogenCountBalancer.balanceImplicitHydrogens(structure.getMolecule(), connectionBondInMonomer.getAtom(1));

            // here we do post processing specific to the particular clade just added
            if (sequenceFeature.hasPostProcessor()) {
                toBePostProcessed.add(sequenceFeature);
            }

            checkForBadlyFormattedStereo(sequenceFeature);

            runVerifiersForFeature(sequenceFeature, "after normal insertion");
        }
    }

    private void runVerifiersForFeature(SequenceFeature feature, String additionalMessage) {
        for (Verifier verifier : verifiers) {
            if (verifier.verify(structure)) {
                LOGGER.error(verifier.descriptionMessage() + " after " + feature.getName() + " " + additionalMessage);
            }
        }
    }


    /**
     * Deals with all the modifications that different domains upstream of the current KS
     * exert to the monomer added by this current KS.
     */
    private void processSubFeatures(PKMonomer monomer) {
        for (SequenceFeature feat : subFeaturesForNextKS) {
            MonomerProcessor processor = feat.getMonomerProcessor();
            processor.modify(monomer);
            runVerifiersForFeature(feat, "after processing sub-features.");
        }
        subFeaturesForNextKS.clear();
    }

    private void checkForBadlyFormattedStereo(SequenceFeature feature) {
        IAtomContainer mol = structure.getMolecule();
        List<IStereoElement> stereoElementsToDel = new LinkedList<>();
        for (IStereoElement element : mol.stereoElements()) {
            if (element instanceof IDoubleBondStereochemistry) {
                for (IBond bondInStereo : ((IDoubleBondStereochemistry) element).getBonds()) {
                    if (!structure.getMolecule().contains(bondInStereo)) {
                        LOGGER.info("Bond in stereo definition is not part of the molecule, after: " + feature.getName());
                        stereoElementsToDel.add(element);
                    }
                }
            }
        }
        if (!stereoElementsToDel.isEmpty()) {
            List<IStereoElement> existingElements = Lists.newArrayList(mol.stereoElements());
            existingElements.removeAll(stereoElementsToDel);
            mol.setStereoElements(existingElements);
        }
    }

    public void postProcess() {
        for (SequenceFeature toPP : this.toBePostProcessed) {
            PostProcessor proc = toPP.getPostProcessor();
            proc.process(structure, toPP.getMonomer());
            runVerifiersForFeature(toPP, "after post-processing");
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
    private IBond removeGenericConnection(IAtom connectionAtomInChain, IAtomContainer structureMol) {
        IAtom toRemoveA = null;
        for (IBond connected : structureMol.getConnectedBondsList(connectionAtomInChain)) {
            for (IAtom atomCon : connected.atoms()) {
                if (atomCon.equals(connectionAtomInChain))
                    continue;
                if (atomCon instanceof IPseudoAtom && ((IPseudoAtom) atomCon).getLabel().equals("R2")) {
                    toRemoveA = atomCon;
                    break;
                }
            }
        }
        IBond bondToRemove = null;
        if (toRemoveA != null) {
            //order = structureMol.getBond(connectionAtomInChain,toRemoveA).getOrder().ordinal();
            bondToRemove = structureMol.getBond(connectionAtomInChain, toRemoveA);
            structureMol.removeBond(bondToRemove);
            structureMol.removeAtom(toRemoveA);
        }
        return bondToRemove;
    }

    public PKStructure getStructure() {
        return structure;
    }

}
