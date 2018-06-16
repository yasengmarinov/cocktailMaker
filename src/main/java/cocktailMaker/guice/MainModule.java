package cocktailMaker.guice;

import cocktailMaker.guice.annotations.ServerProperties;
import cocktailMaker.server.PageNavigator;
import cocktailMaker.server.Utils;
import cocktailMaker.server.config.ServerConfigurator;
import cocktailMaker.server.db.DAO;
import cocktailMaker.server.db.SqlDAO;
import cocktailMaker.ui.controllers.LoginController;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MainModule extends AbstractModule{

    private final Logger logger = Logger.getLogger(MainModule.class);

    @Override
    protected void configure() {
        bind(ServerConfigurator.class);
        bind(PageNavigator.class);

        bind(LoginController.class);

        bind(DAO.class).to(SqlDAO.class);
    }

    @Provides @ServerProperties
    Properties provideServerProperties() {
        Properties serverProperties;

        if (System.getProperty("configFile") != null) {
            serverProperties = new Properties();
            try {
                serverProperties.load(new FileInputStream(new File(System.getProperty("configFile"))));
            } catch (IOException e) {
                e.printStackTrace();
            }
            logger.info("Non default location for server config");
        } else {
            String propertiesLocation = "config/server_config.properties";
            serverProperties = Utils.loadPropertiesFile(propertiesLocation);
        }
        return serverProperties;
    }

}
