package uk.ac.ebi.cheminformatics.pks.generator;

import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.interfaces.IAtomContainer;

import javax.vecmath.Point2d;

public class SimpleMoleculeRotator {

    /**
     * Rotates the given molecule with coordinates to maximize its horizontal disposition
     *
     * @param moleculeWithCoordinates
     */
    public void rotateMolecule(IAtomContainer moleculeWithCoordinates) {
        double[] widthHeight = GeometryUtil.get2DDimension(moleculeWithCoordinates);
        Point2d center = GeometryUtil.get2DCenter(moleculeWithCoordinates);

        double currentMaxWidth = widthHeight[0];
        double completeRotation = 135;
        double angleStep = 2;
        double currentAngle = 0;
        double maxAngle = 0;
        while (currentAngle < completeRotation) {
            currentAngle += angleStep;
            GeometryUtil.rotate(moleculeWithCoordinates, center, angleStep);
            double width = GeometryUtil.get2DDimension(moleculeWithCoordinates)[0];
            if (width > currentMaxWidth) {
                maxAngle = currentAngle;
                currentMaxWidth = width;
            }
        }
        GeometryUtil.rotate(moleculeWithCoordinates, center, -1 * (currentAngle - maxAngle));

        System.out.println("Angle for best rotation is " + maxAngle);
    }

}
