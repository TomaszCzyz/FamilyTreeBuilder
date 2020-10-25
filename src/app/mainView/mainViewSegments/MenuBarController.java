package app.mainView.mainViewSegments;

import basics.AlertBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class MenuBarController {

    @FXML
    public MenuItem closeMenuItem;

    @FXML
    public Button button1;

    @FXML
    public void handleNewMenuItemAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/newMenuItem/NewMenuItemStage.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Create a new Family Tree");
            stage.setScene(new Scene(root, 600, 400));
            stage.showAndWait();
        } catch (IOException e) {
        e.printStackTrace();
        }
    }

    @FXML
    public void handleCloseMenuItemAction() {
        Stage stage = (Stage) button1.getScene().getWindow();
        stage.close();
    }

}
