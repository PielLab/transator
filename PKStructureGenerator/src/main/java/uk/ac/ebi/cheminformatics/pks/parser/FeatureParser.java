package uk.ac.ebi.cheminformatics.pks.parser;

import uk.ac.ebi.cheminformatics.pks.generator.SequenceFeature;
import uk.ac.ebi.cheminformatics.pks.generator.SequenceFeatureFactory;

import java.io.*;
import java.util.Iterator;

/**
 * Parses the .feature file produced by the Python implementation that marks domains and patterns.
 *
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 3/7/13
 * Time: 21:30
 * To change this template use File | Settings | File Templates.
 */
public class FeatureParser implements Iterator<SequenceFeature>{

    private SequenceFeature nextSf;
    private BufferedReader reader;

    public FeatureParser(InputStream input) {
       reader = new BufferedReader(new InputStreamReader(input));
       nextSf = getNext();
    }

    public FeatureParser(String path) {
        try {
        reader = new BufferedReader(new FileReader(path));
        nextSf = getNext();
        } catch (IOException e) {
            throw new RuntimeException("Cannot open .features file "+path,e);
        }
    }

    @Override
    public boolean hasNext() {
        return nextSf!=null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SequenceFeature next() {
        SequenceFeature toRet = nextSf;
        nextSf = getNext();
        return toRet;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void remove() {
        nextSf = getNext();
    }

    public SequenceFeature getNext() {
        try {
            String line = reader.readLine();
            if(line==null)
                return null;
            FeatureFileLineParser parser = new FeatureFileLineParser(line);
            while(!(parser.getRanking().equals("1") || parser.getRanking().equals("N/A"))) {
                line = reader.readLine();
                if(line==null) {
                    reader.close();
                    return null;
                }
                parser = new FeatureFileLineParser(line);
            }
            return SequenceFeatureFactory.makeSequenceFeature(parser);
        } catch (IOException e) {
            throw new RuntimeException("Could not read features : ",e);
        }
    }
}
