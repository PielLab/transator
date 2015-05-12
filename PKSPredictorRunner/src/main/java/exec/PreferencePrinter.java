package exec;

import runner.PKSPredictor;
import runner.RunnerPreferenceField;

import java.util.prefs.Preferences;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 12/5/15
 * Time: 23:16
 * To change this template use File | Settings | File Templates.
 */
public class PreferencePrinter {
    public static void main(String[] args) {
        Preferences pref = Preferences.userNodeForPackage(PKSPredictor.class);
        StringBuilder builder = new StringBuilder();
        for (RunnerPreferenceField field : RunnerPreferenceField.values()) {
            String label = field.toString();
            String value = pref.get(field.toString(), "");
            System.out.print(label + " : ");
            System.out.println(value);

            if(builder.length()>0)
                builder.append(";");
            builder.append(label).append(":").append(value);
        }
        System.out.println("");
        System.out.println(builder.toString());
    }
}
