package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class HelpController {

    private MainController mainController;

    @FXML
    private Label helpLabel;

    @FXML
    private Label closeLabelHelp;

    @FXML
    private Button backButton;

    @FXML
    private void initialize() {
        helpLabel.setText("Aplikacja do przechwytywania pakietów Java w sieci LAN.\n\n" +
                "START = Zaczyna przechwytywanie pakietów.\n" +
                "STOP = Zatrzymuję przechwytywanie pakietów.\n" +
                "ZAPISZ = Zapisuje przechwycone pakiety w pliku.\n" +
                "ZAŁADUJ = Wyświetla plik z przechwyconymi pakietami.\n" +
                "ODBLOKUJ = Odblokowanie portów.\n" +
                "ZABLOKUJ = Zablokowanie wszystkich portów.\n" +
                "MENU = Przejście do menu głównego.\n" +
                "WSTECZ = Przejście do poprzedniego okna.\n" +
                "'X' = Wyłącza aplikacje.\n" +
                "WYCZYŚĆ = Czyści okienko do przechwytywania pakietów.\n" +
                "'PORTY' = Przechwytuje konkretne porty.\n"
        );
    }

    @FXML
    public void handleClose(MouseEvent dragEvent) {
        System.exit(0);
    }

    @FXML
    public void action_backToMenu(ActionEvent actionEvent) throws IOException {
        mainController.loadMenuWindow();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
