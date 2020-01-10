package controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.tcpip.Tcp;

import java.time.LocalTime;
import java.util.List;

class CaptureTask extends Task  {

    StringBuilder errbuf;
    List<PcapIf> interfaceDevice;
    VisualisationController visualisationController;
    CaptureThread captureThread;

    @FXML
    private TextArea textAreaOutput;

    @FXML
    private TextArea textAreaShow;

    int number;

    public CaptureTask(TextArea textAreaOutput,
                       TextArea textAreaShow,
                       List<PcapIf> interfaceDevice,
                       StringBuilder errbuf,
                       int number,
                       VisualisationController visualisationController,
                       CaptureThread captureThread
    ) {
        this.textAreaOutput = textAreaOutput;
        this.textAreaShow = textAreaShow;
        this.errbuf = errbuf;
        this.interfaceDevice = interfaceDevice;
        this.number = number;

        this.visualisationController = visualisationController;
        this.captureThread = captureThread;
    }
    @Override public Void call() {

        LocalTime localTime = LocalTime.now();
        textAreaOutput.appendText("\n\nStarting of capture packet...\n");
        try {
            int snaplen = 64 * 1024;           // Capture all packets, no trucation
            int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
            int timeout = 10 * 1000;           // 10 seconds in millis

            //Open the selected device to capture packets
            Pcap pcap = Pcap.openLive(interfaceDevice.get(number).getName(),
                    snaplen,
                    flags,
                    timeout,
                    errbuf
            );

            if (pcap == null) {
                System.err.printf("Error while opening device for capture: "
                        + errbuf.toString());
            }

            System.out.println("device opened");
            textAreaOutput.appendText("device opened");

            //Create packet handler which will receive packets
            PcapPacketHandler jpacketHandler = new PcapPacketHandler() {

                Tcp tcp = new Tcp();

                @Override
                public void nextPacket(PcapPacket packet, Object t) {
                    if (packet.hasHeader(tcp)) {
                        textAreaShow.appendText("\n--------------------------------------------------------------------------------\n" +
                                " " + localTime.getHour() + ":" + localTime.getMinute()+ ":" +localTime.getSecond() +
                                " >>>> Packet TCP " + tcp.getPacket().getFrameNumber() +
                                "\n--------------------------------------------------------------------------------" +
                                tcp.getPacket()
                        );

                        System.out.println("Packet TCP:" + tcp.getPacket());

                    }
                }

            };
            //we enter the loop and capture the 10 packets here.You can  capture any number of packets just by changing the first
            // argument to pcap.loop() function below

            while (visualisationController.running.get()) {
                    pcap.loop(100, jpacketHandler, "jnetpcap rocks!");

                    pcap.close();

                    System.out.println("\nDevice closed");
                    textAreaOutput.appendText("\nDevice closed");
            }
        } catch (Exception ex) {
            System.out.println("Wyjątek w klasie : " + this.getClass()+ "\n i miejscu "+ this.getTitle() +
                    ". \nInformacja dla admina: \n" + ex.getMessage());
        }

        return null;
    }

}

