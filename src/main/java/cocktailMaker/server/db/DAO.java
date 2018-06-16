package cocktailMaker.server.db;

import cocktailMaker.server.db.entities.*;

import java.time.LocalDate;
import java.util.List;

public interface DAO {
    void init();

    <T> List<T> getAll(Class<T> clazz);

    boolean persist(Object object);

    boolean update(Object object);

    boolean delete(Object object);

    boolean persistCocktailIngredient(CocktailIngredient cocktailIngredient);
    User getUser(String username);

    User getUser(String username, String password);

    User getUserByCard(String card);

    List<CocktailIngredient> getCocktailIngredients(Cocktail cocktail);

    Dispenser getDispenserByIngredient(Ingredient ingredient);

    Dispenser getDispenserByCocktailIngredient(CocktailIngredient cocktailIngredient);

    List<Dispenser> getEnabledDispensers();

    List<CocktailLog> getCocktailLog();

    List<CocktailLog> getIngredientsLog(LocalDate from, LocalDate to);

    List<Cocktail> getCocktailsByGroup(CocktailGroup cocktailGroup);
}
