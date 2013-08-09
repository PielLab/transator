package runner;

import java.io.*;
import java.util.prefs.Preferences;


/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 28/6/13
 * Time: 15:17
 * To change this template use File | Settings | File Templates.
 */
public class PKSPredictor implements Runnable {

    private Preferences prefs;
    private String command;
    private String outPath;

    public PKSPredictor(String fastaPath, String outputPath) {
        StringBuilder builder = new StringBuilder();
        prefs = Preferences.userNodeForPackage(PKSPredictor.class);
        outPath = outputPath;
        if(!outPath.endsWith(File.separator))
            outPath = outPath+File.separator;
        builder.append(getPref(RunnerPreferenceField.PythonPath)).
                append(File.separator).
                append("python ").
                append(getPref(RunnerPreferenceField.ScriptPath)).append("runQueryAgainstHMMModels.py ");
        builder.append(fastaPath+" ").append(getPref(RunnerPreferenceField.HMMERModelPath)+" ").append(outPath+" ")
                .append(getPref(RunnerPreferenceField.HMMERPath)+" ").append(getPref(RunnerPreferenceField.FuzzProPath))
                .append(" allDomains").append(" > ").append(outPath+"run.log"+" 2>&1");
        command = builder.toString();
    }

    private String getPref(RunnerPreferenceField field) {
        return prefs.get(field.toString(), "");
    }

    @Override
    public void run() {
        Process pksPredProc;
        try {

            if(getPref(RunnerPreferenceField.UseCluster).length()>0) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(outPath+"runJob.sh"));
                writer.write(command);
                writer.close();

                StringBuilder builder = new StringBuilder();
                builder.append("bsub -q research-rh6 -cwd ")
                        .append(outPath).append(" -o ").append("runJob.o")
                        .append(" -e ").append("runJob.err").append(outPath).append("runJob.sh");
            } else {
                pksPredProc = Runtime.getRuntime().exec(command);
                writeToFile(pksPredProc.getInputStream(), outPath+"run.out");
                writeToFile(pksPredProc.getErrorStream(), outPath+"run.err");
            }

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void writeToFile(InputStream stream, String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        String line;
        while ((line = reader.readLine())!=null) {
            writer.write(line + "\n");
        }
        reader.close();
        writer.close();
    }
}
