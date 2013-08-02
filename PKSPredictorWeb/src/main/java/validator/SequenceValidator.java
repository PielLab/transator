package validator;

import com.google.common.io.Files;
import org.biojava3.core.sequence.ProteinSequence;
import org.biojava3.core.sequence.io.FastaReaderHelper;
import org.biojava3.core.sequence.io.FastaWriterHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 28/6/13
 * Time: 14:24
 * To change this template use File | Settings | File Templates.
 */
public class SequenceValidator {
    private List<String> sequenceIdentifiers;

    private String outputPath;
    private String fastaPath;

    private Exception e;

    /**
     * Initializes the sequence validator with the given input stream to the file.
     *
     * @param fastaFileInputStream
     */
    public SequenceValidator(InputStream fastaFileInputStream) {
        this.sequenceIdentifiers = new ArrayList<String>();
        processInput(fastaFileInputStream);
    }

    private void processInput(InputStream input) {
        try {
            Map<String,ProteinSequence> data = FastaReaderHelper.readFastaProteinSequence(input);
            outputPath = Files.createTempDir().getCanonicalPath();
            fastaPath = outputPath + File.separator + "query.faa";
            sequenceIdentifiers.addAll(data.keySet());
            cleanIdentifiers(sequenceIdentifiers);
            FastaWriterHelper.writeProteinSequence(new File(fastaPath),data.values());
            writeIdentifiersFile(sequenceIdentifiers);
        } catch (Exception e) {
            this.e = e;
        }
    }

    private void writeIdentifiersFile(List<String> sequenceIdentifiers) {
        String pathIdents = outputPath + File.separator + "query.identifiers";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(pathIdents));
            for (String ident : sequenceIdentifiers) {
                writer.write(ident+"\n");
            }
            writer.close();
        } catch (IOException e1) {
            throw new RuntimeException("problems when writing identifiers",e1);
        }
    }

    private void cleanIdentifiers(List<String> sequenceIdentifiers) {
        for (String ident : sequenceIdentifiers) {
            String newIdent = ident.replaceAll("\\s.*$","");
            sequenceIdentifiers.set(sequenceIdentifiers.indexOf(ident),newIdent);
        }
    }

    public SequenceValidator(String[] sequenceLines) {

    }

    public String getFastaPath() {
        return fastaPath;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public List<String> getSequenceIdentifiers() {
        return sequenceIdentifiers;
    }

    public boolean fail() {
        return this.e!=null;
    }

    public String getError() {
        return "";
    }
}
