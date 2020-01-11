package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.jnetpcap.PcapIf;

import java.util.List;

public class CaptureThread extends Thread {

    StringBuilder errbuf;
    List<PcapIf> interfaceDevice;
    VisualisationController visualisationController;

    @FXML
    private TextArea textAreaOutput;

    @FXML
    private TextArea textAreaInfo;

    int number;

    public CaptureThread(TextArea textAreaOutput,
                         TextArea textAreaInfo,
                         List<PcapIf> interfaceDevice,
                         StringBuilder errbuf,
                         int number,
                         VisualisationController visualisationController) {
        this.textAreaOutput = textAreaOutput;
        this.errbuf = errbuf;
        this.interfaceDevice = interfaceDevice;
        this.number = number;
        this.textAreaInfo = textAreaInfo;

        this.visualisationController = visualisationController;
    }

    public void run() {
        CaptureTask captureTask = new CaptureTask(textAreaOutput, textAreaInfo,
                interfaceDevice, errbuf, number, visualisationController, this);

        Platform.runLater(() -> captureTask.run());

//        ProgressBar bar = new ProgressBar();
//        bar.progressProperty().bind(captureTask.progressProperty());
//        new Thread(captureTask).start();

        while (visualisationController.running.get()) {
            try {
                this.sleep(1);
            } catch (Exception ex) {
                System.out.println("Wyjątek w klasie : " + this.getClass()+ "\n i metodzie "+ this.getName() +
                        ". \nInformacja dla admina: \n" + ex.getMessage());
            }
        }
    }
}
