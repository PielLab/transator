package router;

import org.apache.log4j.Logger;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import prediction.PKSPredictionDataResource;
import prediction.PKSStructureImageDataRes;
import prediction.PKSStructureSmilesDataRes;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 1/7/13
 * Time: 12:25
 * To change this template use File | Settings | File Templates.
 */
public class PKSRouter extends Application {

    private static final Logger LOGGER = Logger.getLogger( PKSRouter.class );

    /**
     * Creates a root Restlet that will receive all incoming calls.
     */
    @Override
    public synchronized Restlet createInboundRoot() {
        // Create a router Restlet that routes each call to a new instance of HelloWorldResource.
        Router router = new Router(getContext());

        // Defines only one route
        router.attach("/pkspredictor/query", PKSPredictionDataResource.class); // {encPath}/{seqID}
        router.attach("/pkspredictor/structure", PKSStructureImageDataRes.class);

        // Set up router for getting a smiles string
        router.attach("/pkspredictor/smiles", PKSStructureSmilesDataRes.class);

        return router;
    }

}
