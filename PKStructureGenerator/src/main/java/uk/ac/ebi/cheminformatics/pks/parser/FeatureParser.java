package uk.ac.ebi.cheminformatics.pks.parser;

import uk.ac.ebi.cheminformatics.pks.annotation.CladeAnnotation;
import uk.ac.ebi.cheminformatics.pks.annotation.CladeAnnotationFactory;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.SequenceFeature;
import uk.ac.ebi.cheminformatics.pks.sequence.feature.SequenceFeatureFactory;

import java.io.*;
import java.util.*;

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
    private final Set<String> cladeForStackSelected = new HashSet<>();
    private final CladeAnnotation ANNOTATION = CladeAnnotationFactory.getInstance();

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
            String line;
            FeatureFileLineParser lineParser;
            while(true) {
                line = reader.readLine();
                if(line==null) {
                    reader.close();
                    return null;
                }
                lineParser = new FeatureFileLineParser(line);
                Boolean verificationPassed = lineParser.getVerificationPass();
                if(lineParser.isaKSClade()) {
                    if (cladeForStackSelected.contains(lineParser.getStackNumber()))
                        // if the stack has already been assigned, we continue to the next feature.
                        continue;

                    if (ANNOTATION.isVerificationMandatory(lineParser.getName())) {
                        if (verificationPassed != null && verificationPassed.booleanValue()) {
                            // if this is a clade (has a ranking), the verification for that clade is mandatory
                            // and that clade's verification passed, then we keep this feature.
                            break;
                        }
                    } else {
                        // if this is a clade (has a ranking) and its verification is not mandatory, we select the
                        // first we see, as it will be sorted by stack and ranking.
                        break;
                    }
                } else {
                    // feature is a pattern or a domain, we choose it if it passed verification or if verification was
                    // not needed.
                    if(verificationPassed != null && verificationPassed.booleanValue()) {
                        break;
                    } else if(verificationPassed == null) {
                        break;
                    }
                }
            }
            if(!lineParser.getStackNumber().equals("N/A")) {
                this.cladeForStackSelected.add(lineParser.getStackNumber());
            }
            return SequenceFeatureFactory.makeSequenceFeature(lineParser);
        } catch (IOException e) {
            throw new RuntimeException("Could not read features : ",e);
        }
    }
}
