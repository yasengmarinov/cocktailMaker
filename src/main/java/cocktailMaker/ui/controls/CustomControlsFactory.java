package cocktailMaker.ui.controls;

import cocktailMaker.ui.controls.objects.CocktailButton;
import cocktailMaker.ui.controls.objects.CocktailGroupButton;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import org.apache.log4j.Logger;
import cocktailMaker.server.db.entities.Cocktail;
import cocktailMaker.server.db.entities.CocktailGroup;

import java.io.IOException;

/**
 * Created by yasen.marinov on 7/8/2017.
 */
public class CustomControlsFactory {

    private static final Logger logger = Logger.getLogger(CustomControlsFactory.class.getName());


    public static CocktailGroupButton getCocktailGroupButton(CocktailGroup cocktailGroup) {
        String cocktailGroupButtonLocation = "views/elements/cocktailGroup_button.fxml";
        try {
            Button button = FXMLLoader.load(CustomControlsFactory.class.getClassLoader().getResource(cocktailGroupButtonLocation));
            CocktailGroupButton cocktailGroupButton = new CocktailGroupButton(button, cocktailGroup);
            return cocktailGroupButton;
        } catch (IOException e) {
            logger.info(e.toString());
        }
        return null;
    }

    public static CocktailButton getCocktailButton(Cocktail cocktail) {
        String cocktailButtonLocation = "views/elements/cocktail_button.fxml";
        try {
            Button button = FXMLLoader.load(CustomControlsFactory.class.getClassLoader().getResource(cocktailButtonLocation));
            CocktailButton cocktailButton = new CocktailButton(button, cocktail);
            return cocktailButton;
        } catch (IOException e) {
            logger.info(e.toString());
        }
        return null;
    }

}
