package cocktailMaker.server;

import cocktailMaker.guice.MainModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

/**
 * Created by yasen.marinov on 6/19/2017.
 */
@Singleton
public class PageNavigator {

    private final Logger logger = Logger.getLogger(PageNavigator.class);

    public static final String PAGE_CONFIGURE_DISPENSERS = "views/configureDispensers.fxml";
    public static final String PAGE_CONFIGURE_USERS = "views/configureUsers.fxml";
    public static final String PAGE_CONFIGURE_INGREDIENTS = "views/configureIngredients.fxml";
    public static final String PAGE_CONFIGURE_COCKTAIL_GROUPS = "views/configureCocktailGroups.fxml";
    public static final String PAGE_CONFIGURE_COCKTAILS = "views/configureCocktails.fxml";
    public static final String PAGE_COCKTAIL_LOG = "views/cocktail_log.fxml";
    public static final String PAGE_INGREDIENTS_LOG = "views/ingredients_log.fxml";
    public static final String PAGE_HOME_ADMIN = "views/home_admin.fxml";
    public static final String PAGE_LOGIN = "views/login.fxml";

    public static final String PAGE_MAKE_COCKTAIL = "views/make_cocktail.fxml";
    public static final String PAGE_REFILL = "views/refill.fxml";

    private Stage stage;
    private Injector injector;

    PageNavigator() {
        logger.debug("Constructor called");
    }

    public void navigateTo(String page) {
        try {
//            Injector injector = Guice.createInjector(new MainModule());

            FXMLLoader loader = new FXMLLoader(ServerLauncher.class.getClassLoader().getResource(page));
            loader.setControllerFactory(injector::getInstance);

//            Parent root = loader.load(ServerLauncher.class.getClassLoader().getResource(page));
            Parent root = loader.<Parent>load();
            if (stage.getScene() == null) {
                String appCss = "css/application.css";
                Scene scene = new Scene(root, 800, 480);
                scene.getStylesheets().add(ServerLauncher.class.getClassLoader().getResource(appCss).toExternalForm());
                stage.setScene(scene);
            } else {
                stage.getScene().setRoot(root);
            }
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setInjector(Injector injector) {
        this.injector = injector;
    }
}
