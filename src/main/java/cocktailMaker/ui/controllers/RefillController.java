package cocktailMaker.ui.controllers;

import cocktailMaker.server.PageNavigator;
import cocktailMaker.server.db.entities.Dispenser;
import cocktailMaker.server.db.entities.Ingredient;
import cocktailMaker.ui.controllers.templates.GuiceInjectedController;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by yasen.marinov on 7/18/2017.
 */
public class RefillController extends GuiceInjectedController {

    public static final String BOTTLE_SIZES_PROPERTY = "bottle_sizes";
    @FXML
    public TableView<Dispenser> dispenser_table;

    @FXML
    public TableColumn<Dispenser, Integer> dispenserId_column;

    @FXML
    public TableColumn<Dispenser, Ingredient> ingredient_column;

    @FXML
    public TableColumn<Dispenser, String> available_column;

    @FXML
    public VBox refillSizes_box;

    @FXML
    public Button logOff_button;

    @FXML
    public Button back_button;

    @FXML
    public Button refill_button;

    ObservableList<Dispenser> dispenserObservableList = FXCollections.observableArrayList();
    Property<Dispenser> selectedProperty = new SimpleObjectProperty<>();
    ToggleGroup bottleSizesGroup = new ToggleGroup();

    public void initialize() {
        configureTableColumns();
        refreshDispenserList();
        createSizeButtons();

        dispenser_table.setItems(dispenserObservableList);
        dispenser_table.getSelectionModel().selectFirst();
        selectedProperty.bind(dispenser_table.getSelectionModel().selectedItemProperty());

        addObjectsVisibility();
        addEventHandlers();
    }

    private void addObjectsVisibility() {
        refill_button.disableProperty().bind(new BooleanBinding() {
            {
                super.bind(selectedProperty);
            }
            @Override
            protected boolean computeValue() {
                return selectedProperty.getValue() == null;
            }
        });
    }

    private void createSizeButtons() {

        List<Integer> bottleSizes = Arrays
                .stream(serverProperties.getProperty(BOTTLE_SIZES_PROPERTY).split(","))
                .map(Integer::valueOf)
                .collect(Collectors.toList());
        int defaultSize = bottleSizes.get(0);
        Collections.sort(bottleSizes);
        List<RadioButton> sizeButtons = new LinkedList<>();

        RadioButton defaultButton = new RadioButton();
        for (Integer size : bottleSizes) {
            RadioButton button = new RadioButton(size.toString());
            button.setUserData(size.toString());
            button.setPrefWidth(100);
            button.setFont(new Font(20));
            button.setToggleGroup(bottleSizesGroup);
            sizeButtons.add(button);
            if (size == defaultSize) {
                defaultButton = button;
            }
        }

        defaultButton.setSelected(true);

        refillSizes_box.getChildren().addAll(sizeButtons);

    }

    private void addEventHandlers() {
        logOff_button.addEventHandler(ActionEvent.ACTION, event -> {
            sessionManager.sessionInvalidate();
            pageNavigator.navigateTo(PageNavigator.PAGE_LOGIN);
        });

        back_button.addEventHandler(ActionEvent.ACTION, event -> {
            pageNavigator.navigateTo(PageNavigator.PAGE_MAKE_COCKTAIL);
        });

        refill_button.addEventHandler(ActionEvent.ACTION, event -> {
            Dispenser dispenser = selectedProperty.getValue();
            dispenser.setMillilitresLeft(Integer.valueOf(bottleSizesGroup.getSelectedToggle().getUserData().toString()));
            dao.update(dispenser);
            refreshDispenserList();
        });
    }

    private void refreshDispenserList() {
        dispenserObservableList.clear();
        dispenserObservableList.addAll(dao.getEnabledDispensers());
        dispenser_table.refresh();
    }

    protected void configureTableColumns() {
        dispenserId_column.setCellValueFactory(new PropertyValueFactory<Dispenser, Integer>("id"));
        ingredient_column.setCellValueFactory(new PropertyValueFactory<Dispenser, Ingredient>("ingredient"));
        available_column.setCellValueFactory(param -> {
            SimpleStringProperty stringProperty = new SimpleStringProperty(param.getValue().getMillilitresLeft() + " ml");
            return stringProperty;
        });
    }

}
