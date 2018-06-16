package cocktailMaker.ui.controllers;

import cocktailMaker.server.card.CardSwipeDispatcher;
import cocktailMaker.server.card.SwipeEventListener;
import cocktailMaker.ui.controllers.templates.GuiceInjectedController;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import cocktailMaker.server.Utils;
import cocktailMaker.server.db.entities.User;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;

/**
 * Created by yasen.marinov on 6/19/2017.
 */
public class ConfigureUsersController extends GuiceInjectedController implements SwipeEventListener {

    private static final Logger logger = Logger.getLogger(ConfigureUsersController.class);

    @FXML
    public GridPane main_grid;

    @FXML
    public Pane cardSwipe_pane;

    @FXML
    public TableView<User> users_table;

    @FXML
    public TableColumn<User, String> username_column;

    @FXML
    public TableColumn<User, String> firstname_column;

    @FXML
    public TableColumn<User, String> lastname_column;

    @FXML
    public TableColumn<User, Boolean> card_column;

    @FXML
    public TableColumn<User, Boolean> admin_column;

    @FXML
    public TextField username_field;

    @FXML
    public TextField firstname_field;

    @FXML
    public TextField lastname_field;

    @FXML
    public PasswordField password_field;

    @FXML
    public PasswordField confirmPassword_field;

    @FXML
    public CheckBox admin_checkbox;

    @FXML
    public Button setCard_button;

    @FXML
    public Button cancel_button;

    @FXML
    public Button add_button;

    @FXML
    public Button remove_button;

    @FXML
    public Button edit_button;

    private ObservableList<User> usersObservableList = FXCollections.observableArrayList();
    private SimpleBooleanProperty editMode = new SimpleBooleanProperty(false);
    protected Property<User> selectedObject = new SimpleObjectProperty<>();


    public void initialize() {
        configureTableColumns();
        users_table.setItems(usersObservableList);
        selectedObject.bind(users_table.getSelectionModel().selectedItemProperty());

        refreshUsersList();

        setObjectsVisibility();
        addEventHandlers();
    }

    private void configureTableColumns() {
        username_column.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
        firstname_column.setCellValueFactory(new PropertyValueFactory<User, String>("firstname"));
        lastname_column.setCellValueFactory(new PropertyValueFactory<User, String>("lastname"));

        card_column.setCellValueFactory(param -> {
            SimpleBooleanProperty simpleBooleanProperty = new SimpleBooleanProperty(param.getValue().getMagneticCard() != null);
            return simpleBooleanProperty;
        });
        card_column.setCellFactory(CheckBoxTableCell.forTableColumn(card_column));

        admin_column.setCellValueFactory(param -> {
            SimpleBooleanProperty simpleBooleanProperty = new SimpleBooleanProperty(param.getValue().getIsAdmin());
            return simpleBooleanProperty;
        });
        admin_column.setCellFactory(CheckBoxTableCell.forTableColumn(admin_column));
    }

    protected void setObjectsVisibility() {

        cardSwipe_pane.setVisible(false);

        //Add button
        add_button.textProperty().bind(new StringBinding() {
            {
                super.bind(editMode);
            }

            @Override
            protected String computeValue() {
                return (editMode.getValue() ? "Save" : "Add");
            }
        });
        BooleanBinding addButtonEnabled = Bindings.and
                (username_field.textProperty().isNotEmpty(), confirmPassword_field.textProperty().isNotEmpty()).and(password_field.textProperty().isNotEmpty()).
                or(editMode);
        add_button.disableProperty().bind(addButtonEnabled.not());

        //Remove and Edit buttons
        BooleanBinding removeEditButtonsEnabled = Bindings.and(users_table.focusedProperty(), users_table.getFocusModel().focusedItemProperty().isNotNull()).and(editMode.not());
        remove_button.disableProperty().bind(removeEditButtonsEnabled.not().
                or(isDefaultAdminSelected()));

        //Edit button
        edit_button.disableProperty().bind(removeEditButtonsEnabled.not());

        //Cancel button
        cancel_button.visibleProperty().bind(editMode);

        //Set Card button
        setCard_button.textProperty().bind(new StringBinding() {
            {
                super.bind(selectedObject);
            }

            @Override
            protected String computeValue() {
                return (selectedObject.getValue() == null || selectedObject.getValue().getMagneticCard() == null ? "Set Card" : "Clear Card");
            }
        });
        setCard_button.disableProperty().bind(removeEditButtonsEnabled.not());

        //Username field
        username_field.disableProperty().bind(editMode);

        //Confirm password field
        confirmPassword_field.disableProperty().bind(password_field.textProperty().isEmpty());

        //Admin checkbox
        admin_checkbox.disableProperty().bind(editMode.and(isDefaultAdminSelected()));
    }

