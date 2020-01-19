package controller.Threads;

import controller.VisualisationController;
import javafx.animation.AnimationTimer;
import javafx.scene.control.TextArea;
import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.network.Arp;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

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
            //TODO ustaw wszystko na default
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

            timer = new AnimationTimer() {
                @Override
                public void handle(long l) {
//                    textAreaOutput.setText(tcpOut);
                    textAreaOutput.caretPositionProperty();
                }
            };

            timer.start();

            //Create packet handler which will receive packets
            pcap.loop(amountPacket, new PcapPacketHandler() {
                Udp udp;
                Tcp tcp;
                Http http;
                Arp arp;
                Icmp icmp;

                @Override
                public void nextPacket(PcapPacket packet, Object t) {
                    tcp = new Tcp();
                    udp = new Udp();
                    http = new Http();
                    arp = new Arp();
                    icmp = new Icmp();
                    if (vController.disableButton.isDisabled()) {
                        if ((packet.hasHeader(tcp))) {
                            textAreaInfo.setText(String.valueOf(vController.progress.incrementAndGet()));
                            System.out.println("Packet TCP:" + tcp.getPacket());
                        } else if (packet.hasHeader(udp)) {
                            textAreaInfo.setText(String.valueOf(vController.progress.incrementAndGet()));
                            textAreaOutput.appendText("Packet UDP:" + udp.getPacket());
//                            System.out.println(("Packet UDP:" + udp.getPacket()));
                        }
                    }
                    else {
                        packetFilter(packet, tcp, udp, http, arp, icmp);
                    }
                    if (amountPacket > 10 || amountPacket == -1) {
                        try {
                            sleep(80);
                        } catch (Exception ex) {
                            System.out.println("Sleep: " + ex.getMessage());
                        }
                    }
                    if (vController.running.get() == false) {
                        try {
                            join();
                            pcap.breakloop();
                        } catch (InterruptedException e) {
                            textAreaInfo.appendText("Catch: " + e.getMessage());
                        }

                    }

                }

            }, errbuf);
            pcap.breakloop();
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

    public void packetFilter(PcapPacket packet, Tcp tcp, Udp udp, Http http, Arp arp, Icmp icmp) {
        //--------------------  HTTP    ---------------------------------------
        if (vController.filtersGroup.getSelectedToggle() == vController.httpButton) {
            if (packet.hasHeader(http)) {
                textAreaInfo.setText(String.valueOf(vController.progress.incrementAndGet()));
                textAreaOutput.appendText("Http protocol" + http.getPacket());
            }
        }
        //--------------------  DNS    ---------------------------------------
        else if (vController.filtersGroup.getSelectedToggle() == vController.dnsButton) {
            if (packet.hasHeader(udp)) {
                if ((udp.source() == 53) || (udp.destination() == 53)) {
                    textAreaInfo.setText(String.valueOf(vController.progress.incrementAndGet()));
                    textAreaOutput.appendText("DNS protocol" + udp.getPacket());
                }
            }
        }
        //--------------------  ICMP    ---------------------------------------
        else if (vController.filtersGroup.getSelectedToggle() == vController.icmpButton) {
            if (packet.hasHeader(icmp)) {
                textAreaInfo.setText(String.valueOf(vController.progress.incrementAndGet()));
                textAreaOutput.appendText("ICMP protocol" + icmp.getPacket());
            }
        }
        //--------------------  ARP    ---------------------------------------
        else if (vController.filtersGroup.getSelectedToggle() == vController.arpButton) {
            if (packet.hasHeader(arp)) {
                textAreaInfo.setText(String.valueOf(vController.progress.incrementAndGet()));
                textAreaOutput.appendText("ARP protocol" + arp.getPacket());
            }
        }
        //--------------------  HTTP SSL   ---------------------------------------
        else if (vController.filtersGroup.getSelectedToggle() == vController.httpSslButton) {
            if (packet.hasHeader(tcp)) {
                textAreaInfo.setText(String.valueOf(vController.progress.incrementAndGet()));
                if ((tcp.source() == 443) || (tcp.destination() == 443)) {
                    textAreaOutput.appendText("TCP- HTTP SSL protocol" + tcp.getPacket());
                }
            }
        }
        //--------------------  FTP    ---------------------------------------
        else if (vController.filtersGroup.getSelectedToggle() == vController.ftpButton) {
            if (packet.hasHeader(tcp)) {
                if ((tcp.source() == 21) || (tcp.destination() == 21)) {
                    textAreaOutput.appendText("TCP- FTP protocol" + tcp.getPacket());
                }
            }
        }
        //--------------------  POP3    ---------------------------------------
        else if (vController.filtersGroup.getSelectedToggle() == vController.popButton) {
            if (packet.hasHeader(tcp)) {
                if ((tcp.source() == 110) || (tcp.destination() == 110)) {
                    textAreaInfo.setText(String.valueOf(vController.progress.incrementAndGet()));
                    textAreaOutput.appendText("TCP- POP3 protocol" + tcp.getPacket());
                }
            }
        }
        //--------------------  SMTP    ---------------------------------------
        else if (vController.filtersGroup.getSelectedToggle() == vController.smtpButton) {
            if (packet.hasHeader(tcp)) {
                if ((tcp.source() == 25) || (tcp.destination() == 25)) {
                    textAreaInfo.setText(String.valueOf(vController.progress.incrementAndGet()));
                    textAreaOutput.appendText("TCP- SMTP protocol" + tcp.getPacket());
                }
            }
        }

    }
}


//TODO zmienic ------ setText --- na --- appendText ---
//
//                        textAreaOutput.setText("\n--------------------------------------------------------------------------------\n" +
//                                " " + localTime.getHour() + ":" + localTime.getMinute() + ":" + localTime.getSecond() +
//                                " >>>> Packet TCP " + tcp.getPacket().getFrameNumber() +
//                                "\n--------------------------------------------------------------------------------" +
//                                //FRAME:
//                                "\nNumer: " + tcp.getPacket().getFrameNumber() + "\n" +
//                                "TimeMilis: " + (tcp.getPacket().getCaptureHeader().timestampInMillis() / (1000 * 60*60)+1) % 24 +":"+
//                                (tcp.getPacket().getCaptureHeader().timestampInMillis() / (1000 * 60)) % 60 +":" +
//                                (tcp.getPacket().getCaptureHeader().timestampInMillis() / 1000 ) % 60 +":" +
//                                (tcp.getPacket().getCaptureHeader().timestampInMillis()% 1000) + "\n" +
//                                "Caplen: " + tcp.getPacket().getCaptureHeader().caplen() + "\n" +
//                                "Wiren: " + tcp.getPacket().getCaptureHeader().wirelen() + "\n" +
//                                "Packet: " + tcp.getPacket()
//
//                        );
//