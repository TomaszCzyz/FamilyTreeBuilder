package app.mainView.mainViewSegments.rightPanel;

import app.mainView.mainViewSegments.MainViewSegment;
import basics.FamilyMember;
import basics.PannableCanvas;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;

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
        String familyMemberId = pannableCanvas.getCurrentNode();
        Rectangle child = (Rectangle) pannableCanvas.lookup("#" + familyMemberId);

        child.setStroke(Color.RED);
        child.setStrokeType(StrokeType.OUTSIDE);
        child.setStrokeWidth(3);

//        pannableCanvas.setSelectFather(true);

        ChangeListener<String> listener = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> obs, String ov, String  nv)  {
                if (nv != null) {
                    child.setStroke(Color.GREEN);
                    System.out.println("here0");
                }
                System.out.println("here1" + nv);
                pannableCanvas.currentNodeProperty().removeListener(this);
            }
        };
        pannableCanvas.currentNodeProperty().addListener(listener);

//        mainViewController.getFamilyMembersHashMap().get(familyMemberId).setFatherId();

//        pannableCanvas.currentNodeProperty().addListener((v, oldValue, newValue) -> {
//            if (newValue != null) {
//                System.out.println(newValue);
//            }
//        });

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
