package basics;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import static java.lang.System.currentTimeMillis;

/**
 * Listeners for making the scene's canvas draggable and zoomable
 */
public class SceneGestures {

    PannableCanvas canvas;

    private static final double MAX_SCALE = 5.0d;
    private static final double MIN_SCALE = .1d;

    private long timePressed;
    private long timeReleased;

    private final DragContext sceneDragContext = new DragContext();

    private final EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {

            timePressed = currentTimeMillis();

            // right mouse button => panning
            if (!event.isSecondaryButtonDown())
                return;

            sceneDragContext.mouseAnchorX = event.getSceneX();
            sceneDragContext.mouseAnchorY = event.getSceneY();

            sceneDragContext.translateAnchorX = canvas.getTranslateX();
            sceneDragContext.translateAnchorY = canvas.getTranslateY();
        }
    };

    private final EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
            // right mouse button => panning
            if (!event.isSecondaryButtonDown())
                return;

            canvas.setTranslateX(sceneDragContext.translateAnchorX + event.getSceneX() - sceneDragContext.mouseAnchorX);
            canvas.setTranslateY(sceneDragContext.translateAnchorY + event.getSceneY() - sceneDragContext.mouseAnchorY);

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
                canvas.setCurrentNode(null);
            }
        }
    };

    private long mousePressedDuration() {
        return timeReleased - timePressed;
    }
    /**
     * Mouse wheel handler: zoom to pivot point
     */
    private final EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>() {

        @Override
        public void handle(ScrollEvent event) {

            double scale = canvas.getScale(); // currently we only use Y, same value is used for X
            double oldScale = scale;

            scale *= Math.pow(1.002, event.getDeltaY());

            if (scale <= MIN_SCALE) {
                scale = MIN_SCALE;
            } else if (scale >= MAX_SCALE) {
                scale = MAX_SCALE;
            }

            double f = (scale / oldScale) - 1;

            double dx = (event.getSceneX() - (canvas.getBoundsInParent().getWidth() / 2 + canvas.getBoundsInParent().getMinX()));
            double dy = (event.getSceneY() - (canvas.getBoundsInParent().getHeight() / 2 + canvas.getBoundsInParent().getMinY()));

            canvas.setScale(scale);
            canvas.setPivot(f * dx, f * dy);

            event.consume();
        }
    };


    public SceneGestures(PannableCanvas canvas) {
        this.canvas = canvas;
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
