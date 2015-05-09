package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.monomer.MonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.NoActionMonomerProcessor;
import uk.ac.ebi.cheminformatics.pks.monomer.PKMonomer;
import uk.ac.ebi.cheminformatics.pks.generator.PostProcessor;
import uk.ac.ebi.cheminformatics.pks.generator.PostProcessorFactory;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLineParser;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 4/7/13
 * Time: 11:09
 * To change this template use File | Settings | File Templates.
 */
public class DomainSeqFeature extends AbstractSeqFeature implements SequenceFeature {

    PostProcessor postProcessor;

    public DomainSeqFeature(FeatureFileLineParser parser) {
        super(parser.getStart(),parser.getStop(),parser.getName());
        this.monomer = new PKMonomer(parser.getName());
        this.postProcessor = PostProcessorFactory.getPostProcessor(parser.getName());
    }

    public DomainSeqFeature(Integer start, Integer stop, String name, String evalue) {
        super(start,stop,name);
        this.monomer = new PKMonomer(name);
        this.postProcessor = PostProcessorFactory.getPostProcessor(name);
    }

    @Override
    public boolean hasPostProcessor() {
        return postProcessor!=null;
    }

    @Override
    public PostProcessor getPostProcessor() {
        return postProcessor;
    }

    @Override
    public MonomerProcessor getMonomerProcessor() {
        return new NoActionMonomerProcessor();
    }
}
