package runner;

import org.apache.log4j.Logger;
import uk.ac.ebi.cheminformatics.pks.PKSPreferences;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
        prefs = Preferences.userNodeForPackage(PKSPreferences.class);
        outPath = outputPath;
        if(!outPath.endsWith(File.separator))
            outPath = outPath+File.separator;
        builder.append(getPref(RunnerPreferenceField.PythonPath)).
                append(getPref(RunnerPreferenceField.PythonPath).length()>0 ? File.separator : "").
                append("python ").
                append(getPref(RunnerPreferenceField.ScriptPath)).append("runQueryAgainstHMMModels.py ");
        builder.append("-q ").append(fastaPath+" ")
                .append("-c ").append(getPref(RunnerPreferenceField.HMMERModelPath)+" ")
                .append("-o ").append(outPath+" ")
                .append("--HMMERPath=").append(getPref(RunnerPreferenceField.HMMERPath)+" ")
                .append("--FuzzProPath=").append(getPref(RunnerPreferenceField.FuzzProPath)+" ")
                .append("-a ");
        if(getPref(RunnerPreferenceField.HMMEROtherModelsPath).length()>0)
            builder.append("-m ").append(getPref(RunnerPreferenceField.HMMEROtherModelsPath)+" ");
        if(getPref(RunnerPreferenceField.NRPS2Path).length()>0)
            builder.append("--NRPS2Path=").append(getPref(RunnerPreferenceField.NRPS2Path)+" ");
        builder.append(" > ").append(outPath+"run.log"+" 2>&1");
        command = builder.toString();
        LOGGER.info("Python Command : "+command);
        System.out.println("Python Command : "+command);
    }

    private String getPref(RunnerPreferenceField field) {
        return prefs.get(field.toString(), "");
    }

    @Override
    public void run() {
        Process pksPredProc;
        Process cronJob;
        try {

            File cronFile = createCronJobFile();
            String cronCommand = "cron "+cronFile.getAbsolutePath();

            if(getPref(RunnerPreferenceField.UseCluster).length()>0) {
                File runJob = new File(outPath+"runJob.sh");
                BufferedWriter writer = new BufferedWriter(new FileWriter(runJob));
                writer.write(command+"\n");
                //writer.write(cronCommand+"\n");
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

            cronJob = Runtime.getRuntime().exec(cronCommand);
            writeToFile(cronJob.getInputStream(), outPath+"cronJob.out");
            writeToFile(cronJob.getErrorStream(), outPath+"cronJob.err");

        } catch (IOException e) {
            LOGGER.error("Could produce the execution files",e);
            throw new RuntimeException("Could produce the execution files",e);
        }
    }

    private File createCronJobFile() {
        try {
            File cronJobFile = new File(outPath+"cronJob.txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(cronJobFile));
            Calendar now = Calendar.getInstance();
            now.add(Calendar.HOUR,2);
            SimpleDateFormat format = new SimpleDateFormat("m h d M ");
            writer.write(format.format(now.getTime())+"* rm -rf "+outPath+"\n");
            writer.close();
            return cronJobFile;
        } catch (IOException e) {
            throw new RuntimeException("Could not create cron job file",e);
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
