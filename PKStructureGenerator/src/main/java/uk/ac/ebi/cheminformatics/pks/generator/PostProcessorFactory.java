package uk.ac.ebi.cheminformatics.pks.generator;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 5/8/13
 * Time: 17:18
 * To change this template use File | Settings | File Templates.
 */
public class PostProcessorFactory {
    public static PostProcessor getPostProcessor(String name) {
        if(name.equals("Clade_3")) {
            return new Clade3PostProcessor();
        }
        return null;
    }
}
