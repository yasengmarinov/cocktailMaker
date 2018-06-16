package cocktailMaker.server.dispensers;

/**
 * Created by yasen.marinov on 6/18/2017.
 */
public class DispenserConfig {
    private int id;
    private int pin;

    public DispenserConfig(int id, int pin) {
        this.id = id;
        this.pin = pin;
    }

    public int getId() {
        return id;
    }

    public int getPin() {
        return pin;
    }
}
