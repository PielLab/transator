package uk.ac.ebi.cheminformatics.pks.generator;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 3/7/13
 * Time: 21:31
 * To change this template use File | Settings | File Templates.
 */
public interface SequenceFeature {

    public PKMonomer getMonomer();

    public String getName();

    boolean hasPostProcessor();

    PostProcessor getPostProcessor();
}
