package app.stages.mainview.mainViewSegments;

import app.stages.mainview.MainViewController;
import app.basics.PannableCanvas;
import app.stages.mainview.mainViewSegments.canvas.CanvasController;

public class MainViewSegment {

    public MainViewController mainViewController;
    public CanvasController canvasController;

    public void injectMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public void injectCanvasController(CanvasController canvasController) {
        this.canvasController = canvasController;
    }

}
