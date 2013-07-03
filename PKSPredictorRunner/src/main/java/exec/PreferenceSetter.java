package exec;

import runner.PKSPredictor;
import runner.RunnerPreferenceField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 28/6/13
 * Time: 16:51
 * To change this template use File | Settings | File Templates.
 */
public class PreferenceSetter {

    private Preferences pref;

    public PreferenceSetter() {
        pref = Preferences.userNodeForPackage(PKSPredictor.class);
    }

    public void set(Enum prefField, String value) {
        pref.put(prefField.toString(),value);
    }

    public void flush() {
        try {
            pref.flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        PreferenceSetter setter = new PreferenceSetter();
        for (RunnerPreferenceField field : RunnerPreferenceField.values()) {
            System.out.println("Set value for "+field.toString()+" [Y/n] :");
            String line = reader.readLine();
            if(line.toLowerCase().equals("y")) {
                System.out.print(field.toString() + " : ");
                line = reader.readLine();
                setter.set(field,line);
            }
        }
        setter.flush();
    }


}
