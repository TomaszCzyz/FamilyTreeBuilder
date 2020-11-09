package app.mainView.mainViewSegments.rightPanel;

import app.mainView.mainViewSegments.MainViewSegment;
import basics.AlertBox;
import basics.ConfirmBox;
import basics.FamilyMember;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.util.Map;

public class RightPanelController extends MainViewSegment {

    @FXML
    public VBox rightPanelVBox;

    @FXML
    public Text firstNameText;
    @FXML
    public Text secondNameText;
    @FXML
    public Text lastNameText;
    @FXML
    public Text birthDateText;
    @FXML
    public Text fatherText;
    @FXML
    public Text motherText;
    @FXML
    public Button fatherLinkButton;
    @FXML
    public Button motherLinkButton;
    @FXML
    public Text spouseText;
    @FXML
    public Button spouseLinkButton;
    @FXML
    public Button delMotherLinkButton;


    @FXML
    public void handleMotherLinkButtonAction() {
        String childId = pannableCanvas.getCurrentNodeId();
        Rectangle child = pannableCanvas.getCurrentRectangle();

        child.setStroke(Color.RED);
        child.setStrokeType(StrokeType.OUTSIDE);
        child.setStrokeWidth(3);

        ChangeListener<String> listener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends String> obs, String oldValue, String newValue) {
                if (newValue != null) {
                    Rectangle mother = pannableCanvas.getCurrentRectangle();

                    if (validate(child.getId(), newValue)) {
                        Line line = new Line();
                        line.setId(childId + mother.getId());

                        line.startXProperty().bind(Bindings.add(child.translateXProperty(), 0.5 * child.getWidth()));
                        line.startYProperty().bind(child.translateYProperty());
                        line.endXProperty().bind(Bindings.add(mother.translateXProperty(), 0.5 * mother.getWidth()));
                        line.endYProperty().bind(Bindings.add(mother.translateYProperty(), mother.getHeight()));

                        line.toBack();
                        pannableCanvas.getChildren().add(line);
                        mainViewController.getFamilyMembersHashMap().get(childId).setMotherId(newValue);
                    }
                }
                unmarkRectangle(child);
                pannableCanvas.currentNodeIdProperty().removeListener(this);
            }
        };

        pannableCanvas.currentNodeIdProperty().addListener(listener);
    }

    @FXML
    public void handleSpouseLinkButtonAction() {
        String firstSpouseId = pannableCanvas.getCurrentNodeId();
        Rectangle firstSpouse = (Rectangle) pannableCanvas.lookup("#" + firstSpouseId);

        firstSpouse.setStroke(Color.GREEN);
        firstSpouse.setStrokeType(StrokeType.OUTSIDE);
        firstSpouse.setStrokeWidth(3);

        ChangeListener<String> listener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends String> obs, String oldValue, String newValue) {
                if (newValue != null) {
                    Rectangle secondSpouse = pannableCanvas.getCurrentRectangle();

                    if (validate(firstSpouseId, newValue)) {
                        Line line = new Line();
                        line.setId(firstSpouse.getId() + secondSpouse.getId());

                        /*
                        Depending on which rectangle is on the right/left, connecting line changes its anchor points
                        in other words: line is always between rectangles
                        */
                        line.startXProperty().bind(Bindings
                                .when(firstSpouse.translateXProperty().greaterThan(secondSpouse.translateXProperty()))
                                .then(firstSpouse.translateXProperty())
                                .otherwise(Bindings.add(firstSpouse.translateXProperty(), firstSpouse.getWidth())));
                        line.startYProperty().bind(Bindings.add(firstSpouse.translateYProperty(), 0.5 * firstSpouse.getHeight()));

                        line.endXProperty().bind(Bindings
                                .when(firstSpouse.translateXProperty().lessThan(secondSpouse.translateXProperty()))
                                .then(secondSpouse.translateXProperty())
                                .otherwise(Bindings.add(secondSpouse.translateXProperty(), secondSpouse.getWidth())));
                        line.endYProperty().bind(Bindings.add(secondSpouse.translateYProperty(), 0.5 * secondSpouse.getHeight()));

                        line.toBack();
                        pannableCanvas.getChildren().add(line);

                        mainViewController.getFamilyMembersHashMap().get(firstSpouseId).getPartners().add(secondSpouse.getId());
                        mainViewController.getFamilyMembersHashMap().get(secondSpouse.getId()).getPartners().add(firstSpouseId);
                    }
                }
                unmarkRectangle(firstSpouse);
                pannableCanvas.currentNodeIdProperty().removeListener(this);
            }
        };
        pannableCanvas.currentNodeIdProperty().addListener(listener);
    }

    @FXML
    public void handleDelMotherLinkButtonAction() {

        boolean answer = ConfirmBox.display("Warning", "Sure you want to delete connection to mother?");
        if (answer) {
            String childId = pannableCanvas.getCurrentNodeId();
            String motherId = mainViewController.getFamilyMembersHashMap().get(childId).getMotherId();
            mainViewController.getCanvasController().delLinkFromTo(childId, motherId);

            mainViewController.getFamilyMembersHashMap().get(childId).setMotherId("");

            //refresh rightPanel (to change button del(Link) to link)
            fillRightPanel(mainViewController.getFamilyMembersHashMap().get(childId));
        }
    }


    private void unmarkRectangle(Rectangle r) {
        r.setStroke(Color.BLUE);
        r.setStrokeType(StrokeType.CENTERED);
        r.setStrokeWidth(1);
    }


    private boolean validate(String childId, String motherId) {
        Map<String, FamilyMember> family = mainViewController.getFamilyMembersHashMap();
        LocalDate fatherBirthDate = family.get(motherId).getBirthDate();
        LocalDate childBirthDate = family.get(childId).getBirthDate();
        if (fatherBirthDate != null && childBirthDate != null) {
            if (fatherBirthDate.isAfter(childBirthDate)) {
                AlertBox.display("Invalid family member", "Father has to be older!");
                return false;
            }
        }
        return true;
    }

    public void fillRightPanel(FamilyMember familyMember) {
        firstNameText.setText(familyMember.getFirstName());
        secondNameText.setText(familyMember.getSecondName());
        lastNameText.setText(familyMember.getLastName());
        birthDateText.setText(String.valueOf(familyMember.getBirthDate()));

        if(!familyMember.getMotherId().equals("")) {
            motherLinkButton.setVisible(false);
            delMotherLinkButton.setVisible(true);
        } else {
            motherLinkButton.setVisible(true);
            delMotherLinkButton.setVisible(false);
        }
    }

    public final void setVisible(boolean b) {
        rightPanelVBox.setVisible(b);
    }

}
