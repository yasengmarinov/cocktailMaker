package cocktailMaker.server.dispensers.controllers;

import org.apache.log4j.Logger;
import cocktailMaker.server.dispensers.DispenserConfig;
import cocktailMaker.server.dispensers.interfaces.DispenserController;

/**
 * Created by yasen.marinov on 7/9/2017.
 */
public class MockPumpController implements DispenserController {
    private static final Logger logger = Logger.getLogger(MockPumpController.class.getName());

    private int id;
    private String gpio;

    public MockPumpController(DispenserConfig config) {
        id = config.getId();
        gpio = String.format("GPIO_%d", config.getPin());
        System.out.printf("Creating dispencser with id %d with pin %s\n", id, gpio);
    }

    @Override
    public void run() {
        logger.info(gpio + " started");
    }

    @Override
    public void stop() {
        logger.info(gpio + " stopped");
    }
}
