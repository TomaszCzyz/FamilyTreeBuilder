package app.stages.mainview.mainViewSegments.leftPanel;

import app.stages.addfamilymember.AddMemberController;
import app.stages.mainview.mainViewSegments.MainViewSegment;
import app.basics.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;


public class LeftPanelController extends MainViewSegment implements Initializable {

    @FXML
    public VBox leftPanelVBox;
    @FXML
    public Button addButton;
    @FXML
    public Button zoomInButton;
    @FXML
    public Button zoomOutButton;
    @FXML
    public Button delButton;

    public LeftPanelController() {
        injectMainViewController(mainViewController);
    }

    @FXML
    public void handleAddMemberButtonAction() {

        addButton.setDisable(true);

        FamilyMember familyMember = new FamilyMember();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/stages/addfamilymember/AddMemberStage.fxml"));
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
            stage.setScene(new Scene(root));
            stage.showAndWait();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if (!familyMember.getFirstName().isEmpty()) {
            mainViewController.getFamilyMembersHashMap().put(familyMember.getId(), familyMember);
            mainViewController.getCanvasController().addMemberToBoard(familyMember);
            mainViewController.printFamilyMembersHashMap();
        } else {
            System.out.println("No new family member added\n");
        }
        addButton.setDisable(false);
    }

    @FXML
    public void handleDelMemberButtonAction() {
        String famMemId = canvasController.pannableCanvas.getCurrentNodeId();
        if(famMemId == null) {
            AlertBox.display("Delete", "Choose family member from board first!");
        } else {
            boolean answer = ConfirmBox.display("Delete", "Sure you want to delete family member permanently?");
            if (answer) {
                mainViewController.getFamilyMembersHashMap().remove(famMemId);
                mainViewController.getCanvasController().delBoxFromCanvas(famMemId);
                mainViewController.getRightPanelController().setVisible(false);
            }
        }
    }

        @FXML
    public void handleZoomInButtonAction() {
        double scale = canvasController.pannableCanvas.getScale();
        scale *= Math.pow(1.002, 40.0);

        if (scale >= SceneGestures.getMaxScale()) {
            scale = SceneGestures.getMaxScale();
        }

            canvasController.pannableCanvas.setScale(scale);
    }

    @FXML
    public void handleZoomOutButtonAction() {
        double scale = canvasController.pannableCanvas.getScale();
        scale *= Math.pow(1.002, -40.0);

        if (scale <= SceneGestures.getMinScale()) {
            scale = SceneGestures.getMinScale();
        }

        canvasController.pannableCanvas.setScale(scale);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
