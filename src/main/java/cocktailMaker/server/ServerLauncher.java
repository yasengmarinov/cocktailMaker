package cocktailMaker.server;

import cocktailMaker.guice.MainModule;
import cocktailMaker.server.config.ServerConfigurator;
import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

public class ServerLauncher extends Application {

    private static final Logger logger = Logger.getLogger(ServerLauncher.class.getName());

    public static void main(String[] args) {

        logger.info("Starting application");
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Injector injector = Guice.createInjector(new MainModule());

        ServerConfigurator serverConfigurator = injector.getInstance(ServerConfigurator.class);
        serverConfigurator.setStage(primaryStage);
        serverConfigurator.setInjector(injector);
        serverConfigurator.configure();
    }

    @Override
    public void stop() {
        Platform.exit();
        System.exit(0);
    }
}
