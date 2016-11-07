package prediction;

import encrypt.Encrypter;
import org.apache.log4j.Logger;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.restlet.data.MediaType;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import uk.ac.ebi.cheminformatics.pks.generator.PKStructure;
import uk.ac.ebi.cheminformatics.pks.generator.PKStructureImageGenerator;
import uk.ac.ebi.cheminformatics.pks.generator.StructureGenerator;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileConcatenator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 1/8/13
 * Time: 16:46
 * To change this template use File | Settings | File Templates.
 */
public class PKSStructureSmilesDataRes extends ServerResource {

    private static final Logger LOGGER = Logger.getLogger(PKSStructureSmilesDataRes.class);

    @Get("xml")
    public Representation represent() {
        String encryptedPath = getQuery().getValues("path");
        Encrypter encrypter = new Encrypter();
        System.out.println("Encrypted image data res : "+encryptedPath);
        String path = encrypter.decrypt(encryptedPath);
        if(!path.endsWith(File.separator))
            path += File.separator;

        FeatureFileConcatenator concatenator = new FeatureFileConcatenator(path);
        System.out.println("Concatenator ready");
        StructureGenerator generator = new StructureGenerator(concatenator.getInputStream());
        generator.run();
        PKStructure struct = generator.getStructure();
        System.out.println("PK structure generated");

        IAtomContainer mol = struct.getMolecule();
        SmilesGenerator sg = new SmilesGenerator();


        try {
            return new StringRepresentation(sg.create(mol));
        } catch (CDKException e) {
            e.printStackTrace();
        }
        return null;
    }
}
