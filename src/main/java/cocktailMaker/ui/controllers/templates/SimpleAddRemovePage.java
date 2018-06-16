package cocktailMaker.ui.controllers.templates;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import cocktailMaker.server.Utils;
import cocktailMaker.server.db.entities.interfaces.NamedEntity;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;

import static cocktailMaker.server.Utils.Dialogs.openAlert;

/**
 * Created by yasen.marinov on 6/10/2017.
 */
public abstract class SimpleAddRemovePage<T extends NamedEntity & Comparable<T>> extends GuiceInjectedController {

    private static final Logger logger = Logger.getLogger(SimpleAddRemovePage.class);
    @FXML
    public TableView<T> object_table;

    @FXML
    public TextField newObjectName;

    @FXML
    public Button add_button;

    @FXML
    public Button remove_button;

    @FXML
    public Button edit_button;

    @FXML
    public Button cancel_button;

    protected Class T;
    protected Property<T> selectedObject = new SimpleObjectProperty<>();

    ObservableList<T> observableList = FXCollections.observableArrayList();
    SimpleBooleanProperty editMode = new SimpleBooleanProperty(false);

    public void initialize() {
        setClass();
        selectedObject.bind(object_table.getSelectionModel().selectedItemProperty());
        configureTableColumns();
        setTableObjectAndFocus();
        setObjectsVisibility();
        addEventHandlers();
    }

    protected void setTableObjectAndFocus() {
        object_table.setItems(observableList);
        refreshObjectList();
        selectFirstElementInTable();
    }

    private void selectFirstElementInTable() {
        if (object_table.getItems().size() != 0)
            object_table.getSelectionModel().select(0);
        object_table.requestFocus();
    }

    protected abstract void configureTableColumns();

    protected void setObjectsVisibility() {
        add_button.textProperty().bind(new StringBinding() {
            {
                super.bind(editMode);
            }

            @Override
            protected String computeValue() {
                return (editMode.getValue() ? "Save" : "Add");
            }
        });
        cancel_button.visibleProperty().bind(editMode);
        object_table.disableProperty().bind(editMode);

        BooleanBinding removeButtonEnabled = isObjectSelected();
        remove_button.disableProperty().bind(removeButtonEnabled.not());

        add_button.disableProperty().bind(isReadyForAdd().not());

        edit_button.disableProperty().bind(removeButtonEnabled.not());

    }

    protected BooleanBinding isObjectSelected() {
        return Bindings.and(newObjectName.focusedProperty().not(), object_table.getFocusModel().focusedItemProperty().isNotNull());
    }

    protected void addEventHandlers() {
        add_button.addEventHandler(ActionEvent.ACTION, event -> {
            boolean success;
            if (editMode.getValue()) {
                success = updateObject();
            } else {
                success = addObject();
            }
            if (success) {
                logger.info(String.format("New %s created: %s. By user %s", T.getSimpleName(),
                        newObjectName.getText(),
                        sessionManager.getSession().getUser().getId()));
                clearInputFields();
                refreshObjectList();
                editMode.set(false);
            } else {
                openAlert(Alert.AlertType.INFORMATION, Utils.Dialogs.TITLE_INCONSISTENT_DATA, "Please make sure the name is unique!");
            }
        });

        remove_button.addEventHandler(ActionEvent.ACTION, event -> {
            if (dao.delete(object_table.getFocusModel().getFocusedItem())) {
                refreshObjectList();
            } else {
                openAlert(Alert.AlertType.WARNING, Utils.Dialogs.TITLE_DELETE_FAILED,
                        "Please make sure the " + T.getSimpleName() + " is not used");
            }
        });

        edit_button.addEventHandler(ActionEvent.ACTION, event -> {
            fillInputFields();
            editMode.set(true);
        });

        cancel_button.addEventHandler(ActionEvent.ACTION, event -> {
            clearInputFields();
            editMode.set(false);
        });
    }

    protected void fillInputFields() {
        newObjectName.setText(object_table.getFocusModel().getFocusedItem().getName());
    }

    protected void clearInputFields() {
        newObjectName.clear();
    }

    protected abstract boolean addObject();

    protected boolean updateObject() {
        boolean success;
        T object = object_table.getFocusModel().getFocusedItem();
        object.setName(newObjectName.getText());
        success = dao.update(object);
        return success;
    }

    protected BooleanBinding isReadyForAdd() {
        return Bindings.isNotEmpty(newObjectName.textProperty());
    }

    protected void refreshObjectList() {
        List<T> list = dao.getAll(T);
        Collections.sort(list);
        observableList.clear();
        observableList.addAll(list);
        selectFirstElementInTable();
    }

    protected abstract void setClass();

}
