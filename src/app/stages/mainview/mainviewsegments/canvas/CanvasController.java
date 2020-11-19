package app.stages.mainview.mainviewsegments.canvas;

import app.basics.*;
import app.stages.mainview.mainviewsegments.MainViewSegment;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class CanvasController extends MainViewSegment implements Initializable {

    @FXML
    public PannableCanvas pannableCanvas;

    @FXML
    public AnchorPane anchorPane;

    private final Map<String, Rectangle> rectangles;

    private final Map<String, Set<String>> tiedGroups;


    public CanvasController() {
        rectangles = new HashMap<>();
        tiedGroups = new HashMap<>();
    }


    public void initializeSceneGestures() {

        SceneGestures sceneGestures = new SceneGestures(pannableCanvas);
        anchorPane.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
        anchorPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
        anchorPane.addEventFilter(MouseEvent.MOUSE_RELEASED, sceneGestures.getOnMouseDraggedEventHandler());
        //eventHandler is used here(instead of eventFilter) because it is important to analyze familyMemberBox event first
        anchorPane.addEventHandler(MouseEvent.MOUSE_CLICKED, sceneGestures.getOnMouseClickedEventHandler());
        anchorPane.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());
    }

    public void addMemberToBoard(FamilyMember familyMember) {
        Rectangle rectangle = new Rectangle(100, 50);
        rectangle.setId(familyMember.getId());

        rectangles.put(familyMember.getId(), rectangle);

        rectangle.setTranslateX(familyMember.getPosX());
        rectangle.setTranslateY(familyMember.getPosY());

        rectangle.setStroke(Color.BLUE);
        rectangle.setFill(Color.BLUE.deriveColor(1, 1, 1, 0.5));

        NodeGestures nodeGestures = new NodeGestures(pannableCanvas);
        //event order matters!
        rectangle.addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        rectangle.addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());
        rectangle.addEventFilter(MouseEvent.MOUSE_RELEASED, nodeGestures.getOnMouseReleasedEventHandler());
        rectangle.addEventFilter(MouseEvent.MOUSE_CLICKED, nodeGestures.getOnMouseClickedEventHandler());

        Text personalDataLabel = new Text();
        personalDataLabel.setId(familyMember.getId() + "text");
        personalDataLabel.setMouseTransparent(true);
        personalDataLabel.translateXProperty().bind(Bindings.add(rectangle.translateXProperty(), 10d));
        personalDataLabel.translateYProperty().bind(Bindings.add(rectangle.translateYProperty(), 15d));
        personalDataLabel.setText(familyMember.getFirstName() + " " + familyMember.getLastName() + "\n" +
                "secondname: " + familyMember.getSecondName() + "\n" +
                "-------------"); //it determines min rectangle width

        personalDataLabel.setWrappingWidth(100);
        rectangle.widthProperty().bind(personalDataLabel.wrappingWidthProperty());

        pannableCanvas.getChildren().addAll(rectangle, personalDataLabel);
    }


    /*
     * linke startRectangle with next selected currentNode with link of type linkType
     */
    public void addLinkFrom(Rectangle startRectangle, LinkType linkType) {
        //setMouseTransparent is used to prevent pressing buttons in right panel till linking operation ends
        mainViewController.getRightPanelController().rightPanelVBox.setDisable(true);
        markRectangle(startRectangle, linkType);

        ChangeListener<String> listener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends String> obs, String oldValue, String newValue) {

                if (newValue != null && validate(startRectangle.getId(), newValue)) {

                    Rectangle endRectangle = pannableCanvas.getCurrentRectangle();

                    createLineFromTo(startRectangle, endRectangle, linkType);

                    switch (linkType) {
                        case MOTHER:
                            mainViewController.getFamilyMembersHashMap().get(startRectangle.getId()).setMotherId(endRectangle.getId());
                            break;
                        case SPOUSE:
                            mainViewController.getFamilyMembersHashMap().get(startRectangle.getId()).getPartners().add(endRectangle.getId());
                            mainViewController.getFamilyMembersHashMap().get(endRectangle.getId()).getPartners().add(startRectangle.getId());
                            break;
                    }
                }
                unmarkRectangle(startRectangle);
                mainViewController.getRightPanelController().rightPanelVBox.setDisable(false);
                pannableCanvas.currentNodeIdProperty().removeListener(this);
            }
        };
        pannableCanvas.currentNodeIdProperty().addListener(listener);
    }


    public void createLineFromTo(Rectangle start, Rectangle end, LinkType linkType) {
        Line line = new Line();
        line.setId(start.getId() + end.getId());

        switch (linkType) {
            case MOTHER:
                line.startXProperty().bind(Bindings.add(start.translateXProperty(), 0.5 * start.getWidth()));
                line.startYProperty().bind(start.translateYProperty());
                line.endXProperty().bind(Bindings.add(end.translateXProperty(), 0.5 * end.getWidth()));
                line.endYProperty().bind(Bindings.add(end.translateYProperty(), end.getHeight()));
                break;
            case SPOUSE:
                /*
                Depending on which rectangle is on the right/left, connecting line changes its anchor points
                in other words: line is always between rectangles
                */
                line.startXProperty().bind(Bindings
                        .when(start.translateXProperty().greaterThan(end.translateXProperty()))
                        .then(start.translateXProperty())
                        .otherwise(Bindings.add(start.translateXProperty(), start.getWidth())));
                line.startYProperty().bind(Bindings.add(start.translateYProperty(), 0.5 * start.getHeight()));

                line.endXProperty().bind(Bindings
                        .when(start.translateXProperty().lessThan(end.translateXProperty()))
                        .then(end.translateXProperty())
                        .otherwise(Bindings.add(end.translateXProperty(), end.getWidth())));
                line.endYProperty().bind(Bindings.add(end.translateYProperty(), 0.5 * end.getHeight()));
                break;
        }
        line.toBack();
        pannableCanvas.getChildren().add(line);
    }

    public void markRectangle(Rectangle r, Color c) {
        r.setFill(c);
    }

    private void markRectangle(Rectangle r, LinkType linkType) {
        r.setStrokeType(StrokeType.OUTSIDE);
        r.setStrokeWidth(3);
        switch (linkType) {
            case MOTHER:
                r.setStroke(Color.RED);
                break;
            case SPOUSE:
                r.setStroke(Color.GREEN);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + linkType);
        }
    }


    private void unmarkRectangle(Rectangle r) {
        r.setStroke(Color.BLUE);
        r.setStrokeType(StrokeType.CENTERED);
        r.setStrokeWidth(1);
    }


    private boolean validate(String childId, String motherId) {
        Map<String, FamilyMember> family = mainViewController.getFamilyMembersHashMap();
        LocalDate fatherBirthDate = family.get(motherId).getBirthDate();
        LocalDate childBirthDate = family.get(childId).getBirthDate();
        if (fatherBirthDate != null && childBirthDate != null) {
            if (fatherBirthDate.isAfter(childBirthDate)) {
                AlertBox.display("Invalid family member", "Mother has to be older!");
                return false;
            }
        }
        return true;
    }


    public void delFromCanvas(String familyMemberId) {
        //writing all possible ids of nodes familiar with familyMemberId
        List<String> selectors = new ArrayList<>();
        selectors.add(familyMemberId);
        selectors.add(familyMemberId + "text");

        String motherId = mainViewController.getFamilyMembersHashMap().get(familyMemberId).getMotherId();
        if (!motherId.isEmpty()) {
            selectors.add(familyMemberId + motherId);
        }

        mainViewController.getFamilyMembersHashMap().forEach((id, familyMember) -> {
            if (!familyMember.getMotherId().isEmpty() && familyMemberId.equals(familyMember.getMotherId())) {
                //this means that there is link (from child to mother) to Rectangle which we want to delete
                selectors.add(familyMember.getId() + familyMemberId);
            }
        });

        List<String> partners = mainViewController.getFamilyMembersHashMap().get(familyMemberId).getPartners();
        partners.forEach(partnerId -> selectors.addAll(Arrays.asList(familyMemberId + partnerId, partnerId + familyMemberId)));

        //search for all nodes for each selector
        Set<Node> nodes = new HashSet<>();
        selectors.forEach(selector -> nodes.addAll(pannableCanvas.lookupAll("#" + selector)));

        pannableCanvas.getChildren().removeAll(nodes);
    }


    public void delLinkFromTo(String startId, String endId) {
        Node node = pannableCanvas.lookup("#" + startId + endId);
        if (node != null) {
            pannableCanvas.getChildren().remove(node);
        }
    }

    public Map<String, Rectangle> getRectangles() {
        return rectangles;
    }

    public Map<String, Set<String>> getTiedGroups() {
        return tiedGroups;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pannableCanvas.setLayoutX(100d);
        pannableCanvas.setLayoutY(100d);
    }
}
