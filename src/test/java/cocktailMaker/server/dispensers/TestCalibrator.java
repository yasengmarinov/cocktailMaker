package cocktailMaker.server.dispensers;

import cocktailMaker.server.db.entities.Dispenser;
import cocktailMaker.server.db.entities.Ingredient;
import cocktailMaker.server.dispensers.controllers.MockPumpController;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TestCalibrator extends TestCase {

    @InjectMocks
    Calibrator calibrator;

    @Mock
    DispenserControllerManager dispenserControllerManager;

    @Mock
    MockPumpController mockPumpController;

    @Mock
    Dispenser dispenser;

    @Mock
    Ingredient ingredient;

    @Before
    public void setUp() {
        calibrator = new Calibrator(dispenserControllerManager);
        calibrator.setDispenser(dispenser);
        calibrator.setIngredient(ingredient);

        when(dispenserControllerManager.getDispenserController(Mockito.anyInt())).thenReturn(mockPumpController);
        when(dispenser.getId()).thenReturn(1);
        when(ingredient.getName()).thenReturn("ingredient");
        calibrator.init();
    }

    @Test
    public void testInit() {}

    @Test
    public void testRun() {
        verify(mockPumpController, never()).run();
        verify(mockPumpController, never()).stop();

        calibrator.start();
        verify(mockPumpController).run();
        calibrator.stop();
        verify(mockPumpController).stop();
    }
}
