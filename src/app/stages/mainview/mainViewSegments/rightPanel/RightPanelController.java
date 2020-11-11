package app.stages.mainview.mainViewSegments.rightPanel;

import app.stages.mainview.mainViewSegments.MainViewSegment;
import app.basics.AlertBox;
import app.basics.ConfirmBox;
import app.basics.FamilyMember;
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
        linkFrom(pannableCanvas.getCurrentRectangle(), "toMother");
    }

    @FXML
    public void handleSpouseLinkButtonAction() {
        linkFrom(pannableCanvas.getCurrentRectangle(), "toSpouse");
    }

    private void linkFrom(Rectangle startRectangle, String linkType) {
        /*setMouseTransparent is used to prevent pressing buttons in right panel till linking operation ends*/
        rightPanelVBox.setDisable(true);
        markRectangle(startRectangle, linkType);

        ChangeListener<String> listener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends String> obs, String oldValue, String newValue) {

                if (newValue != null && validate(startRectangle.getId(), newValue)) {

                    Rectangle endRectangle = pannableCanvas.getCurrentRectangle();

                    Line line = new Line();
                    line.setId(startRectangle.getId() + endRectangle.getId());

                    switch (linkType) {
                        case "toMother":
                            line.startXProperty().bind(Bindings.add(startRectangle.translateXProperty(), 0.5 * startRectangle.getWidth()));
                            line.startYProperty().bind(startRectangle.translateYProperty());
                            line.endXProperty().bind(Bindings.add(endRectangle.translateXProperty(), 0.5 * endRectangle.getWidth()));
                            line.endYProperty().bind(Bindings.add(endRectangle.translateYProperty(), endRectangle.getHeight()));
                            break;
                        case "toSpouse":
                            /*
                            Depending on which rectangle is on the right/left, connecting line changes its anchor points
                            in other words: line is always between rectangles
                            */
                            line.startXProperty().bind(Bindings
                                    .when(startRectangle.translateXProperty().greaterThan(endRectangle.translateXProperty()))
                                    .then(startRectangle.translateXProperty())
                                    .otherwise(Bindings.add(startRectangle.translateXProperty(), startRectangle.getWidth())));
                            line.startYProperty().bind(Bindings.add(startRectangle.translateYProperty(), 0.5 * startRectangle.getHeight()));

                            line.endXProperty().bind(Bindings
                                    .when(startRectangle.translateXProperty().lessThan(endRectangle.translateXProperty()))
                                    .then(endRectangle.translateXProperty())
                                    .otherwise(Bindings.add(endRectangle.translateXProperty(), endRectangle.getWidth())));
                            line.endYProperty().bind(Bindings.add(endRectangle.translateYProperty(), 0.5 * endRectangle.getHeight()));
                            break;
                    }

                    line.toBack();
                    pannableCanvas.getChildren().add(line);

                    mainViewController.getFamilyMembersHashMap().get(startRectangle.getId()).getPartners().add(endRectangle.getId());
                    mainViewController.getFamilyMembersHashMap().get(endRectangle.getId()).getPartners().add(startRectangle.getId());
                }
                unmarkRectangle(startRectangle);
                rightPanelVBox.setDisable(false);
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

    private void markRectangle(Rectangle r, String markType) {
        r.setStrokeType(StrokeType.OUTSIDE);
        r.setStrokeWidth(3);
        switch (markType) {
            case "toMother":
                r.setStroke(Color.RED);
                break;
            case "toSpouse":
                r.setStroke(Color.GREEN);
                break;
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
