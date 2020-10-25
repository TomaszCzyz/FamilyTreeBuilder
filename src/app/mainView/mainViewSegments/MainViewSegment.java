package app.mainView.mainViewSegments;

import app.mainView.MainViewController;

public class MainViewSegment {
    public MainViewController mainViewController;

    public void injectMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }
}
