package app.basics;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import static java.lang.System.currentTimeMillis;

/**
 * Listeners for making the nodes draggable via left mouse button. Considers if parent is zoomed.
 */
public class NodeGestures {

    PannableCanvas pannableCanvas;

    private long timePressed;
    private long timeReleased;

    private final DragContext nodeDragContext = new DragContext();

    private final EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {

            timePressed = currentTimeMillis();

            // left mouse button => dragging
            if (!event.isPrimaryButtonDown())
                return;

            nodeDragContext.mouseAnchorX = event.getSceneX();
            nodeDragContext.mouseAnchorY = event.getSceneY();

            Node node = (Node) event.getSource();

            //this event can be initiate by FamilyMemberBox or BoxUnion(Group)
            //but when we move BoxUnion we must have FamilyMemberBox's translateX(Y) values,
            //because BoxUnion does not have ones
            if(node.getClass().equals(FamilyMemberBox.class)){
                nodeDragContext.translateAnchorX = node.getTranslateX();
                nodeDragContext.translateAnchorY = node.getTranslateY();
            }
        }
    };

    private final EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            // left mouse button => dragging
            if (!event.isPrimaryButtonDown())
                return;

            double scale = pannableCanvas.getScale();

            Node node = (Node) event.getSource();

            System.out.println("Dragged node: " + node.getId());

            double x = nodeDragContext.translateAnchorX + ((event.getSceneX() - nodeDragContext.mouseAnchorX) / scale);
            double y = nodeDragContext.translateAnchorY + ((event.getSceneY() - nodeDragContext.mouseAnchorY) / scale);

            if (node instanceof Group){
                Group unionGroup = (Group) node;
                for (Node node1 : unionGroup.getChildren()) {
                    FamilyMemberBox familyMemberBox = (FamilyMemberBox) node1;
                    familyMemberBox.setTranslateX(x);
                    familyMemberBox.setTranslateY(y);
                }
            }
            event.consume();
        }
    };

    private final EventHandler<MouseEvent> onMouseReleasedEventHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {

            timeReleased = currentTimeMillis();

        }
    };


    private final EventHandler<MouseEvent> onMouseClickedEventHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {

            if (event.getButton() != MouseButton.PRIMARY)
                return;

            if(mousePressedDuration() < 200) {
                Node node = (Node) event.getSource();

                //order matters because setting currentNodeId initialize listener
                pannableCanvas.setCurrentBox((FamilyMemberBox) node);
                pannableCanvas.setCurrentBoxId(node.getId());

                event.consume();
            }
        }
    };


    public NodeGestures(PannableCanvas pannableCanvas) {
        this.pannableCanvas = pannableCanvas;
    }

    private long mousePressedDuration() {
        return timeReleased - timePressed;
    }

    public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
        return onMousePressedEventHandler;
    }

    public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
        return onMouseDraggedEventHandler;
    }

    public EventHandler<MouseEvent> getOnMouseClickedEventHandler() {
        return onMouseClickedEventHandler;
    }

    public EventHandler<MouseEvent> getOnMouseReleasedEventHandler() {
        return onMouseReleasedEventHandler;
    }
}
