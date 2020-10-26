package app.mainView.mainViewSegments.menuBar;

import app.mainView.mainViewSegments.MainViewSegment;
import app.newMenuItem.NewMenuItemController;
import basics.ConfirmBox;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class MenuBarController extends MainViewSegment implements Initializable {

    private String saveURL;

    private final BooleanProperty ifCanSave;

    @FXML
    public Button saveButton;


    public MenuBarController() {
        ifCanSave = new SimpleBooleanProperty(false);
        saveURL = "";
    }

    @FXML
    public void handleNewMenuItemAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/newMenuItem/NewMenuItemStage.fxml"));
            Parent root = (Parent) loader.load();

            Stage stage = new Stage();
            stage.setTitle("Create a new Family Tree");
            stage.setScene(new Scene(root, 600, 400));
            stage.showAndWait();

            NewMenuItemController controller = loader.getController();
            saveURL = controller.getFullURL();
            ifCanSave.setValue(controller.isFileCreated());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleOpenMenuItemAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.*"));
        String url = fileChooser.showOpenDialog(saveButton.getScene().getWindow()).toString();
        if(url != null) {
            if(!mainViewController.getFamilyMembersHashMap().isEmpty()) {
                boolean answer = ConfirmBox.display("Warning", "Open family tree will be close.\n Are you sure you want to continue?");
                if(!answer) {
                    return;
                }
            }
            openExistingFamilyTree(url);
        }
    }

    private void openExistingFamilyTree(String url) {
        saveURL = url;
        ifCanSave.setValue(true);

        mainViewController.getFamilyMembersHashMap().clear();
        mainViewController.getCanvasController().getPannableCanvas().getChildren().clear();
    }

    @FXML
    public void handleCloseMenuItemAction() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleSaveButtonAction() throws IOException {
        if(!ifCanSave.getValue()) {
            handleNewMenuItemAction();
        }
        if(ifCanSave.getValue()) {
            FileWriter writer = new FileWriter(saveURL);
            StringBuilder csvString = new StringBuilder();

            mainViewController.getFamilyMembersHashMap().forEach((key, value) ->
                    csvString.append(key).append(";").append(value.toString()).append(";"));

            writer.write(csvString.toString());
            writer.close();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        saveButton.disableProperty().bind(ifCanSave.not());
    }
}
