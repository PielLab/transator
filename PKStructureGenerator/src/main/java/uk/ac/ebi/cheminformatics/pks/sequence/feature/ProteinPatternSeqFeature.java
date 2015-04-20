package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.monomer.PKMonomer;
import uk.ac.ebi.cheminformatics.pks.generator.PostProcessor;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 2/8/13
 * Time: 09:16
 * To change this template use File | Settings | File Templates.
 */
public class ProteinPatternSeqFeature extends AbstractSeqFeature implements SequenceFeature {
    public ProteinPatternSeqFeature(Integer start, Integer stop, String name, String evalue) {
        super(start,stop,name);
        this.monomer = new PKMonomer("");
        // this protein pattern should include flags that tell the following KS what to do regarding its sequence.
    }

    @Override
    public boolean hasPostProcessor() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PostProcessor getPostProcessor() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
