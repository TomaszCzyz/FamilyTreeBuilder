package app.mainView.mainViewSegments;

import app.mainView.MainViewController;
import basics.PannableCanvas;

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
