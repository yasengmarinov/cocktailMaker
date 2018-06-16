package cocktailMaker.ui.controllers;

import cocktailMaker.server.db.entities.CocktailLog;
import cocktailMaker.ui.controllers.templates.GuiceInjectedController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.text.SimpleDateFormat;

/**
 * Created by yasen.marinov on 6/22/2017.
 */
public class LogController extends GuiceInjectedController {

    public static final String DATE_FORMAT = "dd/MM/yy hh:mm";

    @FXML
    public TableView<CocktailLog> log_table;

    @FXML
    public TableColumn<CocktailLog, String> message_column;

    @FXML
    public TableColumn<CocktailLog, String> user_column;

    @FXML
    public TableColumn<CocktailLog, String> date_column;

    protected ObservableList<CocktailLog> cocktailLogObservableList;

    public void initialize() {

        configureTableColumns();
        refreshLogList();
        log_table.setItems(cocktailLogObservableList);


    }

    private void configureTableColumns() {
        message_column.setCellValueFactory(new PropertyValueFactory<CocktailLog, String>("message"));
        user_column.setCellValueFactory(new PropertyValueFactory<CocktailLog, String>("username"));
        date_column.setCellValueFactory(param -> {
            return new SimpleStringProperty(new SimpleDateFormat(DATE_FORMAT).format(param.getValue().getEventDate()));
        });
    }

    private void refreshLogList() {
        setObservableList();
        FXCollections.sort(cocktailLogObservableList);
    }

    protected void setObservableList() {
        cocktailLogObservableList = FXCollections.observableArrayList(dao.getCocktailLog());
    }

}
