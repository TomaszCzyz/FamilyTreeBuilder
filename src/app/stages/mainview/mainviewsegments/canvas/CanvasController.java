package app.stages.mainview.mainviewsegments.canvas;

import app.basics.*;
import app.stages.mainview.mainviewsegments.MainViewSegment;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class CanvasController extends MainViewSegment implements Initializable {

    @FXML
    public PannableCanvas pannableCanvas;

    @FXML
    public AnchorPane anchorPane;

    private final Map<String, FamilyMemberBox> boxesMap;

    private final Map<String, Group> boxesPartition;    //maps group,s color to group of familyMemberBoxes

    private final Map<String, Set<String>> tiedGroups;  //maps group's color to set of rectangles' ids

    private final Map<String, Line> linesMap;


    public CanvasController() {
        boxesMap = new HashMap<>();
        boxesPartition = new HashMap<>();
        tiedGroups = new HashMap<>();
        linesMap = new HashMap<>();
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


    /*Structure of nodes in pannableCanvas looks this:
    * PannableCanvas -> TiedGroup -> FamilyMemberGroup -> Rectangle + Text
    *
    * when new familyMember is added to pannableCanvas, that means Rectangle + Text inside Group is added,
    * he is pack into new Group representing new TiedGroup composed of one FamilyMemberGroup
    * */
    public void addFamilyMemberBox(FamilyMember familyMember) {
        FamilyMemberBox familyMemberBox = new FamilyMemberBox(familyMember);
        boxesMap.put(familyMember.getId(), familyMemberBox);
        boxesPartition.put("noColor", familyMemberBox);

        Group newGroup = new Group();
        newGroup.getChildren().add(familyMemberBox);


        familyMemberBox.setTranslateX(familyMember.getPosX());
        familyMemberBox.setTranslateY(familyMember.getPosY());

        NodeGestures nodeGestures = new NodeGestures(pannableCanvas);
        //event order matters!
        familyMemberBox.addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        familyMemberBox.addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());
        familyMemberBox.addEventFilter(MouseEvent.MOUSE_RELEASED, nodeGestures.getOnMouseReleasedEventHandler());
        familyMemberBox.addEventFilter(MouseEvent.MOUSE_CLICKED, nodeGestures.getOnMouseClickedEventHandler());

        pannableCanvas.getChildren().add(newGroup);
    }


    /*
     * linke startRectangle with next selected currentNode with link of type linkType
     */
    public void addLinkFrom(FamilyMemberBox startBox, LinkType linkType) {
        //setMouseTransparent is used to prevent pressing buttons in right panel till linking operation ends
        mainViewController.getRightPanelController().rightPanelVBox.setDisable(true);
        startBox.mark(linkType);

        ChangeListener<String> listener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends String> obs, String oldValue, String newValue) {

                if (newValue != null && validate(startBox.getId(), newValue)) {
                    FamilyMemberBox endBox = pannableCanvas.getCurrentBox();

                    createLineFromTo(startBox, endBox, linkType);

                    switch (linkType) {
                        case MOTHER:
                            mainViewController.getFamilyMembersHashMap().get(startBox.getId()).setMotherId(endBox.getId());
                            break;
                        case SPOUSE:
                            mainViewController.getFamilyMembersHashMap().get(startBox.getId()).getPartners().add(endBox.getId());
                            mainViewController.getFamilyMembersHashMap().get(endBox.getId()).getPartners().add(startBox.getId());
                            break;
                    }
                }
                startBox.setSelect(false);
//                unmarkRectangle(startBox.getRectangle());
                mainViewController.getRightPanelController().rightPanelVBox.setDisable(false);
                pannableCanvas.currentBoxIdProperty().removeListener(this);
            }
        };
        pannableCanvas.currentBoxIdProperty().addListener(listener);
    }


    public void createLineFromTo(FamilyMemberBox start, FamilyMemberBox end, LinkType linkType) {
        Line line = new Line();
        linesMap.put(start.getId() + end.getId(), line);
        line.setId(start.getId() + end.getId());

        switch (linkType) {
            case MOTHER:
                line.startXProperty().bind(start.translateXProperty().add(0.5 * start.getWidth()));
                line.startYProperty().bind(start.translateYProperty());
                line.endXProperty().bind(end.translateXProperty().add(0.5 * end.getWidth()));
                line.endYProperty().bind(end.translateYProperty().add(end.getHeight()));
                break;
            case SPOUSE:
                /*
                Depending on which rectangle is on the right/left, connecting line changes its anchor points
                in other words: line is always between rectangles
                */
                line.startXProperty().bind(Bindings
                        .when(start.translateXProperty().greaterThan(end.translateXProperty()))
                        .then(start.translateXProperty())
                        .otherwise(start.translateXProperty().add(start.getWidth())));
                line.startYProperty().bind(start.translateYProperty().add(0.5 * start.getHeight()));

                line.endXProperty().bind(Bindings
                        .when(start.translateXProperty().lessThan(end.translateXProperty()))
                        .then(end.translateXProperty())
                        .otherwise(end.translateXProperty().add(end.getWidth())));
                line.endYProperty().bind(end.translateYProperty().add(0.5 * end.getHeight()));
                break;
        }
        line.toBack();
        pannableCanvas.getChildren().add(line);
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
        //set of nodes to delete
        Set<Node> nodes = new HashSet<>();

        //line to mother
        String motherId = mainViewController.getFamilyMembersHashMap().get(familyMemberId).getMotherId();
        if (!motherId.isEmpty()) {
            nodes.add(linesMap.get(familyMemberId + motherId));
        }

        //line from child (because this familyMember can be mother), may not exists
        mainViewController.getFamilyMembersHashMap().forEach((id, familyMember) -> {
            if (!familyMember.getMotherId().isEmpty() && familyMemberId.equals(familyMember.getMotherId())) {
                nodes.add(linesMap.get(familyMember.getId() + familyMemberId));
            }
        });

        //lines to/from partners
        List<String> partnersIds = mainViewController.getFamilyMembersHashMap().get(familyMemberId).getPartners();
        partnersIds.forEach(partnerId -> {
            nodes.add(linesMap.get(familyMemberId + partnerId));    //one of this line has no effect,
            nodes.add(linesMap.get(partnerId + familyMemberId));    //because line only in one direction
        });
        pannableCanvas.getChildren().removeAll(nodes);

        //removing FamilyMemberBox
        boxesMap.get(familyMemberId).getChildren().clear();
    }


    public void delLinkFromTo(String startId, String endId) {
        Node node = pannableCanvas.lookup("#" + startId + endId);
        if (node != null) {
            pannableCanvas.getChildren().remove(node);
        }
    }

    public Map<String, FamilyMemberBox> getBoxesMap() {
        return boxesMap;
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
