package cocktailMaker.ui.controllers.templates;

import cocktailMaker.guice.annotations.ServerProperties;
import cocktailMaker.server.PageNavigator;
import cocktailMaker.server.db.DAO;
import cocktailMaker.server.dispensers.DispenserControllerManager;
import cocktailMaker.server.session.SessionManager;
import com.google.inject.Inject;

import java.util.Properties;

public class GuiceInjectedController {

    @Inject @ServerProperties protected Properties serverProperties;
    @Inject protected PageNavigator pageNavigator;
    @Inject protected DAO dao;
    @Inject protected SessionManager sessionManager;
    @Inject protected DispenserControllerManager dispenserControllerManager;

    public GuiceInjectedController() {
    }

    @Inject
    private GuiceInjectedController(@ServerProperties Properties properties, PageNavigator pageNavigator) {
        this.serverProperties = properties;
        this.pageNavigator = pageNavigator;

    }
}
