package uk.ac.ebi.cheminformatics.pks.generator;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 4/7/13
 * Time: 14:26
 * To change this template use File | Settings | File Templates.
 */
public interface PatternSeqFeature extends SequenceFeature {
    void setPKMonomer(PKMonomer monomer);
}
