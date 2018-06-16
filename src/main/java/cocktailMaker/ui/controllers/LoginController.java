package cocktailMaker.ui.controllers;

import cocktailMaker.server.PageNavigator;
import cocktailMaker.server.Utils;
import cocktailMaker.server.card.CardSwipeDispatcher;
import cocktailMaker.server.card.SwipeEventListener;
import cocktailMaker.server.db.entities.User;
import cocktailMaker.server.session.Session;
import cocktailMaker.ui.controllers.templates.GuiceInjectedController;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.apache.log4j.Logger;

public class LoginController extends GuiceInjectedController implements SwipeEventListener {

    private static final Logger logger = Logger.getLogger(LoginController.class.getName());

    @FXML
    public GridPane main_grid;

    @FXML
    public HBox credentials_box;

    @FXML
    public TextField username_field;

    @FXML
    public PasswordField password_field;

    @FXML
    public Button login_button;

    @FXML
    public Button logWithCredentials_button;

    @FXML
    public Button logWithCard_button;

    protected BooleanProperty credentials_mode = new SimpleBooleanProperty(false);

    public void initialize() {
        CardSwipeDispatcher.getInstance().subscribe(this);
        BooleanBinding loginButtonEnabled = Bindings.and(username_field.textProperty().isNotEmpty(),
                password_field.textProperty().isNotEmpty());

        login_button.disableProperty().bind(loginButtonEnabled.not());
        credentials_box.visibleProperty().bind(credentials_mode);
        logWithCredentials_button.visibleProperty().bind(credentials_mode.not());

        logWithCredentials_button.addEventHandler(ActionEvent.ACTION, event -> {
            CardSwipeDispatcher.getInstance().unsubscribe(this);
            credentialsModeToggle();
        });
        logWithCard_button.addEventHandler(ActionEvent.ACTION, event -> {
            CardSwipeDispatcher.getInstance().subscribe(this);
            credentialsModeToggle();
        });

        login_button.addEventHandler(ActionEvent.ACTION, event -> {
            Session session = sessionManager.createSession(username_field.getText(), password_field.getText());
            if (session != null) {
                logUser(session.getUser());
            } else {
                Utils.Dialogs.openAlert(Alert.AlertType.WARNING, Utils.Dialogs.TITLE_LOGIN_FAILED, Utils.Dialogs.CONTENT_LOGIN_FAILED);
                username_field.clear();
                password_field.clear();
            }
        });

    }

    private void credentialsModeToggle() {
        credentials_mode.setValue(!credentials_mode.getValue());
    }

    private void logUser(User user) {
        if (user == null) {
            logger.warn("User is null");
            return;
        }
        if (user.getIsAdmin()) {
            pageNavigator.navigateTo(PageNavigator.PAGE_HOME_ADMIN);
        } else {
            pageNavigator.navigateTo(PageNavigator.PAGE_MAKE_COCKTAIL);
        }
        CardSwipeDispatcher.getInstance().unsubscribe(this);
    }

    @Override
    public void cardSwiped(String card) {
        User user = dao.getUserByCard(card);
        if (user == null) {
            Utils.Dialogs.openAlert(Alert.AlertType.WARNING,
                    Utils.Dialogs.TITLE_UNRECOGNIZED_CARD,
                    Utils.Dialogs.CONTENT_UNRECOGNIZED_CARD);
        } else {
            sessionManager.createSession(user);
            logUser(user);
        }
    }
}
