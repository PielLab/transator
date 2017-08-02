package uk.ac.ebi.cheminformatics.pks.generator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import org.openscience.cdk.interfaces.*;
import uk.ac.ebi.cheminformatics.pks.annotation.CladeAnnotationFactory;
import uk.ac.ebi.cheminformatics.pks.monomer.MonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.PKMonomer;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.FinalSeqFeature;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.KSDomainSeqFeature;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.SequenceFeature;
import uk.ac.ebi.cheminformatics.pks.verifier.*;

import java.util.*;

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

    // this is required for the termination rules
    private static final Set<String> terminationRelevantDomains = ImmutableSet.of(
            "KR", "DH", "ER", "MT", "OMT", "CR", "Oxy", "B");

    private Set<String> domainTypesSinceLastElongatingKs = new HashSet<>();

    private List<Verifier> verifiers;
    private CarbonHydrogenCountBalancer hydrogenCountBalancer;

    public PKSAssembler() {
        this.structure = new PKStructure();
        this.toBePostProcessed = new LinkedList<>();
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

        // TODO: implement cis-AT PKS functionality
        if (sequenceFeature.getName().equals("Clade_32")) {
            return;
        }

        if (!((sequenceFeature instanceof KSDomainSeqFeature))) {
            this.subFeaturesForNextKS.add(sequenceFeature);
            // TODO: which domains to add here, and what type to have here?
            if (isTerminationRelevant(sequenceFeature)) {
                this.domainTypesSinceLastElongatingKs.add(sequenceFeature.getSubtype());
            }
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

        processSubFeatures(sequenceFeature.getMonomer());

        if (structure.getMonomerCount() == 0) {
            structure.add(sequenceFeature.getMonomer());
        } else {

            if (sequenceFeature.getMonomer().isTerminationBoundary()) {
                terminateAtBoundary();
            }

            growByMonomer(sequenceFeature.getMonomer());

            // here we do post processing specific to the particular clade just added
            if (sequenceFeature.hasPostProcessor()) {
                toBePostProcessed.add(sequenceFeature);
            }

            checkForBadlyFormattedStereo(sequenceFeature);
        }

        runVerifiersForFeature(sequenceFeature);

        if (!sequenceFeature.getMonomer().isNonElongating()) {
            this.domainTypesSinceLastElongatingKs.clear();
        }
    }

    private void terminateAtBoundary() {
        growByMonomer(findBoundaryMonomer());
        this.domainTypesSinceLastElongatingKs.clear();
    }

    private boolean isTerminationRelevant(SequenceFeature sequenceFeature) {
        return terminationRelevantDomains.contains(sequenceFeature.getSubtype());
    }

    private static Map<Set<String>, String> terminationRules = new HashMap<>();

    static {
        // TODO: add the other domain combinations
        ImmutableSet<String> betaHydroxy = ImmutableSet.of("KR");
        ImmutableSet<String> alphaBetaDoubleBonds = ImmutableSet.of("KR", "DH");
        ImmutableSet<String> reduced = ImmutableSet.of("KR", "DH", "ER");
        ImmutableSet<String> alphaMethylAlphaBetaDoubleBond = ImmutableSet.of("KR", "MT", "DH");

        // TODO: clade_names to mol files? directly molfiles would be nice too...
        // TODO: where to read this from?
        terminationRules.put(betaHydroxy, "Clade_78");
        terminationRules.put(alphaBetaDoubleBonds, "Clade_65");
        terminationRules.put(reduced, "Clade_25");
        terminationRules.put(alphaMethylAlphaBetaDoubleBond, "Clade_83");
    }

    private PKMonomer findBoundaryMonomer() {
        if (domainTypesSinceLastElongatingKs.size() == 0) {
            //  beta-keto
            return new PKMonomer("Clade_45", CladeAnnotationFactory.getInstance());
        }
        String cladeName = terminationRules.get(domainTypesSinceLastElongatingKs);
        return new PKMonomer(cladeName, CladeAnnotationFactory.getInstance());
    }

    private void growByMonomer(PKMonomer monomer) {
        IAtom connectionAtomInChain = structure.getConnectionAtom();
        IBond connectionBondInMonomer = monomer.getConnectionBond();

        IAtomContainer structureMolecule = structure.getMolecule();

        removeGenericConnection(connectionAtomInChain, structureMolecule);

        IAtomContainer monomerMolecule = monomer.getMolecule();
        int indexToRemove = connectionBondInMonomer.getAtom(0) instanceof IPseudoAtom ? 0 : 1;

        monomerMolecule.removeAtom(connectionBondInMonomer.getAtom(indexToRemove));

        connectionBondInMonomer.setAtom(connectionAtomInChain, indexToRemove);

        structure.add(monomer);

        // adjust implicit hydrogen atoms
        hydrogenCountBalancer.balanceImplicitHydrogens(structure.getMolecule(), connectionBondInMonomer.getAtom(0));
        hydrogenCountBalancer.balanceImplicitHydrogens(structure.getMolecule(), connectionBondInMonomer.getAtom(1));
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

    private void runVerifiersForFeature(SequenceFeature feature) {
        runVerifiersForFeature(feature, "");
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

    public void addFinalExtension() {

        // TODO: there is an edge case, when there is also a "NRPS" boundary at the end
        terminateAtBoundary();

        growByMonomer(new FinalSeqFeature().getMonomer());
    }

    public PKStructure getStructure() {
        return structure;
    }

}
