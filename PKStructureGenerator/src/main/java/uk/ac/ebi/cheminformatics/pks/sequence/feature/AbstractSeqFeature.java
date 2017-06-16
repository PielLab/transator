package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.monomer.PKMonomer;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Pattern sequence features stand for small patterns of amino acids in the PKS that signal particular
 * changes or modifications to the nascent PK chain. For this reason, they don't create a new PKMonomer,
 * but rather modify an existing one, according to their position.
 */
public abstract class AbstractSeqFeature implements SequenceFeature {

    PKMonomer monomer;
    Integer start;
    Integer stop;
    String name;

    List<SequenceFeature> subFeatures;

    public AbstractSeqFeature(Integer start, Integer stop, String name) {
        this.start = start;
        this.stop = stop;
        this.name = name;
        this.subFeatures = new LinkedList<>();
    }

    public PKMonomer getMonomer() {
        return monomer;
    }

    public String getName() {
        return name;
    }

    @Override
    public void setSubFeatures(Collection<SequenceFeature> subfeatures) {
        this.subFeatures.clear();
        this.subFeatures.addAll(subfeatures);
    }
}
