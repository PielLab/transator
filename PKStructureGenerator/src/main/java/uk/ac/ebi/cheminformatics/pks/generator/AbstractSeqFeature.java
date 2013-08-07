package uk.ac.ebi.cheminformatics.pks.generator;

/**
 * Pattern sequence features stand for small patterns of amino acids in the PKS that signal particular
 * changes or modifications to the nascent PK chain. For this reason, they don't create a new PKMonomer,
 * but rather modify an existing one, according to their position.
 *
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 4/7/13
 * Time: 12:35
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractSeqFeature implements SequenceFeature {

    PKMonomer monomer;
    Integer start;
    Integer stop;
    String name;

    public AbstractSeqFeature(Integer start, Integer stop, String name) {
        this.start = start;
        this.stop = stop;
        this.name = name;
    }

    public PKMonomer getMonomer() {
        return monomer;
    }

    public String getName() {
        return name;
    }
}
