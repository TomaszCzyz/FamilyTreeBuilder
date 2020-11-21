package app.basics;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;


public class PannableCanvas extends Pane {

    DoubleProperty myScale = new SimpleDoubleProperty(1.0);

    private final StringProperty currentBoxId = new SimpleStringProperty(null);

    private FamilyMemberBox currentBox;


    public PannableCanvas() {
        // add scale transform
        scaleXProperty().bind(myScale);
        scaleYProperty().bind(myScale);
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

    public String getCurrentBoxId() {
        return currentBoxId.get();
    }

    public StringProperty currentBoxIdProperty() {
        return currentBoxId;
    }

    public void setCurrentBoxId(String currentBoxId) {
        this.currentBoxId.set(currentBoxId);
    }

    public FamilyMemberBox getCurrentBox() {
        return currentBox;
    }

    public void setCurrentBox(FamilyMemberBox currentBox) {
        this.currentBox = currentBox;
    }

    public void resetCurrentNode() {
        currentBox = null;
        currentBoxId.setValue("");
    }
}
