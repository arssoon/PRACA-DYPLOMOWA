package fx;

import controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        try {
            Scene scene;
            scene = getScene();

            stage.setScene(scene);
            stage.initStyle(StageStyle.UNDECORATED);

            //set title for the stage
            stage.setTitle("CAPTURING PACKETS");
            stage.show();
        }
        catch (Exception e) {

            System.out.println(e.getMessage());
        }
    }

    // Main Method
    public static void main(String[] args) {

        launch(args);
    }

    public Scene getScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/resources/MainWindow.fxml"));
        StackPane stackPane = loader.load();

        MainController mainController = loader.getController();
        loader.setController(mainController);

        Scene scene = new Scene(stackPane, 1000, 800);

        return scene;
    }
}