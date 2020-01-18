package controller.Threads;

import controller.VisualisationController;
import javafx.animation.AnimationTimer;
import javafx.scene.control.TextArea;
import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.tcpip.Tcp;

import java.time.LocalTime;

public class CaptureThread extends Thread {
    LocalTime localTime = LocalTime.now();
    StringBuilder errbuf;
    VisualisationController vController;
    private AnimationTimer timer;
    private final TextArea textAreaInfo;
    private TextArea textAreaOutput;
    int amountPacket;
    String nameDevice;
    String tcpOut = "";


    public CaptureThread(TextArea textAreaOutput,
                         TextArea textAreaInfo,
                         int amountPacket,
                         StringBuilder errbuf,
                         String nameDevice,
                         VisualisationController vController) {
        this.textAreaOutput = textAreaOutput;
        this.textAreaInfo = textAreaInfo;
        this.amountPacket = amountPacket;
        this.errbuf = errbuf;
        this.nameDevice = nameDevice;
        this.vController = vController;

    }

    @Override
    public void run() {
        textAreaInfo.appendText("\n\n>>> Start capturing...\n");
        Pcap pcap;
        int snaplen = 64 * 1024;           // Capture all packets, no trucation
        int flags = Pcap.MODE_NON_PROMISCUOUS; // capture all packets
        int timeout = 10 * 1000;           // 10 seconds in millis
        //Open the selected device to capture packets
        try {
            pcap = Pcap.openLive(nameDevice,
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
            textAreaInfo.appendText("\ndevice opened\n");

            timer  = new AnimationTimer() {
                @Override
                public void handle(long l) {
//                     textAreaOutput.setText(tcpOut);
                    textAreaOutput.caretPositionProperty();
                }
            };
            timer.start();
            //Create packet handler which will receive packets
            pcap.loop(amountPacket, new PcapPacketHandler() {

                Tcp tcp;

                @Override
                public void nextPacket(PcapPacket packet, Object t) {
                    tcp = new Tcp();
                    if (packet.hasHeader(tcp)) {
                        textAreaInfo.setText(String.valueOf(vController.progress.incrementAndGet()));

                        //TODO zmienic ------ setText --- na --- appendText ---
                        textAreaOutput.setText("\n--------------------------------------------------------------------------------\n" +
                                " " + localTime.getHour() + ":" + localTime.getMinute() + ":" + localTime.getSecond() +
                                " >>>> Packet TCP " + tcp.getPacket().getFrameNumber() +
                                "\n--------------------------------------------------------------------------------" +
                                //FRAME:
                                "\nNumer: " + tcp.getPacket().getFrameNumber() + "\n" +
                                "TimeMilis: " + (tcp.getPacket().getCaptureHeader().timestampInMillis() / (1000 * 60*60)+1) % 24 +":"+
                                (tcp.getPacket().getCaptureHeader().timestampInMillis() / (1000 * 60)) % 60 +":" +
                                (tcp.getPacket().getCaptureHeader().timestampInMillis() / 1000 ) % 60 +":" +
                                (tcp.getPacket().getCaptureHeader().timestampInMillis()% 1000) + "\n" +
                                "Caplen: " + tcp.getPacket().getCaptureHeader().caplen() + "\n" +
                                "Wiren: " + tcp.getPacket().getCaptureHeader().wirelen() + "\n" +
                                "Pcaket: " + tcp.getPacket()

                        );
                    }
                    if(amountPacket != 10)
                    try {
                        sleep(80);
                    } catch (Exception ex) {
                        System.out.println("Sleep: " + ex.getMessage());
                    }
                    if(vController.running.get() == false) {
                        try {
                            join();
                        } catch (InterruptedException e) {
                            textAreaInfo.appendText("Catch: " +  e.getMessage());
                        }

                    }

                }

            }, errbuf);
            pcap.close();

        } catch (Exception ex) {
            System.out.println("Wyjątek w klasie : " + this.getClass() + "\n i miejscu " +
                    ". \nInformacja dla admina: \n" + ex.getMessage());

            vController.action_StopCapturePacket();
        }
        vController.action_StopCapturePacket();
    }

    public void stopAnimationTimer() {
        timer.stop();
    }
}
