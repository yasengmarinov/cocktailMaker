package cocktailMaker.server.dispensers;

import com.google.inject.Inject;
import org.apache.log4j.Logger;
import cocktailMaker.server.db.entities.Dispenser;
import cocktailMaker.server.db.entities.Ingredient;
import cocktailMaker.server.dispensers.interfaces.DispenserController;

import java.time.Duration;
import java.time.Instant;

/**
 * Created by yasen.marinov on 7/17/2017.
 */
public class Calibrator {
    private static final Logger logger = Logger.getLogger(Calibrator.class.getName());

    protected Ingredient ingredient;
    protected Dispenser dispenser;
    protected Duration duration;

    protected Instant calibrationStart;
    protected DispenserController dispenserController;
    protected DispenserControllerManager dispenserControllerManager;

    @Inject
    Calibrator (DispenserControllerManager dispenserControllerManager) {
        this.dispenserControllerManager = dispenserControllerManager;
    }

    public void init() {
        if (ingredient == null || dispenser == null) {
            logger.error("Calibrator could not be created");
            return;
        }
        dispenserController = dispenserControllerManager.getDispenserController(dispenser.getId());
        logger.info("Calibrator created:");
        logger.info("Ingredient: " + ingredient.getId());
        logger.info("Dispenser: " + dispenser.getId());
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public void setDispenser(Dispenser dispenser) {
        this.dispenser = dispenser;
    }

    public Duration getDuration() {
        return duration;
    }

    public void start() {
        logger.info(String.format("Calibration of %s on Dispenser %s initiated", ingredient.getName(), dispenser.getId()));
        calibrationStart = Instant.now();
        dispenserController.run();
    }

    public void stop() {
        dispenserController.stop();
        duration = java.time.Duration.between(calibrationStart, Instant.now());
        logger.info(String.format("Calibration of %s on Dispenser %s completed in %02d:%03d",
                ingredient.getName(), dispenser.getId(), duration.getSeconds(), duration.getNano() / 1000000));
    }

}
