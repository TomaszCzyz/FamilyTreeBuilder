package app.mainView.mainViewSegments.rightPanel;

import app.mainView.mainViewSegments.MainViewSegment;
import basics.AlertBox;
import basics.FamilyMember;
import basics.PannableCanvas;
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
    public void handlefatherLinkButtonAction() {
        PannableCanvas pannableCanvas = mainViewController.getCanvasController().pannableCanvas;
        String childId = pannableCanvas.getCurrentNode();
        Rectangle child = (Rectangle) pannableCanvas.lookup("#" + childId);

        child.setStroke(Color.RED);
        child.setStrokeType(StrokeType.OUTSIDE);
        child.setStrokeWidth(3);


        ChangeListener<String> listener = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> obs, String oldValue, String newValue) {
                if (newValue != null) {
                    Rectangle father = (Rectangle) pannableCanvas.lookup("#" + newValue);

                    if(validate(childId, newValue)) {
                        Line line = new Line();
                        line.startXProperty().bind(Bindings.add(child.translateXProperty(), 0.5 * child.getWidth()));
                        line.startYProperty().bind(child.translateYProperty());
                        line.endXProperty().bind(Bindings.add(father.translateXProperty(), 0.5 * father.getWidth()));
                        line.endYProperty().bind(Bindings.add(father.translateYProperty(), father.getHeight()));

                        pannableCanvas.getChildren().add(line);
                    }
                }
                pannableCanvas.currentNodeProperty().removeListener(this);
                child.setStroke(null);
            }
        };
        pannableCanvas.currentNodeProperty().addListener(listener);

//        mainViewController.getFamilyMembersHashMap().get(familyMemberId).setFatherId();
    }

    private boolean validate(String childId, String fatherId) {
        Map<String, FamilyMember> family = mainViewController.getFamilyMembersHashMap();
        LocalDate fatherBirthDate = family.get(fatherId).getBirthDate();
        LocalDate childBirthDate = family.get(childId).getBirthDate();
        if (fatherBirthDate != null && childBirthDate != null) {
            if(fatherBirthDate.isAfter(childBirthDate)) {
                AlertBox.display("Invalid family member", "Father have to be older!");
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
    }

    public final void setVisible(boolean b) {
        rightPanelVBox.setVisible(b);
    }

}
