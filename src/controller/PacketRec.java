package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;


public class PacketRec implements PacketReceiver {

    VisualisationController visualisationController;

    @FXML
    private TextArea textAreaOutput;

    public PacketRec(TextArea textAreaOutput, VisualisationController visualisationController) {
        this.textAreaOutput = textAreaOutput;
        this.visualisationController = visualisationController;
    }

    @Override
    public void receivePacket(Packet packet) {

    }
}
