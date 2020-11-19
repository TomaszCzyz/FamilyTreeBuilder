package app.stages.mainview.mainviewsegments.leftPanel;

import app.basics.AlertBox;
import app.basics.ConfirmBox;
import app.basics.FamilyMember;
import app.basics.SceneGestures;
import app.stages.addfamilymember.AddMemberController;
import app.stages.mainview.mainviewsegments.MainViewSegment;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;


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
    @FXML
    public ToggleButton anchorToggleButton;

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
        } catch (IOException e) {
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
        if (famMemId == null) {
            AlertBox.display("Delete", "Choose family member from board first!");
        } else {
            boolean answer = ConfirmBox.display("Delete", "Sure you want to delete family member permanently?");
            if (answer) {
                mainViewController.getCanvasController().delFromCanvas(famMemId);
                mainViewController.getFamilyMembersHashMap().remove(famMemId);
                mainViewController.getRightPanelController().setVisible(false);
                canvasController.pannableCanvas.resetCurrentNode();
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


    ChangeListener<String> listener;
    private final Set<String> newTiedGroup = new HashSet<>();
    private Color newTiedGroupColor;   //color is key of tiedDroups_


    /*
    * When anchorToggleButton is pressed newTiedGroup is created.
    * If untied rectangle will be clicked it will be added to newTiedGroup
    * But if rectangle which already is in TiedGroup will be selected, then this TiedGroup will become
    * newTiedGroup and it will be delete form tiedGroups(Map). Thanks to this behavior multiple small TiedGroups
    * can be join into one.
    * When anchorToggleButton is released, newTiedGroup is added to tiedGroups_
    * */
    @FXML
    public void handleAnchorToggleButtonAction() {
        mainViewController.getRightPanelController().rightPanelVBox.setDisable(anchorToggleButton.isSelected());
        canvasController.pannableCanvas.resetCurrentNode();

        Map<String, Set<String>> tiedGroups_ = canvasController.getTiedGroups();

        if (anchorToggleButton.isSelected()) {
            newTiedGroupColor = getColor(tiedGroups_.size());

            listener = (obs, oldId, newId) -> {
                if (!newId.equals("")) {
                    newTiedGroup.add(newId);
                    canvasController.markRectangle(canvasController.getRectangles().get(newId), newTiedGroupColor);

                    Map<String, Set<String>> tiedGroups_Copy = new HashMap<>(tiedGroups_);
                    tiedGroups_Copy.forEach((color, tiedGroup) -> {
                        if (tiedGroup.contains(newId)) {  //if clicked rectangle is already in a tiedGroup

                            newTiedGroupColor = Color.valueOf(color);
                            newTiedGroup.forEach(s -> canvasController.markRectangle(canvasController.getRectangles().get(s), newTiedGroupColor));

                            newTiedGroup.addAll(tiedGroup);
                            tiedGroups_.remove(color);

                            System.out.println(tiedGroups_);
                        }
                    });
                }
                System.out.println(newTiedGroup);
            };

            canvasController.pannableCanvas.currentNodeIdProperty().addListener(listener);
        } else {
            if(!newTiedGroup.isEmpty()) {
                tiedGroups_.put(String.valueOf(newTiedGroupColor), new HashSet<>(newTiedGroup));
                newTiedGroup.clear();
            }
            canvasController.pannableCanvas.currentNodeIdProperty().removeListener(listener);

            //updateTiedGroups()
            System.out.println(tiedGroups_);
        }
    }


//    private void updateTiedGroups() {
//        canvasController.getTiedGroups()
//    }


    private Color getColor(int i) {
        Random rand = new Random(i);
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        return Color.color(r, g, b);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

}
