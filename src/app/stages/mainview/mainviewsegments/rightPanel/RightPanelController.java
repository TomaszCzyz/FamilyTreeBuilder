package app.stages.mainview.mainviewsegments.rightPanel;

import app.basics.LinkType;
import app.stages.mainview.mainviewsegments.MainViewSegment;
import app.basics.ConfirmBox;
import app.basics.FamilyMember;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
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
    public Text spouseText;
    @FXML
    public Button spouseLinkButton;
    @FXML
    public Button delMotherLinkButton;


    @FXML
    public void handleMotherLinkButtonAction() {
        canvasController.addLinkFrom(canvasController.pannableCanvas.getCurrentRectangle(), LinkType.MOTHER);
    }


    @FXML
    public void handleSpouseLinkButtonAction() {
        canvasController.addLinkFrom(canvasController.pannableCanvas.getCurrentRectangle(), LinkType.SPOUSE);
    }


    @FXML
    public void handleDelMotherLinkButtonAction() {

        boolean answer = ConfirmBox.display("Warning", "Sure you want to delete connection to mother?");
        if (answer) {
            String childId = canvasController.pannableCanvas.getCurrentNodeId();
            String motherId = mainViewController.getFamilyMembersHashMap().get(childId).getMotherId();

            mainViewController.getFamilyMembersHashMap().get(childId).setMotherId("");
            mainViewController.getCanvasController().delLinkFromTo(childId, motherId);

            //refresh rightPanel (to change button del(Link) to link)
            fillRightPanel(mainViewController.getFamilyMembersHashMap().get(childId));
        }
    }


    public void fillRightPanel(FamilyMember familyMember) {
        firstNameText.setText(familyMember.getFirstName());
        secondNameText.setText(familyMember.getSecondName());
        lastNameText.setText(familyMember.getLastName());
        birthDateText.setText(String.valueOf(familyMember.getBirthDate()));

        if (!familyMember.getMotherId().equals("")) {
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
