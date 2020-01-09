package fx;

import controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.UnknownHostException;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/resources/MainWindow.fxml"));
            StackPane stackPane = loader.load();

            MainController mainController = loader.getController();
            loader.setController(mainController);

            Scene scene = new Scene(stackPane, 1100, 1000);

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
    public static void main(String[] args) throws UnknownHostException {

        launch(args);
    }
}