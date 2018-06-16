package cocktailMaker.ui.controllers;

import cocktailMaker.server.db.entities.CocktailLog;
import cocktailMaker.ui.controllers.templates.GuiceInjectedController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class IngredientsLogController extends GuiceInjectedController{

    @FXML
    public TableView<Map.Entry<String, Integer>> log_table;

    @FXML
    public TableColumn<Map.Entry<String, Integer>, String> ingredient_column;

    @FXML
    public TableColumn<Map.Entry<String, Integer>, String> amount_column;

    @FXML
    public DatePicker from_date;

    @FXML
    public DatePicker to_date;

    @FXML
    public Button search_button;

    ObservableList<Map.Entry<String, Integer>> ingredientsObservableList = FXCollections.observableArrayList();

    public void initialize() {
        createTableBindings();
        setDefaultSearchValues();
        addEventHandlers();
    }

    private void setDefaultSearchValues() {
        from_date.setValue(LocalDate.now());
        to_date.setValue(LocalDate.now());
    }

    private void createTableBindings() {
        log_table.setItems(ingredientsObservableList);
        ingredient_column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Integer>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, Integer>, String> param) {
                return new SimpleStringProperty(param.getValue().getKey());
            }
        });

        amount_column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, Integer>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, Integer>, String> param) {
                return new SimpleStringProperty(param.getValue().getValue().toString());
            }
        });
    }

    private void addEventHandlers() {

        search_button.addEventHandler(ActionEvent.ACTION, event -> {
            LocalDate from = from_date.getValue();
            LocalDate to = to_date.getValue();

            List<CocktailLog> logEntries = dao.getIngredientsLog(from, to);

            Map<String, Integer> ingredientsAmountMap = new HashMap<>();

            for (CocktailLog logEntry : logEntries) {
                String ingredientsString = logEntry.getMessage();

                for (String ingredientAmountPair : ingredientsString.split(";")) {
                    String[] parts = ingredientAmountPair.split(":");
                    String ingredient = parts[0];
                    Integer amount = Integer.valueOf(parts[1]);
                    addToMap(ingredientsAmountMap, ingredient, amount);
                }
            }

            ingredientsObservableList.clear();
            ingredientsObservableList.addAll(ingredientsAmountMap.entrySet());
        });
    }

    private void addToMap(Map<String, Integer> ingredientsAmountMap, String ingredient, Integer amount) {
        if (ingredientsAmountMap.containsKey(ingredient)) {
            ingredientsAmountMap.replace(ingredient, ingredientsAmountMap.get(ingredient) + amount);
        } else {
            ingredientsAmountMap.put(ingredient, amount);
        }
    }

}
