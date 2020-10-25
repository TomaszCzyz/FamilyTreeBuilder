package app.mainView.mainViewSegments.canvas;

import app.mainView.MainViewController;
import app.mainView.mainViewSegments.MainViewSegment;
import basics.FamilyMember;
import basics.NodeGestures;
import basics.PannableCanvas;
import basics.SceneGestures;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;

import java.awt.geom.Point2D;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class CanvasController extends MainViewSegment implements Initializable {

    @FXML
    public AnchorPane anchorPane;

    @FXML
    public PannableCanvas pannableCanvas;

//    private HashMap<FamilyMember, Point2D.Float> shapesOnBoardArrayList = new HashMap<>();


    public void addMemberToBoard(FamilyMember familyMember, float x, float y) {

        NodeGestures nodeGestures = new NodeGestures(pannableCanvas);

        double initialPosition = 20d;
        DoubleProperty minWidth = new SimpleDoubleProperty(150);

        Rectangle rectangle = new Rectangle(100, 50);
//        rectangle.set
        rectangle.setTranslateX(initialPosition);
        rectangle.setTranslateY(initialPosition);
        rectangle.setStroke(Color.BLUE);
        rectangle.setFill(Color.BLUE.deriveColor(1, 1, 1, 0.5));
        rectangle.addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        rectangle.addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());

        Label personalDataLabel = new Label();
        personalDataLabel.setMouseTransparent(true);
        personalDataLabel.setTranslateX(initialPosition + 10);
        personalDataLabel.setTranslateY(initialPosition + 10);
        personalDataLabel.translateXProperty().bind(rectangle.translateXProperty());
        personalDataLabel.translateYProperty().bind(rectangle.translateYProperty());
        personalDataLabel.setText(familyMember.getFirstName() + " " + familyMember.getLastName() + "\n" +
                "secondname: " + familyMember.getSecondName() + "\n" +
                "-----------------------------"); //it determines min rectangle width

        //instead of this, better make bounds for text length (otherwise rectangle can be to big)
        rectangle.widthProperty().bind(personalDataLabel.widthProperty());

        pannableCanvas.getChildren().addAll(rectangle, personalDataLabel);

        Scene scene = (Scene) pannableCanvas.getScene();
        SceneGestures sceneGestures = new SceneGestures(pannableCanvas);
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
        scene.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());

//        pannableCanvas.addGrid();
    }


    public PannableCanvas getPannableCanvas() {
        return pannableCanvas;
    }


    public void clipChildren(Region region) {
        final Rectangle clip = new Rectangle();

        region.setClip(clip);

        region.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
            clip.setWidth(newValue.getWidth());
            clip.setHeight(newValue.getHeight());
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clipChildren(anchorPane);
    }
}
