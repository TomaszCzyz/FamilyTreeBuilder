package app.stages.mainview.mainviewsegments.leftPanel;

import app.basics.*;
import app.stages.addfamilymember.AddMemberController;
import app.stages.mainview.mainviewsegments.MainViewSegment;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;


public class LeftPanelController extends MainViewSegment implements Initializable {

    private final Logger logger = LoggerFactory.getLogger(LeftPanelController.class);

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
            mainViewController.getCanvasController().addFamilyMemberBox(familyMember);
            mainViewController.printFamilyMembersHashMap();
        } else {
            logger.info("No new family member added");
        }
        addButton.setDisable(false);
    }

    @FXML
    public void handleDelMemberButtonAction() {
        String famMemId = canvasController.pannableCanvas.getCurrentBoxId();
        if (canvasController.getBoxesMap().get(famMemId) == null) {
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
    private final Map<String, Set<String>> tiedGroupsMap = new HashMap<>();  //maps group's color to set of rectangles' ids
    private final Set<String> newTiedGroup = new HashSet<>();
    private Color newTiedGroupColor;   //color is key of tiedGroups

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

        if (anchorToggleButton.isSelected()) {
            newTiedGroupColor = getColor(tiedGroupsMap.size());

            listener = (obs, oldId, newId) -> {
                if (!newId.equals("")) {
                    newTiedGroup.add(newId);
                    canvasController.getBoxesMap().get(newId).mark(newTiedGroupColor);

                    Map<String, Set<String>> tiedGroups_Copy = new HashMap<>(tiedGroupsMap);
                    tiedGroups_Copy.forEach((color, tiedGroup) -> {
                        if (tiedGroup.contains(newId)) {  //if clicked rectangle is already in a tiedGroup

                            newTiedGroupColor = Color.valueOf(color);
                            newTiedGroup.forEach(s -> canvasController.getBoxesMap().get(s).mark(newTiedGroupColor));

                            newTiedGroup.addAll(tiedGroup);
                            tiedGroupsMap.remove(color);

                            logger.info("tiedGroupMap: {}", tiedGroupsMap);
                        }
                    });
                }
                logger.info("newTiedGroup: {}", newTiedGroup);
            };

            canvasController.pannableCanvas.currentBoxIdProperty().addListener(listener);
        } else {
            if (!newTiedGroup.isEmpty()) {
                tiedGroupsMap.put(String.valueOf(newTiedGroupColor), new HashSet<>(newTiedGroup));
                newTiedGroup.clear();
            }
            canvasController.pannableCanvas.currentBoxIdProperty().removeListener(listener);

            updateTiedGroups();
        }
    }

    /*
     * Check if all FamilyMemberBoxes are in good Unions,
     * if not, then put them in right ones
     */
    private void updateTiedGroups() {
        logger.info("tiedGroup: {}", tiedGroupsMap);
        logger.info("unionMap: {}", canvasController.getUnionsMap());

        tiedGroupsMap.forEach((color, tiedGroup) -> {
            //create a newUnion with color
            Group newUnion = new Group();
            newUnion.setId(color);
            NodeGestures nodeGestures = new NodeGestures(canvasController.pannableCanvas);
            newUnion.addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
            newUnion.addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());
            canvasController.pannableCanvas.getChildren().add(newUnion);

            tiedGroup.forEach(familyMemberId -> {
                Group currentUnion = canvasController.getUnionsMap().get(familyMemberId);
                FamilyMemberBox familyMemberBox = canvasController.getBoxesMap().get(familyMemberId);
                if (currentUnion.getId().equals(familyMemberBox.getId())) { /*if familyMemberBox is alone in his group, then delete this group after moving Box*/
                    canvasController.getUnionsMap().put(familyMemberId, newUnion);

                    currentUnion.getChildren().remove(familyMemberBox);
                    newUnion.getChildren().add(familyMemberBox);
                }
            });
        });
    }


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
