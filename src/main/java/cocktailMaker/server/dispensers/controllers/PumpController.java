package cocktailMaker.server.dispensers.controllers;

import com.pi4j.io.gpio.*;
import org.apache.log4j.Logger;
import cocktailMaker.server.dispensers.DispenserConfig;
import cocktailMaker.server.dispensers.interfaces.DispenserController;

/**
 * Created by yasen.marinov on 7/9/2017.
 */
public class PumpController implements DispenserController {

    private static final Logger logger = Logger.getLogger(PumpController.class.getName());

    private final GpioPinDigitalOutput pinOutput;
    private final int id;
    private final boolean inverseCurrent;

    public PumpController(DispenserConfig config, boolean inverseCurrent) {
        this.id = config.getId();
        this.inverseCurrent = inverseCurrent;

        String pinName = String.format("GPIO %d", config.getPin());
        final GpioController gpio = GpioFactory.getInstance();
        Pin pin = RaspiPin.getPinByName(pinName);
        if (pin == null) {
            pinOutput = null;
            logger.error("Non-existing GPIO name " + pinName);
        } else {
            logger.info(String.format("Configuring pump %d with %s", id, pinName));
            pinOutput = gpio.provisionDigitalOutputPin(pin);
        }
        if (inverseCurrent) {
            logger.info("Setting output to high because of inverse current");
            pinOutput.high();
        }
    }

    @Override
    public void run() {
        logger.info(String.format("Pump %d started on pin %s", id, pinOutput.getName()));
        if (!inverseCurrent) {
            pinOutput.high();
        } else {
            pinOutput.low();
        }
    }

    @Override
    public void stop() {
        logger.info(String.format("Pump %d stopped on pin $s", id, pinOutput.getName()));
        if (!inverseCurrent) {
            pinOutput.low();
        } else {
            pinOutput.high();
        }
    }
}
