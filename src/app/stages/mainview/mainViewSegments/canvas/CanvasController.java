package app.stages.mainview.mainViewSegments.canvas;

import app.stages.mainview.mainViewSegments.MainViewSegment;
import app.basics.FamilyMember;
import app.basics.NodeGestures;
import app.basics.PannableCanvas;
import app.basics.SceneGestures;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

public class CanvasController extends MainViewSegment implements Initializable {

    @FXML
    public PannableCanvas pannableCanvas;

    @FXML
    public AnchorPane anchorPane;


    public void initializeSceneGestures() {
        SceneGestures sceneGestures = new SceneGestures(pannableCanvas);
        anchorPane.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
        anchorPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
        anchorPane.addEventFilter(MouseEvent.MOUSE_RELEASED, sceneGestures.getOnMouseDraggedEventHandler());
        //eventHandler is used here because it is important to analyze familyMemberBox event first
        anchorPane.addEventHandler(MouseEvent.MOUSE_CLICKED, sceneGestures.getOnMouseClickedEventHandler());
        anchorPane.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());
    }

    public void addMemberToBoard(FamilyMember familyMember) {

        addMemberToBoard(familyMember, 0f, 0f);

    }


    public void addMemberToBoard(FamilyMember familyMember, float posX, float posY) {

        Rectangle rectangle = new Rectangle(100, 50);
        rectangle.setId(familyMember.getId());
        rectangle.setTranslateX(posX);
        rectangle.setTranslateY(posY);

        rectangle.setStroke(Color.BLUE);
        rectangle.setFill(Color.BLUE.deriveColor(1, 1, 1, 0.5));

        NodeGestures nodeGestures = new NodeGestures(pannableCanvas);
        //event order matters!
        rectangle.addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        rectangle.addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());
        rectangle.addEventFilter(MouseEvent.MOUSE_RELEASED, nodeGestures.getOnMouseReleasedEventHandler());
        rectangle.addEventFilter(MouseEvent.MOUSE_CLICKED, nodeGestures.getOnMouseClickedEventHandler());

        Label personalDataLabel = new Label();
        personalDataLabel.setId(familyMember.getId());
        personalDataLabel.setMouseTransparent(true);
        personalDataLabel.setTranslateX(posX + 10);
        personalDataLabel.setTranslateY(posY + 10);
        personalDataLabel.translateXProperty().bind(rectangle.translateXProperty());
        personalDataLabel.translateYProperty().bind(rectangle.translateYProperty());
        personalDataLabel.setText(familyMember.getFirstName() + " " + familyMember.getLastName() + "\n" +
                "secondname: " + familyMember.getSecondName() + "\n" +
                "-----------------------------"); //it determines min rectangle width

        //instead of this, better make bounds for text length (otherwise rectangle can be too big)
        rectangle.widthProperty().bind(personalDataLabel.widthProperty());

        pannableCanvas.getChildren().addAll(rectangle, personalDataLabel);
    }

    public void delBoxFromCanvas(String familyMemberId) {
        for(int i = 0; i < 2; ++i) {    //we need to remove rectangle and label
            Node node = pannableCanvas.lookup("#" + familyMemberId);
            if(node != null) {
                pannableCanvas.getChildren().remove(node);
            }
        }
    }

    public void delLinkFromTo(String startId, String endId) {
        Node node = pannableCanvas.lookup("#" + startId + endId);
        if(node != null) {
            pannableCanvas.getChildren().remove(node);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }
}
