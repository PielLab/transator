package uk.ac.ebi.cheminformatics.pks.generator;

import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.AtomContainerRenderer;
import org.openscience.cdk.renderer.IRenderer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.*;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import javax.vecmath.Point2d;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * This class receives a poliketyde structure to produce an image of it.
 *
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 1/8/13
 * Time: 11:37
 * To change this template use File | Settings | File Templates.
 */
public class PKStructureImageGenerator {

    private IRenderer chemicalMoleculeRenderer;
    private StructureDiagramGenerator structureGenerator;

    public PKStructureImageGenerator() {
        this.structureGenerator = new StructureDiagramGenerator();
        //this.chemicalMoleculeRenderer = new AtomContainerRenderer(Arrays.asList(new BasicSceneGenerator(),
        //        new BasicBondGenerator(), new BasicAtomGenerator()), new AWTFontManager());

        List<IGenerator<IAtomContainer>> generators = new ArrayList<IGenerator<IAtomContainer>>();
        generators.add(new BasicSceneGenerator());
        generators.add(new RingGenerator());
        generators.add(new ExtendedAtomGenerator()); // I assume that’s doing the –OH thingy you want!
        //generators.add(new AtomNumberGenerator());
        this.chemicalMoleculeRenderer = new AtomContainerRenderer(generators, new AWTFontManager());

        // options
        RendererModel renderer2dModel = chemicalMoleculeRenderer.getRenderer2DModel();
        renderer2dModel.set(RingGenerator.ShowAromaticity.class, false);
        //renderer2dModel.set(RingGenerator.MaxDrawableAromaticRing.class, 9);
        renderer2dModel.set(BasicSceneGenerator.UseAntiAliasing.class, true);
        renderer2dModel.set(BasicAtomGenerator.ShowExplicitHydrogens.class, true);
        renderer2dModel.set(BasicAtomGenerator.ShowEndCarbons.class, true);
        renderer2dModel.set(ExtendedAtomGenerator.ShowImplicitHydrogens.class, true);
    }

    public BufferedImage generateStructureImage(PKStructure pkMolecule, Dimension dimension) throws CDKException {

        calculateProperties(pkMolecule.getMolecule());
        IAtomContainer moleculeWithCoordinates = generateCoordinatesForMolecule(pkMolecule.getMolecule());
        Rectangle2D rect = GeometryTools.getRectangle2D(moleculeWithCoordinates);
        if(rect.getHeight() > rect.getWidth()) {
            // we rotate 90 degrees counter clockwise.
            Point2d center2D = GeometryTools.get2DCenter(moleculeWithCoordinates);
            GeometryTools.rotate(moleculeWithCoordinates,center2D,Math.PI/2);
        }
        assignColors(pkMolecule);

        BufferedImage image = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
        Rectangle2D bounds = new Rectangle2D.Double(0, 0,
                image.getWidth(),
                image.getHeight());
        Graphics2D g2 = image.createGraphics();
        g2.setBackground(Color.WHITE);
        g2.clearRect(0, 0, dimension.width, dimension.height);
        AWTDrawVisitor awtDrawVisitor = new AWTDrawVisitor(g2);
        chemicalMoleculeRenderer.paint(moleculeWithCoordinates, awtDrawVisitor, bounds, true);
        g2.dispose();
        return image;
    }

    private IAtomContainer generateCoordinatesForMolecule(IAtomContainer molecule) throws CDKException {
        structureGenerator.setMolecule(molecule);
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
            for (IAtom atom : molecule.atoms()) {
                if(!(atom.getSymbol().equalsIgnoreCase("C") || atom instanceof IPseudoAtom))
                    CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance()).addImplicitHydrogens(molecule,atom);
            }

            //AtomContainerManipulator.convertImplicitToExplicitHydrogens(molecule);
            CDKHueckelAromaticityDetector.detectAromaticity(molecule);
    }

}
