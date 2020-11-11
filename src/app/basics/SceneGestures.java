package app.basics;

import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import static java.lang.System.currentTimeMillis;

/**
 * Listeners for making the scene's canvas draggable and zoomable
 */
public class SceneGestures {

    PannableCanvas pannableCanvas;

    private static final double MAX_SCALE = 5.0d;
    private static final double MIN_SCALE = .1d;

    private long timePressed;
    private long timeReleased;

    private final DragContext sceneDragContext = new DragContext();

    private final EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {

            timePressed = currentTimeMillis();

            // right mouse button => panning
            if (!event.isSecondaryButtonDown())
                return;

            sceneDragContext.mouseAnchorX = event.getSceneX();
            sceneDragContext.mouseAnchorY = event.getSceneY();

            sceneDragContext.translateAnchorX = pannableCanvas.getTranslateX();
            sceneDragContext.translateAnchorY = pannableCanvas.getTranslateY();
        }
    };

    private final EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<>() {
        public void handle(MouseEvent event) {
            // right mouse button => panning
            if (!event.isSecondaryButtonDown())
                return;

            pannableCanvas.setTranslateX(sceneDragContext.translateAnchorX + event.getSceneX() - sceneDragContext.mouseAnchorX);
            pannableCanvas.setTranslateY(sceneDragContext.translateAnchorY + event.getSceneY() - sceneDragContext.mouseAnchorY);

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
                pannableCanvas.setCurrentNodeId(null);
                pannableCanvas.setCurrentRectangle(null);
            }

        }
    };

    private long mousePressedDuration() {
        return timeReleased - timePressed;
    }

    /**
     * Mouse wheel handler: zoom to pivot point
     */
    private final EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<>() {

        @Override
        public void handle(ScrollEvent event) {

            double scale = pannableCanvas.getScale(); // currently we only use Y, same value is used for X
            double oldScale = scale;

            scale *= Math.pow(1.002, event.getDeltaY());

            if (scale <= MIN_SCALE) {
                scale = MIN_SCALE;
            } else if (scale >= MAX_SCALE) {
                scale = MAX_SCALE;
            }

            double f = (scale / oldScale) - 1;

            double dx = (event.getSceneX() - (pannableCanvas.getBoundsInParent().getWidth() / 2 + pannableCanvas.getBoundsInParent().getMinX()));
            double dy = (event.getSceneY() - (pannableCanvas.getBoundsInParent().getHeight() / 2 + pannableCanvas.getBoundsInParent().getMinY()));

            pannableCanvas.setScale(scale);
            pannableCanvas.setPivot(f * dx, f * dy);

            event.consume();
        }
    };


    public SceneGestures(PannableCanvas pannableCanvas) {
        this.pannableCanvas = pannableCanvas;
    }

    public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
        return onMousePressedEventHandler;
    }

    public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
        return onMouseDraggedEventHandler;
    }

    public EventHandler<MouseEvent> getOnMouseReleasedEventHandler() {
        return onMouseReleasedEventHandler;
    }

    public EventHandler<MouseEvent> getOnMouseClickedEventHandler() {
        return onMouseClickedEventHandler;
    }

    public EventHandler<ScrollEvent> getOnScrollEventHandler() {
        return onScrollEventHandler;
    }

    public static double getMaxScale() {
        return MAX_SCALE;
    }

    public static double getMinScale() {
        return MIN_SCALE;
    }
}
