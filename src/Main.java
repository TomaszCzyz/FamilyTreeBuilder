import app.configuration.Configuration;
import app.stages.mainview.MainViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/stages/mainview/MainViewStage.fxml"));
        Parent root = loader.load();

        MainViewController controller = loader.getController();
        primaryStage.setOnCloseRequest(event -> {
            controller.closeProgram();
        });


        primaryStage.setTitle("FamilyTreeBuilder");
        primaryStage.setScene(new Scene(root, 1100, 800));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
