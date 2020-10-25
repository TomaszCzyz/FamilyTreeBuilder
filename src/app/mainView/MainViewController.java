package app.mainView;

import app.mainView.mainViewSegments.bottom.BottomController;
import app.mainView.mainViewSegments.canvas.CanvasController;
import app.mainView.mainViewSegments.leftPanel.LeftPanelController;
import app.mainView.mainViewSegments.menuBar.MenuBarController;
import app.mainView.mainViewSegments.rightPanel.RightPanelController;
import basics.FamilyMember;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

    private final List<FamilyMember> familyMembersArrayList;


    @FXML
    public BorderPane borderPane;
    @FXML
    private MenuBarController menuBarController;
    @FXML
    private LeftPanelController leftPanelController;
    @FXML
    private RightPanelController rightPanelController;
    @FXML
    private CanvasController canvasController;
    @FXML
    private BottomController bottomController;

    public MainViewController() {
        familyMembersArrayList = new ArrayList<>();
    }

    public void printFamilyMembersArrayList() {
        System.out.println("[ ");
        for (var member : familyMembersArrayList) {
            System.out.println(member);
        }
        System.out.println(" ]");
    }

    public List<FamilyMember> getFamilyMembersArrayList() {
        return familyMembersArrayList;
    }

    public MenuBarController getMenuBarController() {
        return menuBarController;
    }

    public LeftPanelController getLeftPanelController() {
        return leftPanelController;
    }

    public RightPanelController getRightPanelController() {
        return rightPanelController;
    }

    public CanvasController getCanvasController() {
        return canvasController;
    }

    public BottomController getBottomController() {
        return bottomController;
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        menuBarController.injectMainViewController(this);
        leftPanelController.injectMainViewController(this);
        canvasController.injectMainViewController(this);
        rightPanelController.injectMainViewController(this);
        bottomController.injectMainViewController(this);

    }
}






