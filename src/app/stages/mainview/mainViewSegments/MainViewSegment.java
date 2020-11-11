package app.stages.mainview.mainViewSegments;

import app.stages.mainview.MainViewController;
import app.basics.PannableCanvas;

public class MainViewSegment {

    public MainViewController mainViewController;
    public PannableCanvas pannableCanvas;


    public void injectMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public void injectPannableCanvas(PannableCanvas pannableCanvas) {
        this.pannableCanvas = pannableCanvas;
    }

}
