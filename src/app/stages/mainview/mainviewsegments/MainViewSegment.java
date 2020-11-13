package app.stages.mainview.mainviewsegments;

import app.stages.mainview.MainViewController;
import app.stages.mainview.mainviewsegments.canvas.CanvasController;

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
