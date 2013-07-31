package uk.ac.ebi.cheminformatics.pks.generator;

import com.google.common.base.Joiner;
import org.junit.Test;
import org.openscience.cdk.io.MDLV2000Writer;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLineParser;

import java.io.FileWriter;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 31/7/13
 * Time: 10:41
 * To change this template use File | Settings | File Templates.
 */
public class PKSAssemblerTest {
    @Test
    public void testAddMonomer() throws Exception {
        FeatureFileLineParser parser = new FeatureFileLineParser(getCladeLine(100,1E-20,100,1,1,"Clade_6",""));
        SequenceFeature clade6 = SequenceFeatureFactory.makeSequenceFeature(parser);
        FeatureFileLineParser parser2 = new FeatureFileLineParser(getCladeLine(200,1E-20,100,1,1,"Clade_11",""));
        SequenceFeature clade11 = SequenceFeatureFactory.makeSequenceFeature(parser2);
        FeatureFileLineParser parser3 = new FeatureFileLineParser(getCladeLine(300,1E-20,100,1,1,"Clade_13",""));
        SequenceFeature clade13 = SequenceFeatureFactory.makeSequenceFeature(parser3);
        SequenceFeature clade6_2 = SequenceFeatureFactory.makeSequenceFeature(parser);

        PKSAssembler assembler = new PKSAssembler();
        assembler.addMonomer(clade6);
        assembler.addMonomer(clade11);
        assembler.addMonomer(clade13);
        assembler.addMonomer(clade6_2);

        PKStructure struc = assembler.getStructure();

        MDLV2000Writer writer = new MDLV2000Writer(new FileWriter("/tmp/pks.mol"));
        writer.write(struc.getMolecule());
        writer.close();
    }

    @Test
    public void testGetStructure() throws Exception {

    }

    private String getCladeLine(Integer start, Double evalue, Integer score, Integer ranking,
                                Integer stackNumber, String name, String label) {
        return Joiner.on("\t").join(start,start+100,evalue,score,ranking,stackNumber,"domain",name,label);
    }
}
