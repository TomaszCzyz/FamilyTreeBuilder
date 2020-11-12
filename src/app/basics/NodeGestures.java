package app.basics;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

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
            nodeDragContext.translateAnchorX = node.getTranslateX();
            nodeDragContext.translateAnchorY = node.getTranslateY();
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
            node.setTranslateX(nodeDragContext.translateAnchorX + ((event.getSceneX() - nodeDragContext.mouseAnchorX) / scale));
            node.setTranslateY(nodeDragContext.translateAnchorY + ((event.getSceneY() - nodeDragContext.mouseAnchorY) / scale));

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
//                Node node = (Node) event.getSource();
                Rectangle rectangle = (Rectangle) event.getSource();

                //order matters because setting currentNodeId initialize listener
                pannableCanvas.setCurrentRectangle((Rectangle) rectangle);
                pannableCanvas.setCurrentNodeId(rectangle.getId());

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