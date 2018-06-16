package cocktailMaker.ui.controllers;

import cocktailMaker.ui.controllers.templates.GuiceInjectedController;
import cocktailMaker.ui.controls.CustomControlsFactory;
import cocktailMaker.ui.controls.objects.CocktailButton;
import cocktailMaker.ui.controls.objects.CocktailGroupButton;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.apache.log4j.Logger;
import cocktailMaker.server.events.CocktailEvent;
import cocktailMaker.server.LogType;
import cocktailMaker.server.PageNavigator;
import cocktailMaker.server.cocktail.CocktailMaker;
import cocktailMaker.server.db.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by yasen.marinov on 7/8/2017.
 */
public class MakeCocktailController extends GuiceInjectedController {

    private static final Logger logger = Logger.getLogger(MakeCocktailController.class.getName());
    protected CocktailMaker cocktailMaker;

    @FXML
    public GridPane main_pane;

    @FXML
    public Pane makingCocktail_pane;

    @FXML
    public FlowPane cocktailGroup_pane;

    @FXML
    public FlowPane cocktail_pane;

    @FXML
    public Label welcome_label;

    @FXML
    public Button refill_button;

    @FXML
    public Button logOff_button;

    private List<CocktailGroupButton> cocktailGroupButtons = new ArrayList<>();
    private List<CocktailButton> cocktailButtons = new ArrayList<>();

    private Set<Ingredient> enabledIngredients;

    public void initialize() {
        welcome_label.setText("Welcome " + sessionManager.getSession().getUser().getFirstname());
        enabledIngredients = dao.getEnabledDispensers()
                .stream()
                .map(Dispenser::getIngredient)
                .collect(Collectors.toSet());

        addEventHandlers();
        setObjectsVisibility();
        populateCocktailGroupPane();
        addEventHandlers();

    }

    @Inject
    public void setCocktailMaker(CocktailMaker cocktailMaker) {
        this.cocktailMaker = cocktailMaker;
    }

    private void populateCocktailGroupPane() {
        List<CocktailGroup> cocktailGroups = dao.getAll(CocktailGroup.class);
        List<Button> buttons = new ArrayList<>(cocktailGroups.size());

        for (CocktailGroup cocktailGroup : cocktailGroups) {
            CocktailGroupButton cocktailGroupButton = CustomControlsFactory.getCocktailGroupButton(cocktailGroup);
            buttons.add(cocktailGroupButton.getButton());
            cocktailGroupButtons.add(cocktailGroupButton);
        }

        cocktailGroup_pane.getChildren().addAll(buttons);
    }

    protected void setObjectsVisibility() {
        makingCocktail_pane.setVisible(false);
    }

    protected void addEventHandlers() {
        main_pane.addEventFilter(CocktailEvent.DONE, event -> {
            makingCocktail_pane.setVisible(false);
            main_pane.setDisable(false);
        });

        main_pane.addEventFilter(CocktailEvent.BEGIN, event -> {
            main_pane.setDisable(true);
            makingCocktail_pane.setVisible(true);
        });

        logOff_button.addEventHandler(ActionEvent.ACTION, event -> {
            sessionManager.sessionInvalidate();
            pageNavigator.navigateTo(PageNavigator.PAGE_LOGIN);
        });

        refill_button.addEventHandler(ActionEvent.ACTION, event -> {
            pageNavigator.navigateTo(PageNavigator.PAGE_REFILL);
        });

        for (CocktailGroupButton button : cocktailGroupButtons) {
            button.getButton().addEventHandler(ActionEvent.ACTION, event -> {
                cocktailButtons.clear();
                cocktail_pane.getChildren().clear();
                fillCocktailPane(button.getCocktailGroup());
            });
        }

    }

    private void makeCocktail(Cocktail cocktail) {
        if (cocktailMaker.validate(cocktail)) {
            logger.info("Begin making " + cocktail.getName());
            main_pane.fireEvent(new CocktailEvent(CocktailEvent.BEGIN, cocktail));
            runCocktailTask(cocktail);
        } else {
            logger.info("Not enough availability for making " + cocktail.getName());
        }
    }

    private void runCocktailTask(Cocktail cocktail) {
        ExecutorService es = Executors.newSingleThreadExecutor();
        es.submit(() -> {
            cocktailMaker.make(cocktail);
            main_pane.fireEvent(new CocktailEvent(CocktailEvent.DONE, cocktail));
            logInDB(cocktail);
        });
    }

    private void logInDB(Cocktail cocktail) {
        dao.persist(new CocktailLog(LogType.TYPE_COCKTAIL,
                String.format("Cocktail %s made", cocktail.getName()),
                sessionManager.getSession().getUser().getUsername()));

        StringBuilder builder = new StringBuilder();
        for (CocktailIngredient cocktailIngredient : cocktail.getCocktailIngredients()) {
            builder.append(cocktailIngredient.getIngredient().getName());
            builder.append(":");
            builder.append(cocktailIngredient.getMillilitres());
            builder.append(";");
        }

        dao.persist(new CocktailLog(LogType.TYPE_INGREDIENTS,
                builder.toString(),
                sessionManager.getSession().getUser().getUsername()));
    }

    private Dialog<Boolean> getMakingCocktailDialog() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Please wait");
        dialog.setContentText("Making a cocktail is in progress...");

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
        return dialog;
    }

    private void fillCocktailPane(CocktailGroup cocktailGroup) {
        List<Cocktail> cocktails = dao.getCocktailsByGroup(cocktailGroup);
        List<Button> buttons = new ArrayList<>(cocktails.size());

        for (Cocktail cocktail : cocktails) {
            if (isAvailable(cocktail)) {
                CocktailButton cocktailButton = CustomControlsFactory.getCocktailButton(cocktail);
                buttons.add(cocktailButton.getButton());
                cocktailButtons.add(cocktailButton);
            }
        }

        for (CocktailButton button : cocktailButtons) {
            button.getButton().addEventHandler(ActionEvent.ACTION, event -> {
                makeCocktail(button.getCocktail());
            });
        }

        cocktail_pane.getChildren().addAll(buttons);
    }

    private boolean isAvailable(Cocktail cocktail) {
        return enabledIngredients.containsAll(cocktail.getCocktailIngredients().stream().
                map(CocktailIngredient::getIngredient)
                .collect(Collectors.toSet()));
    }


}
