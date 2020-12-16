package app.stages.mainview;

import app.basics.FamilyMember;
import app.basics.FamilyMemberBox;
import app.basics.NodeGestures;
import app.configuration.Configuration;
import app.stages.mainview.mainviewsegments.bottom.BottomController;
import app.stages.mainview.mainviewsegments.canvas.CanvasController;
import app.stages.mainview.mainviewsegments.leftPanel.LeftPanelController;
import app.stages.mainview.mainviewsegments.menuBar.MenuBarController;
import app.stages.mainview.mainviewsegments.rightPanel.RightPanelController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

    private final Logger logger = LoggerFactory.getLogger(MainViewController.class);

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

    private final Configuration configuration;

    private final HashMap<String, FamilyMember> familyMembersHashMap;


    public MainViewController() {
        this.configuration = Configuration.getInstance();
        this.familyMembersHashMap = new HashMap<>();
    }


    public void printFamilyMembersHashMap() {
        logger.info("[ ");
        for (var member : familyMembersHashMap.values()) {
            logger.info("{}", member);
        }
        logger.info(" ]");
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
        canvasController.pannableCanvas.currentBoxIdProperty().addListener((v, oldValue, newValue) -> {
            FamilyMemberBox newFamilyMemberBox = canvasController.pannableCanvas.getCurrentBox();
            rightPanelController.setVisible(newFamilyMemberBox != null);

            if(newFamilyMemberBox != null){
                rightPanelController.fillRightPanel(familyMembersHashMap.get(newFamilyMemberBox.getId()));


                newFamilyMemberBox.getRectangle().setStroke(Color.ORANGE);
                newFamilyMemberBox.getRectangle().setStrokeType(StrokeType.OUTSIDE);
                newFamilyMemberBox.getRectangle().setStrokeWidth(3);
            }
            if (oldValue != null) {
                //after deleting from canvas, rectangle with oldValue can be null
                if(canvasController.getBoxesMap().containsKey(oldValue)) {
                    FamilyMemberBox oldBox = canvasController.getBoxesMap().get(oldValue);
                    oldBox.setSelect(false);
                }
            }
            logger.info("new: {}", newValue);
        });

        centerHbox.setBorder(new Border(new BorderStroke(Color.BLUE,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        canvasController.anchorPane.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    }
}






