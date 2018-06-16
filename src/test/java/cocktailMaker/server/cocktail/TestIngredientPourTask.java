package cocktailMaker.server.cocktail;

import cocktailMaker.server.db.DAO;
import cocktailMaker.server.db.entities.CocktailIngredient;
import cocktailMaker.server.db.entities.Dispenser;
import cocktailMaker.server.db.entities.Ingredient;
import cocktailMaker.server.dispensers.DispenserControllerManager;
import cocktailMaker.server.dispensers.interfaces.DispenserController;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Properties;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TestIngredientPourTask extends TestCase {

    @InjectMocks
    IngredientPourTask ingredientPourTask;

    @Mock
    DispenserControllerManager dispenserControllerManager;

    @Mock
    DAO dao;

    @Mock
    CocktailIngredient cocktailIngredient;

    @Mock
    Ingredient ingredient;

    @Mock
    DispenserController dispenserController;

    @Mock
    Dispenser dispenser;

    @Mock
    Properties properties;

    @Before
    public void setUp() {
        ingredientPourTask = new IngredientPourTask(dispenserControllerManager, dao, properties);
        ingredientPourTask.setCocktailIngredient(cocktailIngredient);
    }

    @Test
    public void testCall() throws Exception {
        int initialMillilitres = 200;
        int millilitresToPour = 100;

        when(dao.getDispenserByCocktailIngredient(cocktailIngredient)).thenReturn(dispenser);
        when(properties.getProperty(Mockito.anyString())).thenReturn("10");
        when(cocktailIngredient.getIngredient()).thenReturn(ingredient);
        when(cocktailIngredient.getMillilitres()).thenReturn(millilitresToPour);
//        when(ingredient.getVelocity()).thenReturn(10);
        when(dispenser.getId()).thenReturn(1);
        when(dispenser.getMillilitresLeft()).thenReturn(initialMillilitres);
        when(dispenserControllerManager.getDispenserController(1)).thenReturn(dispenserController);
        ingredientPourTask.call();

        verify(dispenserController).run();
        verify(dispenserController).stop();
        verify(dispenser).setMillilitresLeft(initialMillilitres - millilitresToPour);
    }
}
