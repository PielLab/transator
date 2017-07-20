package uk.ac.ebi.cheminformatics.pks.generator;

import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.aromaticity.ElectronDonation;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.AtomContainerRenderer;
import org.openscience.cdk.renderer.IRenderer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.SymbolVisibility;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.standard.StandardGenerator;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class PKStructureImageGenerator {

    private IRenderer chemicalMoleculeRenderer;
    private StructureDiagramGenerator structureGenerator;
    private final Aromaticity daylight =
            new Aromaticity(ElectronDonation.daylight(), Cycles.or(Cycles.all(), Cycles.all(6)));

    public PKStructureImageGenerator() {
        this.structureGenerator = new StructureDiagramGenerator();

        Font font = new Font("Verdana", Font.PLAIN, 14);

        List<IGenerator<IAtomContainer>> generators = new ArrayList<IGenerator<IAtomContainer>>();
        generators.add(new BasicSceneGenerator());
        generators.add(new StandardGenerator(font));
        this.chemicalMoleculeRenderer = new AtomContainerRenderer(generators, new AWTFontManager());

        // options
        RendererModel renderer2dModel = chemicalMoleculeRenderer.getRenderer2DModel();

        renderer2dModel.set(StandardGenerator.Visibility.class,
                SymbolVisibility.iupacRecommendationsWithoutTerminalCarbon());
    }

    public BufferedImage generateStructureImage(PKStructure pkMolecule, Dimension dimension) throws CDKException {
        calculateProperties(pkMolecule.getMolecule());
        IAtomContainer moleculeWithCoordinates = generateCoordinatesForMolecule(pkMolecule.getMolecule());
        SimpleMoleculeRotator rotator = new SimpleMoleculeRotator();
        rotator.rotateMolecule(moleculeWithCoordinates);

        BufferedImage image = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
        Rectangle2D bounds = new Rectangle2D.Double(0, 0,
                image.getWidth(),
                image.getHeight());
        Graphics2D g2 = image.createGraphics();
        g2.setBackground(Color.WHITE);
        g2.clearRect(0, 0, dimension.width, dimension.height);
        AWTDrawVisitor awtDrawVisitor = AWTDrawVisitor.forVectorGraphics(g2);
        chemicalMoleculeRenderer.paint(moleculeWithCoordinates, awtDrawVisitor, bounds, true);
        g2.dispose();
        return image;
    }

    private IAtomContainer generateCoordinatesForMolecule(IAtomContainer molecule) throws CDKException {
        molecule.setStereoElements(new ArrayList<>());
        structureGenerator.setMolecule(molecule, false);
        // use false to avoid cloning, this modifies the original molecule.
        structureGenerator.generateCoordinates();
        return structureGenerator.getMolecule();
    }

    private void assignColors(PKStructure pkMolecule) {

//        if (moleculeWithCoordinates.getProperty(SPECIFIC_BONDS) != null) {
//            Map<IChemObject, Color> bondColorMap = new HashMap<IChemObject, Color>();
//            Map<Integer, Color> bondIndexColourMap = (Map<Integer, Color>) moleculeWithCoordinates.getProperty(SPECIFIC_BONDS);
//            for (Map.Entry entry : bondIndexColourMap.entrySet()) {
//                Integer index = (Integer) entry.getKey();
//                Color color = (Color) entry.getValue();
//                bondColorMap.put(moleculeWithCoordinates.getBond(index.intValue()), color);
//            }
//            RendererModel renderer2DModel = chemicalMoleculeRenderer.getRenderer2DModel();
//            renderer2DModel.set(RendererModel.ColorHash.class, bondColorMap);
//            return;
//        }
//        if (moleculeWithCoordinates.getProperty(COLOR_ALL_BONDS) != null) {
//            RendererModel renderer2DModel = chemicalMoleculeRenderer.getRenderer2DModel();
//            renderer2DModel.set(BasicBondGenerator.DefaultBondColor.class, (Color) moleculeWithCoordinates.getProperty(COLOR_ALL_BONDS));
//        }
    }

    private void calculateProperties(IAtomContainer molecule) throws CDKException {
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance()).addImplicitHydrogens(molecule);
        //AtomContainerManipulator.convertImplicitToExplicitHydrogens(molecule);
        daylight.apply(molecule);
    }

}
