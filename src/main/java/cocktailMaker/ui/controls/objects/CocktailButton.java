package cocktailMaker.ui.controls.objects;

import javafx.scene.control.Button;
import cocktailMaker.server.db.entities.Cocktail;


/**
 * Created by yasen.marinov on 7/8/2017.
 */
public class CocktailButton {

    Cocktail cocktail;
    Button button;

    public CocktailButton(Button button, Cocktail cocktail) {
        this.button = button;
        this.button.setText(cocktail.getName());
        this.cocktail = cocktail;
    }

    public Button getButton() {
        return button;
    }

    public Cocktail getCocktail() {
        return cocktail;
    }

}
