package io;

import com.google.common.io.Files;
import runner.PKSPredictor;
import runner.RunnerPreferenceField;

import java.io.File;
import java.util.prefs.Preferences;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 13/8/13
 * Time: 16:15
 * To change this template use File | Settings | File Templates.
 */
public class TempDirMaker {

    private static Preferences prefs = Preferences.userNodeForPackage(PKSPredictor.class);
    private static final int TEMP_DIR_ATTEMPTS = 10000;

    /**
     * Produces a temporary directory, based on the google guava Files.createTempDir method. If the
     * {@link RunnerPreferenceField.TmpPath} is set, then the temporary directory is within this path and not the usual
     * system.io.tmpdir system property.
     *
     * @return a temporary directory.
     */
    public static synchronized File createTempDir() {
        if(prefs.get(RunnerPreferenceField.TmpPath.name(),"").length()>0) {
            // from google guava Files class:
            File baseDir = new File(prefs.get(RunnerPreferenceField.TmpPath.name(), ""));
            String baseName = System.currentTimeMillis() + "-";


            for (int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
                File tempDir = new File(baseDir, baseName + counter);
                if (tempDir.mkdir()) {
                    return tempDir;
                }
            }
            throw new IllegalStateException("Failed to create directory within "
                    + TEMP_DIR_ATTEMPTS + " attempts (tried "
                    + baseName + "0 to " + baseName + (TEMP_DIR_ATTEMPTS - 1) + ')');
        } else {
            return Files.createTempDir();
        }
    }

}
