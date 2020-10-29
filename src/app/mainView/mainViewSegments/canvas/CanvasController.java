package app.mainView.mainViewSegments.canvas;

import app.mainView.mainViewSegments.MainViewSegment;
import basics.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

public class CanvasController extends MainViewSegment implements Initializable {

    @FXML
    public AnchorPane anchorPane;

    @FXML
    public PannableCanvas pannableCanvas;

//    private HashMap<FamilyMember, Point2D.Float> shapesCordsArrayList = new HashMap<>();


    public  void addMemberToBoard(FamilyMember familyMember) {
        addMemberToBoard(familyMember, 0f, 0f);
    }

    public void addMemberToBoard(FamilyMember familyMember, float posX, float posY) {

        addBoxToBoard(familyMember, posX, posY);

        SceneGestures sceneGestures = new SceneGestures(pannableCanvas);
        Scene scene = (Scene) pannableCanvas.getScene();
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
        scene.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());
    }

    public void addBoxToBoard(FamilyMember familyMember, float posX, float posY) {

        NodeGestures nodeGestures = new NodeGestures(pannableCanvas);

        Rectangle rectangle = new Rectangle(100, 50);
        rectangle.setId(familyMember.getId());
        rectangle.setTranslateX(posX);
        rectangle.setTranslateY(posY);
        rectangle.setStroke(Color.BLUE);
        rectangle.setFill(Color.BLUE.deriveColor(1, 1, 1, 0.5));
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

        //instead of this, better make bounds for text length (otherwise rectangle can be to big)
        rectangle.widthProperty().bind(personalDataLabel.widthProperty());

        pannableCanvas.getChildren().addAll(rectangle, personalDataLabel);
    }

    public void clipChildren(Region region) {
        final Rectangle clip = new Rectangle();

        region.setClip(clip);

        region.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
            clip.setWidth(newValue.getWidth());
            clip.setHeight(newValue.getHeight());
        });
    }

    public PannableCanvas getPannableCanvas() {
        return pannableCanvas;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clipChildren(anchorPane);
    }
}
