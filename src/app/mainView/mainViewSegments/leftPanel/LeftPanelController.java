package app.mainView.mainViewSegments.leftPanel;

import app.addFamilyMember.AddMemberController;
import app.mainView.MainViewController;
import app.mainView.mainViewSegments.MainViewSegment;
import basics.FamilyMember;
import basics.PannableCanvas;
import com.sun.tools.javac.Main;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class LeftPanelController extends MainViewSegment implements Initializable {

    @FXML
    public Button addButton;
    @FXML
    public Button zoomInButton;
    @FXML
    public Button zoomOutButton;
    @FXML
    public VBox leftPanelVBox;

    public LeftPanelController() {
        injectMainViewController(mainViewController);
    }

    public void handleAddMemberButtonAction() {

        addButton.setDisable(true);

        FamilyMember familyMember = new FamilyMember();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/addFamilyMember/AddMemberStage.fxml"));
            Parent root = loader.load();
            AddMemberController controller = loader.getController();

            //it can be done better, by making FamilyMember's field StringProperty
            controller.firstNameProperty().addListener((v, oldValue, newValue) -> familyMember.setFirstName(newValue));
            controller.secondNameProperty().addListener((v, oldValue, newValue) -> familyMember.setSecondName(newValue));
            controller.lastNameProperty().addListener((v, oldValue, newValue) -> familyMember.setLastName(newValue));
            controller.birthDateProperty().addListener((v, oldValue, newValue) -> familyMember.setBirthDate(LocalDate.parse(newValue)));
            controller.notesProperty().addListener((v, oldValue, newValue) -> familyMember.setNotes(newValue));

            Stage stage = new Stage();
            stage.setTitle("Add new family member");
            stage.setScene(new Scene(root, 600, 400));
            stage.showAndWait();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if (!familyMember.getFirstName().isEmpty()) {
            mainViewController.getFamilyMembersArrayList().add(familyMember);
            mainViewController.getCanvasController().addMemberToBoard(familyMember, 100, 100);
            mainViewController.printFamilyMembersArrayList();
        } else {
            System.out.println("Nope\n");
        }
        addButton.setDisable(false);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
