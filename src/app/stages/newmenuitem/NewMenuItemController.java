package app.stages.newmenuitem;

import app.basics.AlertBox;
import app.basics.NodeGestures;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class NewMenuItemController implements Initializable {

    private final Logger logger = LoggerFactory.getLogger(NewMenuItemController.class);

    private boolean isFileCreated = false;

    @FXML
    public TextField fileNameTextFiled;
    @FXML
    public Label pathLabel;
    @FXML
    public Button changeDirButton;
    @FXML
    public Button okButton;
    @FXML
    public Button cancelButton;

    private final StringProperty newFileName = new SimpleStringProperty("outfile.csv");

    private final StringProperty baseURL = new SimpleStringProperty(new File("").getAbsolutePath());

    private final StringProperty fullURL = new SimpleStringProperty(baseURL.getValue() + "\\" + newFileName.getValue());


    @FXML
    public void handleChangeDirButtonAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose folder");
        fileChooser.setInitialDirectory(new File(baseURL.getValue()));
        fileChooser.setInitialFileName(fileNameTextFiled.getText() + ".csv");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.*"));
        baseURL.setValue(fileChooser.showSaveDialog(okButton.getScene().getWindow()).getParent());
    }

    @FXML
    public void handleOkButtonAction() {
        if (!isPathValid(fullURL.getValue()))
            return;

        try {
            File newFile = new File(fullURL.getValue());
            if (newFile.createNewFile()) {
                isFileCreated = true;

                logger.info("File created: {}", newFile.getName());
                Stage stage = (Stage) okButton.getScene().getWindow();
                stage.close();
            } else {
                AlertBox.display("Failed!", "File already exists.");
            }
        } catch (IOException e) {
            AlertBox.display("Error", "Action fialed");
            e.printStackTrace();

            Stage stage = (Stage) okButton.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    public void handleCancelButtonAction() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public static boolean isPathValid(String path) {
        try {
            Paths.get(path);
        } catch (InvalidPathException ex) {
            return false;
        }
        return true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pathLabel.setText(fullURL.getValue());

        fileNameTextFiled.textProperty().addListener((v, oldValue, newValue) -> {
            newFileName.setValue(newValue + ".csv");
        });
        newFileName.addListener((v, oldValue, newValue) -> {
            fullURL.setValue(baseURL.getValue() + "\\" + newValue);
        });
        baseURL.addListener((v, oldValue, newValue) -> {
            fullURL.setValue(newValue + "\\" + newFileName.getValue());
        });
        fullURL.addListener((v, oldValue, newValue) -> {
            pathLabel.setText(fullURL.getValue());
        });
    }

    public String getFullURL() {
        return fullURL.get();
    }

    public StringProperty fullURLProperty() {
        return fullURL;
    }

    public boolean isFileCreated() {
        return isFileCreated;
    }
}
