package runner;

import org.apache.log4j.Logger;

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

    private static final Logger LOGGER = Logger.getLogger(PKSPredictor.class);

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
                append(getPref(RunnerPreferenceField.PythonPath).length()>0 ? File.separator : "").
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
                File runJob = new File(outPath+"runJob.sh");
                BufferedWriter writer = new BufferedWriter(new FileWriter(runJob));
                writer.write(command);
                writer.close();
                runJob.setExecutable(true);

                StringBuilder builder = new StringBuilder();
                builder.append("bsub -q normal -cwd ")
                        .append(outPath).append(" -o ").append("run.out")
                        .append(" -e ").append("run.err ").append(outPath).append("runJob.sh");

                String clusterExecCommand = builder.toString();
                pksPredProc = Runtime.getRuntime().exec(clusterExecCommand);
                writeToFile(pksPredProc.getInputStream(), outPath+"runCluster.out");
                writeToFile(pksPredProc.getErrorStream(), outPath+"runCluster.err");
            } else {
                pksPredProc = Runtime.getRuntime().exec(command);
                writeToFile(pksPredProc.getInputStream(), outPath+"run.out");
                writeToFile(pksPredProc.getErrorStream(), outPath+"run.err");
            }

        } catch (IOException e) {
            LOGGER.error("Could produce the execution files",e);
            throw new RuntimeException("Could produce the execution files",e);
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
