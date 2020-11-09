package app.mainView.mainViewSegments.menuBar;

import app.mainView.mainViewSegments.MainViewSegment;
import app.newMenuItem.NewMenuItemController;
import basics.AlertBox;
import basics.ConfirmBox;
import basics.FamilyMember;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/*
* MenuBarController responsibilities:
* -Saving
* -Opening
* -Closing
* */


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

            //i need only positions for each familyMember, beacuse i can create opened familyTree by run addMemberToBoard
            //label should clearly determinate familyMember
            Label l = new Label();
            Iterable<Node> labelsOnBoard = pannableCanvas.getChildren();
            labelsOnBoard.forEach(label -> {
                if(label.getClass() == l.getClass()) {
                    String id = label.getId();
                    csvString.append(id).append(',');
                    csvString.append(label.getTranslateX()).append(',');
                    csvString.append(label.getTranslateY()).append(',');
                    csvString.append(mainViewController.getFamilyMembersHashMap().get(id).toString()).append('\n');
                }
            });

            writer.write(csvString.toString());
            writer.close();
        }
    }

    private void openExistingFamilyTree(String url) {
        saveURL = url;
        ifCanSave.setValue(true);

        mainViewController.getFamilyMembersHashMap().clear();
        pannableCanvas.getChildren().clear();

        readCSV(url);
    }

    private void readCSV(String url) {
        String FieldDelimiter = ",";
        BufferedReader br;

        try {
            br = new BufferedReader(new FileReader(url));

            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(FieldDelimiter, -1);

                FamilyMember familyMember = new FamilyMember(
                        fields[0],
                        fields[3],
                        fields[4],
                        fields[5],
                        LocalDate.parse(fields[6]),
                        fields[7]);

                mainViewController.getFamilyMembersHashMap().put(familyMember.getId(), familyMember);
                mainViewController.getCanvasController().addMemberToBoard(
                        familyMember,
                        Float.parseFloat(fields[1]),
                        Float.parseFloat(fields[2]));
            }

        } catch (FileNotFoundException ex) {
            AlertBox.display("Error", "File not found");
        } catch (IOException ex) {
            AlertBox.display("Error", "IOException");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        saveButton.disableProperty().bind(ifCanSave.not());
    }
}
