package cocktailMaker.ui.controllers;

import cocktailMaker.ui.controllers.templates.SimpleAddRemovePage;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import cocktailMaker.server.db.entities.CocktailGroup;


/**
 * Created by yasen.marinov on 7/5/2017.
 */
public class ConfigureCocktailGroupController extends SimpleAddRemovePage {

    @FXML
    public TableColumn<CocktailGroup, String> cocktailGroup_column;

    @Override
    protected void configureTableColumns() {
        cocktailGroup_column.setCellValueFactory(new PropertyValueFactory<CocktailGroup, String>("name"));
    }

    @Override
    protected boolean addObject() {
        return dao.persist(new CocktailGroup(newObjectName.getText()));
    }

    @Override
    protected void setClass() {
        this.T = CocktailGroup.class;
    }
}
