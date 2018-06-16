package cocktailMaker.server.cocktail;

import cocktailMaker.server.db.DAO;
import com.google.inject.Inject;
import com.google.inject.Provider;
import javafx.scene.control.Alert;
import org.apache.log4j.Logger;
import cocktailMaker.server.Utils;
import cocktailMaker.server.db.entities.Cocktail;
import cocktailMaker.server.db.entities.CocktailIngredient;
import cocktailMaker.server.db.entities.Dispenser;
import cocktailMaker.server.db.entities.Ingredient;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yasen.marinov on 7/18/2017.
 */
public class CocktailMaker {
    private static final Logger logger = Logger.getLogger(CocktailMaker.class.getName());

    protected DAO dao;
    protected Provider<IngredientPourTask> ingredientPourTaskProvider;

    private static ExecutorService pool = Executors.newFixedThreadPool(4);

    @Inject
    public CocktailMaker(DAO dao, Provider<IngredientPourTask> ingredientPourTaskProvider) {
        this.dao = dao;
        this.ingredientPourTaskProvider = ingredientPourTaskProvider;
    }

    public boolean validate(Cocktail cocktail) {
        List<CocktailIngredient> cocktailIngredients = cocktail.getCocktailIngredients();

//        for (CocktailIngredient ingredient : cocktailIngredients) {
//            if (ingredient.getIngredient().getVelocity() == null) {
//                Utils.Dialogs.openAlert(Alert.AlertType.WARNING, Utils.Dialogs.TITLE_INGREDIENTS_UNAVAILABLE,
//                        String.format("%s is not calibrated",
//                                ingredient.getIngredient().getName()));
//            return false;
//            }
//        }

        Map<Ingredient, Integer> ingredientsAvailability = new HashMap<>();
        for (Dispenser dispenser : dao.getEnabledDispensers()) {
            if (!ingredientsAvailability.containsKey(dispenser.getIngredient())) {
                ingredientsAvailability.put(dispenser.getIngredient(), dispenser.getMillilitresLeft());
            } else {
                ingredientsAvailability.compute(dispenser.getIngredient(), (ingredient, integer) -> {
                    return Math.max(integer, dispenser.getMillilitresLeft());
                });
            }
        }

        for (CocktailIngredient cocktailIngredient : cocktailIngredients) {
            if (ingredientsAvailability.get(cocktailIngredient.getIngredient()) < cocktailIngredient.getMillilitres()) {
                Utils.Dialogs.openAlert(Alert.AlertType.INFORMATION, Utils.Dialogs.TITLE_INGREDIENTS_UNAVAILABLE,
                        String.format("Not enough %s. Please change the bottle and go to Refill page to update availability",
                                cocktailIngredient.getIngredient().getName()));
                return false;
            }
        }

        return true;
    }

    public void make(Cocktail cocktail) {

        logger.info("Begin making of cocktail " + cocktail.getName());

        List<Callable<Boolean>> tasks = new LinkedList<>();
        for (CocktailIngredient cocktailIngredient : cocktail.getCocktailIngredients()) {
            IngredientPourTask task = ingredientPourTaskProvider.get();
            task.setCocktailIngredient(cocktailIngredient);
            tasks.add(task);
        }

        try {
            pool.invokeAll(tasks);
        } catch (InterruptedException e) {
            logger.error(e);
        }
    }
}
