package uk.ac.ebi.cheminformatics.pks.sequence.feature;

import uk.ac.ebi.cheminformatics.pks.annotation.CladeAnnotation;
import uk.ac.ebi.cheminformatics.pks.annotation.CladeAnnotationFactory;
import uk.ac.ebi.cheminformatics.pks.generator.PostProcessorFactory;
import uk.ac.ebi.cheminformatics.pks.monomer.PKMonomer;
import uk.ac.ebi.cheminformatics.pks.parser.FeatureFileLineParser;

import static java.lang.Integer.parseInt;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 19/4/15
 * Time: 22:13
 * To change this template use File | Settings | File Templates.
 */
public class KSDomainSeqFeature extends DomainSeqFeature implements SequenceFeature {

    private final int ranking;
    private final int stackNumber;

    public int getRanking() {
        return ranking;
    }

    public int getStackNumber() {
        return stackNumber;
    }

    public KSDomainSeqFeature(FeatureFileLineParser parser) {
        super(parser);
        String clade = parser.getName();
        ranking = parseInt(parser.getRanking());
        stackNumber = parseInt(parser.getStackNumber());
        setUpMonomer(clade);
    }

    private void setUpMonomer(String clade) {
        CladeAnnotation annotation = CladeAnnotationFactory.getInstance();
        this.monomer = new PKMonomer(clade, annotation);
        this.postProcessor = PostProcessorFactory.getPostProcessor(clade, annotation);
    }
}
