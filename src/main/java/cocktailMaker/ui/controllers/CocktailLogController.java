package cocktailMaker.ui.controllers;

import javafx.collections.FXCollections;

/**
 * Created by yasen.marinov on 7/8/2017.
 */
public class CocktailLogController extends LogController {

    @Override
    protected void setObservableList() {
        cocktailLogObservableList = FXCollections.observableArrayList(dao.getCocktailLog());
    }

}
