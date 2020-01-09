package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MenuController {

    @FXML
    private MainController mainController;

    // Menu -> VISUALISATION
    @FXML
    public void openVisualisation() {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/resources/VisualisationWindow.fxml"));
        Pane pane = null;

        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        VisualisationController visualisationController = loader.getController();
//        visualisationController.setMainController(mainController);
        mainController.setWindow(pane);
    }


    // Menu -> SETTINGS
    @FXML
    public void openSettings() {

    }

    // Menu -> EXIT
    @FXML
    public void exit() {

        Platform.exit();
    }

    public void setMainController(MainController mainController) {

        this.mainController = mainController;

    }

    @FXML
    public void backToMenu() throws IOException {

        String command = "netsh wlan show interfaces";
        Process p = Runtime.getRuntime().exec(command);

        InetAddress localhost = InetAddress.getLocalHost();
        System.out.println(" IP Addr: " + localhost.getHostAddress());

        BufferedReader inn = new BufferedReader(new InputStreamReader(p.getInputStream()));

        Pattern pattern = Pattern.compile(".*BSSID.*: (.*)");


        while (true) {
            String line = inn.readLine();

            if (line == null)
                break;

            Matcher mm = pattern.matcher(line);
            if (mm.matches()) {
                System.out.println(mm.group(1));
            }
        }


    }
}

