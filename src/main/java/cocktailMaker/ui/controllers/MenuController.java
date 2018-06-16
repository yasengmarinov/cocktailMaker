package cocktailMaker.ui.controllers;

import cocktailMaker.ui.controllers.templates.GuiceInjectedController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import cocktailMaker.server.PageNavigator;
import cocktailMaker.server.session.SessionManager;

/**
 * Created by yasen.marinov on 6/18/2017.
 */
public class MenuController extends GuiceInjectedController {

    @FXML
    public MenuItem logoff_item;
    @FXML
    public MenuItem dispensers_item;

    @FXML
    public MenuItem ingredients_item;

    @FXML
    public MenuItem users_item;

    @FXML
    public MenuItem ingredientsLog_item;

    @FXML
    public MenuItem cocktailLog_item;

    @FXML
    public MenuItem cocktailGroup_item;

    @FXML
    public MenuItem cocktail_item;

    @FXML
    public MenuItem exit_item;

    public void initialize() {

        configureNavigation(dispensers_item, PageNavigator.PAGE_CONFIGURE_DISPENSERS);
        configureNavigation(ingredients_item, PageNavigator.PAGE_CONFIGURE_INGREDIENTS);
        configureNavigation(users_item, PageNavigator.PAGE_CONFIGURE_USERS);
        configureNavigation(cocktailLog_item, PageNavigator.PAGE_COCKTAIL_LOG);
        configureNavigation(ingredientsLog_item, PageNavigator.PAGE_INGREDIENTS_LOG);
        configureNavigation(cocktailGroup_item, PageNavigator.PAGE_CONFIGURE_COCKTAIL_GROUPS);
        configureNavigation(cocktail_item, PageNavigator.PAGE_CONFIGURE_COCKTAILS);

        logoff_item.addEventHandler(ActionEvent.ACTION, event -> {
            sessionManager.sessionInvalidate();
            pageNavigator.navigateTo(PageNavigator.PAGE_LOGIN);
        });

        exit_item.addEventHandler(ActionEvent.ACTION, event -> {
            System.exit(0);
        });

    }

    private void configureNavigation(MenuItem menuItem, String page) {
        menuItem.addEventHandler(ActionEvent.ACTION, event -> {
            pageNavigator.navigateTo(page);
        });
    }

}
