package basics;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;


public class PannableCanvas extends Pane implements Initializable {

    DoubleProperty myScale = new SimpleDoubleProperty(1.0);

    private final StringProperty currentNode = new SimpleStringProperty(null);
    private boolean wasDragged = false;

    public PannableCanvas() {
        // add scale transform
        scaleXProperty().bind(myScale);
        scaleYProperty().bind(myScale);

        // logging
//        addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
//            System.out.println(
//                    "canvas event: " + (((event.getSceneX() - getBoundsInParent().getMinX()) / getScale()) + ", scale: " + getScale())
//            );
//            System.out.println("canvas bounds: " + getBoundsInParent());
//        });
    }


    /**
     * Add a grid to the canvas, send it to back
     */
    public void addGrid(double w, double h) {

//        double w = getBoundsInLocal().getWidth();
//        double h = getBoundsInLocal().getHeight();

        // add grid
        Canvas grid = new Canvas(w, h);

        // don't catch mouse events
        grid.setMouseTransparent(true);

        GraphicsContext gc = grid.getGraphicsContext2D();

        gc.setStroke(Color.GRAY);
        gc.setLineWidth(1);


        System.out.println("w: " + w + " h: " + h);

        // draw grid lines
        double offset = 50;
        for (double i = offset; i < w; i += offset) {
            // vertical
            gc.strokeLine(i, 0, i, h);
            // horizontal
            gc.strokeLine(0, i, w, i);
        }

        getChildren().add(grid);
        grid.toBack();
    }


    public double getScale() {
        return myScale.get();
    }

    public void setScale(double scale) {
        myScale.set(scale);
    }

    public void setPivot(double x, double y) {
        setTranslateX(getTranslateX() - x);
        setTranslateY(getTranslateY() - y);
    }

    public String getCurrentNode() {
        return currentNode.get();
    }

    public StringProperty currentNodeProperty() {
        return currentNode;
    }

    public void setCurrentNode(String currentNode) {
        this.currentNode.set(currentNode);
    }

    public boolean getWasDragged() {
        return wasDragged;
    }

    public void setWasDragged(boolean ifDragged) {
        this.wasDragged = ifDragged;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("adsa");
    }
}


/**
 * Mouse drag context used for scene and nodes.
 */
class DragContext {

    double mouseAnchorX;
    double mouseAnchorY;

    double translateAnchorX;
    double translateAnchorY;
}

