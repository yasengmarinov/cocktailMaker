package cocktailMaker.server.dispensers;

import cocktailMaker.server.db.DAO;
import cocktailMaker.server.db.entities.Dispenser;
import cocktailMaker.server.dispensers.interfaces.DispenserController;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class TestDispenserControllerManager extends TestCase {

    @InjectMocks
    DispenserControllerManager dispenserControllerManager;

    @Mock
    DAO dao;

    @Before
    public void setUp() {
        Properties properties = new Properties();
        properties.setProperty("testMode", "true");
        properties.setProperty("dispenser.1.pin", "0");
        properties.setProperty("dispenser.2.pin", "1");
        dispenserControllerManager = new DispenserControllerManager(dao, properties);

        List<Dispenser> dispensers = new LinkedList<>();
        dispensers.add(new Dispenser());

        when(dao.getAll(Dispenser.class)).thenReturn(dispensers);
        dispenserControllerManager.init();
    }

    @Test
    public void testInit() {
    }

    @Test
    public void testGetDispenserController() {
        DispenserController controller = dispenserControllerManager.getDispenserController(1);
        Assert.assertNotNull(controller);
    }

    @Test
    public void testDispenserDBPersistence() {
        Dispenser dispenser = new Dispenser();
        dispenser.setId(1);
        dispenser.setEnabled(false);
        verify(dao).persist(dispenser);

        Dispenser dispenser2 = new Dispenser();
        dispenser2.setId(2);
        dispenser2.setEnabled(false);
        verify(dao).persist(dispenser2);

    }
}
