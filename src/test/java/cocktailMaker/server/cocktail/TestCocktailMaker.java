package cocktailMaker.server.cocktail;

import cocktailMaker.server.db.DAO;
import com.google.inject.Provider;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

public class TestCocktailMaker extends TestCase {

    @InjectMocks
    CocktailMaker cocktailMaker;

    @Mock
    DAO dao;

    @Mock
    Provider<IngredientPourTask> provider;

    @Before
    public void setUp() {
        cocktailMaker = new CocktailMaker(dao, provider);
    }

    @Test
    public void testInit() {

    }

}
