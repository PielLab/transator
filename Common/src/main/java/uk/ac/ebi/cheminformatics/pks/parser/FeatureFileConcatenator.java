package uk.ac.ebi.cheminformatics.pks.parser;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 1/8/13
 * Time: 17:13
 * To change this template use File | Settings | File Templates.
 */
public class FeatureFileConcatenator {

    private static final Logger LOGGER = Logger.getLogger(FeatureFileConcatenator.class);

    private InputStream inputStream;

    public FeatureFileConcatenator(String path) {
        //To change body of created methods use File | Settings | File Templates.
        // Look for the original fasta or a file that holds the order of the identifiers.
        List<String> identifiers = getIdentifiers(path);
        // Read sequentially the features for each identifiers, waiting until the files are all ready.
        StringBuilder builder = new StringBuilder();
        for (String identifier : identifiers) {
            if(identifierFilesAreReady(path,identifier)) {
                builder.append(getFeatureFileContent(path,identifier));
            }
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path + "concatenated.features"));
            writer.write(builder.toString());
            writer.close();
        } catch (IOException e) {
            LOGGER.error("Problems writing ",e);
        }
        inputStream = new ByteArrayInputStream(builder.toString().getBytes());
        // leave everything in a string buffer or something that can be streamed into the input stream.
    }

    private String getFeatureFileContent(String path, String identifier) {
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path + identifier+".features"));
            String line;
            while ((line=reader.readLine())!=null) {
                builder.append(line+"\n");
            }
            reader.close();
        } catch (IOException e) {
            LOGGER.error("Problems reading feature files for concat : ",e);
            throw new RuntimeException(e);
        }
        return builder.toString();
    }

    private boolean identifierFilesAreReady(String path, String identifier) {
        File identFin = new File(path + identifier +".finished");
        try {
            while(true) {
                if(identFin.exists())
                    return true;
                else
                    Thread.sleep(300);
            }
        } catch (InterruptedException e) {
            LOGGER.error("Problems waiting for identifier files...",e);
            throw new RuntimeException(e);
        }
    }

    private List<String> getIdentifiers(String path) {
        List<String> identifiers = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path + "query.identifiers"));
            String line;
            while ((line=reader.readLine())!=null) {
                identifiers.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return identifiers;
    }

    public InputStream getInputStream() {
        return inputStream;
    }
}
