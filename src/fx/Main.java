package fx;

import controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/resources/MainWindow.fxml"));
            StackPane stackPane = loader.load();

            MainController controller = loader.getController();
            loader.setController(controller);

            Scene scene = new Scene(stackPane, 1000, 400);

            stage.setScene(scene);
            //set title for the stage
            stage.setTitle("VISUALISATION ISO/OSI");
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
}