    private BooleanBinding isDefaultAdminSelected() {
        return users_table.getSelectionModel().selectedItemProperty().isEqualTo(dao.getUser("admin"));
    }

    protected void addEventHandlers() {
        add_button.addEventHandler(ActionEvent.ACTION, event -> {
            if (!passwordsMatch()) {
                Utils.Dialogs.openAlert(Alert.AlertType.WARNING, Utils.Dialogs.TITLE_PASSWORD_DO_NOT_MATCH, Utils.Dialogs.CONTENT_PASSWORDS_DO_NOT_MATCH);
                password_field.clear();
                confirmPassword_field.clear();
                return;
            }
            boolean success;
            if (editMode.getValue()) {
                success = updateUser();
            } else {
                success = createUser();
            }

            if (success) {
                clearFields();
                editMode.setValue(false);
                refreshUsersList();
            } else {
                Utils.Dialogs.openAlert(Alert.AlertType.INFORMATION, Utils.Dialogs.TITLE_INCONSISTENT_DATA,
                        "Please make sure the UserID is unique");
            }
        });

        remove_button.addEventHandler(ActionEvent.ACTION, event -> {
            if (dao.delete(users_table.getFocusModel().getFocusedItem())) {
                refreshUsersList();
            } else {
                Utils.Dialogs.openAlert(Alert.AlertType.WARNING, Utils.Dialogs.TITLE_DELETE_FAILED, "Delete of user failed");
            }
        });

        edit_button.addEventHandler(ActionEvent.ACTION, event -> {
            populateFields();
            editMode.setValue(true);
        });

        cancel_button.addEventHandler(ActionEvent.ACTION, event -> {
            editMode.setValue(false);
            clearFields();
        });

        setCard_button.addEventHandler(ActionEvent.ACTION, event -> {
            if (selectedObject.getValue().getMagneticCard() == null) {
                enterCarSetMode();
            } else {
                clearCard();
            }
        });
    }

    private void clearCard() {
        User user = selectedObject.getValue();
        user.setMagneticCard(null);
        dao.update(user);
        refreshUsersList();
    }

    private void enterCarSetMode() {
        CardSwipeDispatcher.getInstance().subscribe(this);
        main_grid.setDisable(true);
        cardSwipe_pane.setVisible(true);
    }

    private void exitCarSetMode() {
        CardSwipeDispatcher.getInstance().unsubscribe(this);
        main_grid.setDisable(false);
        cardSwipe_pane.setVisible(false);
    }

    private boolean createUser() {
        boolean success;
        success = dao.persist(new User(username_field.getText().toLowerCase(), firstname_field.getText(),
                lastname_field.getText(), Utils.md5(password_field.getText()), admin_checkbox.isSelected()));
        if (success)
            logger.info("New user created: " + username_field.getText());
        return success;
    }

    private boolean updateUser() {
        boolean success;

        User user = users_table.getFocusModel().getFocusedItem();
        user.setFirstname(firstname_field.getText());
        user.setLastname(lastname_field.getText());
        user.setIsAdmin(admin_checkbox.isSelected());
        if (!password_field.getText().isEmpty()) {
            user.setPassword(Utils.md5(password_field.getText()));
        }

        success = dao.update(user);

        if (success)
            logger.info("User updated: " + user.getUsername());
        return success;
    }

    private void populateFields() {
        User user = users_table.getFocusModel().getFocusedItem();
        username_field.setText(user.getUsername());
        firstname_field.setText(user.getFirstname());
        lastname_field.setText(user.getLastname());
        admin_checkbox.setSelected(user.getIsAdmin());
    }

    private boolean passwordsMatch() {
        return password_field.getText().equals(confirmPassword_field.getText());
    }

    private void clearFields() {
        firstname_field.clear();
        lastname_field.clear();
        password_field.clear();
        confirmPassword_field.clear();
        username_field.clear();
        admin_checkbox.setSelected(false);
    }

    private void refreshUsersList() {
        List<User> list = dao.getAll(User.class);
        Collections.sort(list);
        usersObservableList.clear();
        usersObservableList.addAll(list);

    }

    @Override
    public void cardSwiped(String card) {
        if (dao.getUserByCard(card) != null) {
            Utils.Dialogs.openAlert(Alert.AlertType.WARNING, Utils.Dialogs.TITLE_INCONSISTENT_DATA, Utils.Dialogs.CONTENT_USED_CARD);
        } else {
            User user = users_table.getSelectionModel().getSelectedItem();
            user.setMagneticCard(Utils.md5(card));
            dao.update(user);
            users_table.refresh();
        }
        exitCarSetMode();
    }
}
