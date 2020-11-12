package app.stages.mainview;

import app.configuration.Configuration;
import app.stages.mainview.mainViewSegments.bottom.BottomController;
import app.stages.mainview.mainViewSegments.canvas.CanvasController;
import app.stages.mainview.mainViewSegments.leftPanel.LeftPanelController;
import app.stages.mainview.mainViewSegments.menuBar.MenuBarController;
import app.stages.mainview.mainViewSegments.rightPanel.RightPanelController;
import app.basics.FamilyMember;
import app.basics.PannableCanvas;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

    @FXML
    public HBox centerHbox;

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

    private final Configuration configuration = Configuration.getInstance();

    private final HashMap<String, FamilyMember> familyMembersHashMap = new HashMap<>();


    public void printFamilyMembersHashMap() {
        System.out.println("[ ");
        for (var member : familyMembersHashMap.values()) {
            System.out.println(member);
        }
        System.out.println(" ]");
    }


    public Map<String, FamilyMember> getFamilyMembersHashMap() {
        return familyMembersHashMap;
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

    public Configuration getConfiguration() {
        return configuration;
    }

    public void clipChildren(Region region) {
        final Rectangle clip = new Rectangle();

        region.setClip(clip);

        region.layoutBoundsProperty().addListener((v, oldValue, newValue) -> {
            clip.setWidth(newValue.getWidth());
            clip.setHeight(newValue.getHeight());
        });
    }

    public void closeProgram() {
        if (menuBarController.ifCanSaveProperty().getValue()) {
            configuration.getProperties().setLastSession(menuBarController.getSaveURL());
            Configuration.updateProperties();
        }
        Platform.exit();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        menuBarController.injectMainViewController(this);
        leftPanelController.injectMainViewController(this);
        canvasController.injectMainViewController(this);
        rightPanelController.injectMainViewController(this);
        bottomController.injectMainViewController(this);

        menuBarController.injectCanvasController(canvasController);
        leftPanelController.injectCanvasController(canvasController);
        rightPanelController.injectCanvasController(canvasController);
        bottomController.injectCanvasController(canvasController);

        canvasController.initializeSceneGestures();
        clipChildren(centerHbox);
        rightPanelController.setVisible(false);

        //open last session if can
        if (configuration.getProperties().getLastSession() != null) {
//            menuBarController.openExistingFamilyTree(configuration.getProperties().getLastSession());
            menuBarController.openExistingFamilyTree("outfile.csv");
        }

        //action when user click on rectangle or on board
        canvasController.pannableCanvas.currentNodeIdProperty().addListener((v, oldValue, newValue) -> {
            System.out.println("new: " + newValue);
            rightPanelController.setVisible(newValue != null);

            if (newValue != null) {
                rightPanelController.fillRightPanel(familyMembersHashMap.get(newValue));
                Rectangle newRectangle = canvasController.pannableCanvas.getCurrentRectangle();

                newRectangle.setStroke(Color.ORANGE);
                newRectangle.setStrokeType(StrokeType.OUTSIDE);
                newRectangle.setStrokeWidth(3);
            }
            if (oldValue != null) {
                Rectangle oldRectangle = (Rectangle) canvasController.pannableCanvas.lookup("#" + oldValue);
                oldRectangle.setStroke(Color.BLUE);
                oldRectangle.setStrokeType(StrokeType.CENTERED);
                oldRectangle.setStrokeWidth(1);
            }
        });

        centerHbox.setBorder(new Border(new BorderStroke(Color.BLUE,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        canvasController.anchorPane.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    }
}






