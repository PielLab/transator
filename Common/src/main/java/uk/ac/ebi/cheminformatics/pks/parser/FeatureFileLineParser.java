package uk.ac.ebi.cheminformatics.pks.parser;

import com.google.common.base.Splitter;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 3/7/13
 * Time: 23:05
 * To change this template use File | Settings | File Templates.
 */
public class FeatureFileLineParser {

    /**
     * The start of the feature in amino acid coordinates (referenced to the sequence).
     *
     * @return start in aa.
     */
    public Integer getStart() {
        return start;
    }

    /**
     * The stop of the feature in amino acid coordinates (references to the sequence).
     *
     * @return stop in aa.
     */
    public Integer getStop() {
        return stop;
    }

    /**
     * The e-value of the feature, if available. For features that do not have an evalue, the file
     * contains a "N/A" string.
     *
     * @return e-value as String or "N/A".
     */
    public String getEvalue() {
        return evalue;
    }

    public String getScore() {
        return score;
    }

    public String getRanking() {
        return ranking;
    }

    public String getStackNumber() {
        return stackNumber;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    private final Integer start;
    private final Integer stop;
    private final String evalue;
    private final String score;
    private final String ranking;
    private final String stackNumber;
    private final String type;
    private final String name;
    private final String label;

    /**
     * Parses a line of the .feature file provided by the python executable.
     *
     * @param line from .feature file.
     */
    public FeatureFileLineParser(String line) {
        Iterator<String> tokens = Splitter.on("\t").split(line).iterator();
        start = Integer.parseInt(tokens.next());
        stop = Integer.parseInt(tokens.next());
        evalue = tokens.next();
        score = tokens.next();
        ranking = tokens.next();
        stackNumber = tokens.next();
        type = tokens.next();
        name = tokens.next();
        label = tokens.next();
    }



}
