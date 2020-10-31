package app.mainView.mainViewSegments.rightPanel;

import app.mainView.mainViewSegments.MainViewSegment;
import basics.FamilyMember;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class RightPanelController extends MainViewSegment implements Initializable {

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

    public RightPanelController() {
        System.out.println("Here0");
    }

    public final void setVisible(boolean b) {
        rightPanelVBox.setVisible(b);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
