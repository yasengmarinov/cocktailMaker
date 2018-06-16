package cocktailMaker.server.cocktail;

import cocktailMaker.guice.annotations.ServerProperties;
import cocktailMaker.server.db.DAO;
import com.google.inject.Inject;
import org.apache.log4j.Logger;
import cocktailMaker.server.db.entities.CocktailIngredient;
import cocktailMaker.server.db.entities.Dispenser;
import cocktailMaker.server.dispensers.DispenserControllerManager;
import cocktailMaker.server.dispensers.interfaces.DispenserController;

import java.util.Properties;
import java.util.concurrent.Callable;

/**
 * Created by yasen.marinov on 7/18/2017.
 */
public class IngredientPourTask implements Callable<Boolean> {
    private static final Logger logger = Logger.getLogger(IngredientPourTask.class.getName());
    private static final String MS_FOR_100_ML = "pour.ms";

    protected final DispenserControllerManager dispenserControllerManager;
    protected final DAO dao;
    protected final Properties properties;

    private CocktailIngredient cocktailIngredient;

    @Inject
    IngredientPourTask(DispenserControllerManager dispenserControllerManager, DAO dao,@ServerProperties Properties properties) {
        this.dispenserControllerManager = dispenserControllerManager;
        this.dao = dao;
        this.properties = properties;
    }

    public void setCocktailIngredient(CocktailIngredient cocktailIngredient) {
        this.cocktailIngredient = cocktailIngredient;
    }

    @Override
    public Boolean call() throws Exception {
        Dispenser dispenser = dao.getDispenserByCocktailIngredient(cocktailIngredient);
//        Long msToRun = (long) cocktailIngredient.getIngredient().getVelocity() * cocktailIngredient.getMillilitres() / 100;
        Long msToRun = (long) Long.valueOf(properties.getProperty(MS_FOR_100_ML)) * cocktailIngredient.getMillilitres() / 100;
        DispenserController dispenserController = dispenserControllerManager.getDispenserController(dispenser.getId());

        logger.info(String.format("Begin pouring %s on Dispenser %d for %d ms", cocktailIngredient.getIngredient().getName(),
                dispenser.getId(), msToRun));
        try {
            dispenserController.run();
            Thread.sleep(msToRun);
            dispenserController.stop();

            dispenser.setMillilitresLeft(dispenser.getMillilitresLeft() - cocktailIngredient.getMillilitres());
            dao.update(dispenser);
            logger.info(String.format("Availability of %s on dispenser %d reduced to %d", cocktailIngredient.getIngredient(),
                    dispenser.getId(), dispenser.getMillilitresLeft()));
        } catch (InterruptedException e) {
            logger.error(e);
            return false;
        }

        return true;
    }

}