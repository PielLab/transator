package uk.ac.ebi.cheminformatics.pks.io;

import runner.RunnerPreferenceField;
import uk.ac.ebi.cheminformatics.pks.PKSPreferences;

import java.util.prefs.Preferences;

/**
 * Loads the mol file into an IAtomContainer for a monomer from a file in the user's file system
 * (as opposed to an internal file in the jar).
 */
public class MonomerStructLoader extends AbstractFileStructLoader implements StructureLoader {

    Preferences prefs = Preferences.userNodeForPackage(PKSPreferences.class);

    public MonomerStructLoader(String cladeName) {
        this.cladeName = cladeName;
        StringBuilder builder = new StringBuilder(prefs.get(RunnerPreferenceField.MonomerMolsPath.toString(),""));
        builder.append(cladeName).append(".mol");
        this.path = builder.toString();
    }
}
