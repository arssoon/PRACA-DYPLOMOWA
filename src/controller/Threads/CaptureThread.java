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

public class CaptureThread extends Thread {
    StringBuilder errbuf;
    VisualisationController vController;
    private AnimationTimer timer;
    private final TextArea textAreaInfo;
    private TextArea textAreaOutput;
    private TextArea textAreaPacket;
    int amountPacket;
    String nameDevice;
    String tcpOut = "";


    public CaptureThread(TextArea textAreaOutput,
                         TextArea textAreaInfo,
                         TextArea textAreaPacket,
                         int amountPacket,
                         StringBuilder errbuf,
                         String nameDevice,
                         VisualisationController vController) {
        this.textAreaOutput = textAreaOutput;
        this.textAreaInfo = textAreaInfo;
        this.textAreaPacket = textAreaPacket;
        this.amountPacket = amountPacket;
        this.errbuf = errbuf;
        this.nameDevice = nameDevice;
        this.vController = vController;

    }

    @Override
    public void run() {
        textAreaInfo.appendText(">>> Otwieranie przechwytywania <<<\n");
        Pcap pcap;

        //Open the selected device to capture packets

        try {
            pcap = Pcap.openLive(nameDevice,
                    Pcap.DEFAULT_SNAPLEN,
                    Pcap.DEFAULT_PROMISC,
                    Pcap.DEFAULT_TIMEOUT,
                    errbuf
            );

            if (pcap == null) {
                textAreaInfo.appendText("Error while opening device for capture: "
                        + errbuf.toString());
            }

            timer = new AnimationTimer() {
                @Override
                public void handle(long l) {

                    textAreaOutput.appendText(tcpOut);
                    textAreaOutput.caretPositionProperty();
                }
            };
            timer.start();

            textAreaInfo.appendText("\n>>> Przechwytywanie w TOKU...\n");
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

                    if ((vController.disableButton.isDisabled()) || (vController.filtersGroup.getSelectedToggle() == null)) {
                        if ((packet.hasHeader(tcp))) {
                            textAreaPacket.setText("\t       TCP: " + String.valueOf(vController.progress.incrementAndGet()));
                            tcpOut = ("\n--------------------------------------------------------------------------------\n"+
                                    "\t\t\t\t\tTCP"+
                                    "\n--------------------------------------------------------------------------------\n"
                                    + tcp.getPacket()
                            );
                        }
                        // ----------------------  UDP  -----------------------------------------------------
                        else if (packet.hasHeader(udp)) {
                            textAreaPacket.setText("\t       UDP: " + String.valueOf(vController.progress.incrementAndGet()));
                            tcpOut = ("\n--------------------------------------------------------------------------------\n"+
                                    "\t\t\t\t\tUDP"+
                                    "\n--------------------------------------------------------------------------------\n"
                                    + udp.getPacket()
                            );
                        }
                    } else {
                        packetFilter(packet, tcp, udp, http, arp, icmp);
                    }

                    try {
                        sleep(1);
                    } catch (Exception ex) {
                        if(!ex.getMessage().equals("sleep interrupted"))
                            textAreaInfo.appendText("Sleep: " + ex.getMessage());
                        textAreaInfo.appendText("\n>>> Zakończono przechwytywanie <<<\n");
                    }

                    if (vController.running.get() == false) {
                        try {
                            join();
                        } catch (InterruptedException e) {
                            if(e.getMessage() != null)
                                textAreaInfo.appendText("Catch: " + e.getMessage());
                        }

                    }

                }

            }, errbuf);
            pcap.close();

        } catch (Exception ex) {
            textAreaInfo.appendText("Wyjątek->CapureThread:Class : " + ex.getMessage());

        }

        textAreaInfo.appendText("\n>>> Zakończono przechwytywanie <<<\n");
        vController.action_StopCapturePacket();
    }


    //--------------------  STOP timer    ---------------------------------------
    public void stopAnimationTimer() {
        timer.stop();
    }

    //--------------------  FILTERS PACKET   ---------------------------------------
    public void packetFilter(PcapPacket packet, Tcp tcp, Udp udp, Http http, Arp arp, Icmp icmp) {
        //--------------------  HTTP    ---------------------------------------
        if (vController.filtersGroup.getSelectedToggle() == vController.httpButton) {
            if (packet.hasHeader(http)) {
                textAreaPacket.setText("\t       HTTP: " + String.valueOf(vController.progress.incrementAndGet()));
                tcpOut = ("\n--------------------------------------------------------------------------------\n"+
                        "\t\t\t\t\tHTTP protocol"+
                        "\n--------------------------------------------------------------------------------\n"
                        + http.getPacket()
                );
            }
        }
        //--------------------  DNS    ---------------------------------------
        else if (vController.filtersGroup.getSelectedToggle() == vController.dnsButton) {
            if (packet.hasHeader(udp)) {
                if ((udp.source() == 53) || (udp.destination() == 53)) {
                    textAreaPacket.setText("\t       DNS: " + String.valueOf(vController.progress.incrementAndGet()));
                    tcpOut = ("\n--------------------------------------------------------------------------------\n"+
                            "\t\t\t\t\tDNS protocol"+
                            "\n--------------------------------------------------------------------------------\n"
                            + udp.getPacket()
                    );
                }
            }
        }
        //--------------------  ICMP    ---------------------------------------
        else if (vController.filtersGroup.getSelectedToggle() == vController.icmpButton) {
            if (packet.hasHeader(icmp)) {
                textAreaPacket.setText("\t       ICMP: " + String.valueOf(vController.progress.incrementAndGet()));
                tcpOut = ("\n--------------------------------------------------------------------------------\n"+
                        "\t\t\t\t\tICMP protocol"+
                        "\n--------------------------------------------------------------------------------\n"
                        + icmp.getPacket()
                );
            }
        }
        //--------------------  ARP    ---------------------------------------
        else if (vController.filtersGroup.getSelectedToggle() == vController.arpButton) {
            if (packet.hasHeader(arp)) {
                textAreaPacket.setText("\t       ARP: " + String.valueOf(vController.progress.incrementAndGet()));
                tcpOut = ("\n--------------------------------------------------------------------------------\n"+
                        "\t\t\t\t\tARP protocol"+
                        "\n--------------------------------------------------------------------------------\n"
                        + arp.getPacket()
                );
            }
        }
        //--------------------  HTTP SSL   ---------------------------------------
        else if (vController.filtersGroup.getSelectedToggle() == vController.httpSslButton) {
            if (packet.hasHeader(tcp)) {
                if ((tcp.source() == 443) || (tcp.destination() == 443)) {
                    textAreaPacket.setText("\t HTTP SSL: " + String.valueOf(vController.progress.incrementAndGet()));
                    tcpOut = ("\n--------------------------------------------------------------------------------\n"+
                            "\t\t\t\t\tHTTP SSL protocol"+
                            "\n--------------------------------------------------------------------------------\n"
                            + tcp.getPacket()
                    );
                }
            }
        }
        //--------------------  FTP    ---------------------------------------
        else if (vController.filtersGroup.getSelectedToggle() == vController.ftpButton) {
            if (packet.hasHeader(tcp)) {
                if ((tcp.source() == 21) || (tcp.destination() == 21)) {
                    textAreaPacket.setText("\t       FTP: " + String.valueOf(vController.progress.incrementAndGet()));
                    tcpOut = ("\n--------------------------------------------------------------------------------\n"+
                            "\t\t\t\t\tFTP protocol"+
                            "\n--------------------------------------------------------------------------------\n"
                            + tcp.getPacket()
                    );
                }
            }
        }
        //--------------------  POP3    ---------------------------------------
        else if (vController.filtersGroup.getSelectedToggle() == vController.popButton) {
            if (packet.hasHeader(tcp)) {
                if ((tcp.source() == 110) || (tcp.destination() == 110)) {
                    textAreaPacket.setText("\t       POP3: " + String.valueOf(vController.progress.incrementAndGet()));
                    tcpOut = ("\n--------------------------------------------------------------------------------\n"+
                            "\t\t\t\t\tPOP3 protocol"+
                            "\n--------------------------------------------------------------------------------\n"
                            + tcp.getPacket()
                    );
                }
            }
        }
        //--------------------  SMTP    ---------------------------------------
        else if (vController.filtersGroup.getSelectedToggle() == vController.smtpButton) {
            if (packet.hasHeader(tcp)) {
                if ((tcp.source() == 25) || (tcp.destination() == 25)) {
                    textAreaPacket.setText("\t       SMTP: " + String.valueOf(vController.progress.incrementAndGet()));
                    tcpOut = ("\n--------------------------------------------------------------------------------\n"+
                            "\t\t\t\t\tSMTP protocol"+
                            "\n--------------------------------------------------------------------------------\n"
                            + tcp.getPacket()
                    );
                }
            }
        }

    }
}