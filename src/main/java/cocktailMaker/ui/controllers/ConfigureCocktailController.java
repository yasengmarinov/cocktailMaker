package cocktailMaker.ui.controllers;

import cocktailMaker.server.db.entities.Cocktail;
import cocktailMaker.server.db.entities.CocktailGroup;
import cocktailMaker.server.db.entities.CocktailIngredient;
import cocktailMaker.server.db.entities.Ingredient;
import cocktailMaker.ui.controllers.templates.SimpleAddRemovePage;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

/**
 * Created by yasen.marinov on 7/5/2017.
 */
public class ConfigureCocktailController extends SimpleAddRemovePage<Cocktail> {

    @FXML
    public TableColumn<Cocktail, String> cocktail_column;

    @FXML
    public TableColumn<Cocktail, CocktailGroup> cocktailGroup_column;

    @FXML
    public ComboBox<CocktailGroup> cocktailGroup_box;

    @FXML
    public TableView<CocktailIngredient> ingredients_table;

    @FXML
    public TableColumn<CocktailIngredient, String> ingredientName_column;

    @FXML
    public TableColumn<CocktailIngredient, String> ingredientMl_column;

    @FXML
    public Button addIngredient_button;

    @FXML
    public Button deleteIngredient_button;

    private ObservableList<CocktailIngredient> ingredientsObservableList = FXCollections.observableArrayList();

    public void initialize() {
        super.initialize();
        configureCocktailGroupDropdown();
        configureIngredientsTable();
        configureAddRemoveIngredientButtons();
    }

    private void configureCocktailGroupDropdown() {
        List<CocktailGroup> cocktailGroups = dao.getAll(CocktailGroup.class);
        cocktailGroup_box.setItems(FXCollections.observableArrayList(cocktailGroups));
    }

    private void configureAddRemoveIngredientButtons() {
        addIngredient_button.disableProperty().bind(isObjectSelected().not());
        addIngredient_button.addEventHandler(ActionEvent.ACTION, event -> {
            CocktailIngredient cocktail_ingredient = getCocktailIngredientByDialog();
            if (cocktail_ingredient != null) {
                dao.persistCocktailIngredient(cocktail_ingredient);
                refreshIngredientsTable();
            }
        });

        deleteIngredient_button.disableProperty().bind(ingredients_table.getSelectionModel().selectedItemProperty().isNull());
        deleteIngredient_button.addEventHandler(ActionEvent.ACTION, event -> {
            dao.delete(ingredients_table.getSelectionModel().getSelectedItem());
            refreshIngredientsTable();
        });
    }

    @Override
    protected void configureTableColumns() {
        cocktail_column.setCellValueFactory(new PropertyValueFactory<Cocktail, String>("name"));
        cocktailGroup_column.setCellValueFactory(new PropertyValueFactory<Cocktail, CocktailGroup>("cocktailGroup"));

        ingredientName_column.setCellValueFactory(new PropertyValueFactory<CocktailIngredient, String>("ingredient"));
        ingredientMl_column.setCellValueFactory(param -> {
            SimpleStringProperty simpleStringProperty = new SimpleStringProperty(param.getValue().getMillilitres() + " ml");
            return simpleStringProperty;
        });
    }

    @Override
    protected boolean addObject() {
        return dao.persist(new Cocktail(newObjectName.getText(), cocktailGroup_box.getSelectionModel().getSelectedItem()));
    }

    @Override
    protected boolean updateObject() {
        Cocktail cocktail = (Cocktail) object_table.getSelectionModel().getSelectedItem();
        cocktail.setName(newObjectName.getText());
        cocktail.setCocktailGroup(cocktailGroup_box.getValue());
        return dao.update(cocktail);
    }

    @Override
    protected void setClass() {
        this.T = Cocktail.class;
    }

    @Override
    protected BooleanBinding isReadyForAdd() {
        return Bindings.isNotEmpty(newObjectName.textProperty()).
                and(Bindings.isNotNull(cocktailGroup_box.valueProperty()));
    }

    @Override
    protected void clearInputFields() {
        newObjectName.clear();
        cocktailGroup_box.setValue(null);
    }

    private void configureIngredientsTable() {
        ingredients_table.setItems(ingredientsObservableList);
        refreshIngredientsTable();

        selectedObject.addListener(observable -> {
            refreshIngredientsTable();
        });
    }

    private void refreshIngredientsTable() {
        List<CocktailIngredient> list = dao.getCocktailIngredients
                ((Cocktail) selectedObject.getValue());
        ingredientsObservableList.clear();
        if (list != null) {
            ingredientsObservableList.addAll(list);
            FXCollections.sort(ingredientsObservableList);
        }
        ingredients_table.refresh();
    }

    private CocktailIngredient getCocktailIngredientByDialog() {

        Dialog<CocktailIngredient> dialog = addCocktailIngredientDialog();

        Optional<CocktailIngredient> newCocktailIngredient = dialog.showAndWait();
        if (newCocktailIngredient.isPresent()) {
            return newCocktailIngredient.get();
        } else {
            return null;
        }
    }

    private Dialog<CocktailIngredient> addCocktailIngredientDialog() {
        Cocktail selectedCocktail = (Cocktail) object_table.getSelectionModel().getSelectedItem();
        Dialog<CocktailIngredient> dialog = new Dialog<>();
        dialog.setTitle("Add Ingredient");
        dialog.setHeaderText("Add a new ingredient for cocktail " + selectedCocktail);

        ComboBox<Ingredient> ingredient = new ComboBox<>();
        ingredient.setPrefWidth(200);
        ingredient.setPromptText("Ingredient");
        ingredient.setItems(FXCollections.observableArrayList(dao.getAll(Ingredient.class)));

        TextField ingredientAmount = new TextField();
        ingredientAmount.setMaxWidth(150);
        ingredientAmount.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d+")) {
                ingredientAmount.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        ingredientAmount.setPromptText("Amount in ml");

        VBox vBox = new VBox();
        vBox.getChildren().add(ingredient);
        vBox.getChildren().add(ingredientAmount);

        dialog.getDialogPane().setContent(vBox);

        ButtonType buttonAdd = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(buttonCancel, buttonAdd);
        dialog.getDialogPane().lookupButton(buttonAdd).disableProperty().bind(
                Bindings.and(ingredient.valueProperty().isNotNull(), ingredientAmount.textProperty().isNotEmpty()).not()
        );
        dialog.setResultConverter(param -> {
            if (param == buttonAdd) {
                return new CocktailIngredient(selectedCocktail, ingredient.getValue(), Integer.valueOf(ingredientAmount.getText()));
            }

            return null;
        });
        return dialog;
    }

}
