package controller.Threads;

import controller.TableViewColumn.Ip;
import controller.TableViewColumn.Transport;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.network.Arp;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

import java.util.List;

public class CaptureWithoutFilters {
    private PcapPacket pcapPacket;
    CaptureThread captureThread;
    private Tcp tcp;
    private Udp udp;
    private Icmp icmp;
    private Arp arp;
    private Http http;
    private String packetCapture;
    private boolean ifIp6;
    private int line;
    private int counterIp;
    private int counterTranspotLayer;
    private List<Ip> listIp;
    private List<Transport> listTransport;

    public CaptureWithoutFilters(PcapPacket pcapPacket, Tcp tcp, Udp udp, Icmp icmp, Arp arp, Http http, String packetCapture,
                                 boolean ifIp6, int line, int counterIp, int counterTranspotLayer, List<Ip> listIp,
                                 List<Transport> listTransport, CaptureThread captureThread) {
        this.pcapPacket = pcapPacket;
        this.tcp = tcp;
        this.udp = udp;
        this.icmp = icmp;
        this.arp = arp;
        this.http = http;
        this.packetCapture = packetCapture;
        this.ifIp6 = ifIp6;
        this.line = line;
        this.counterIp = counterIp;
        this.counterTranspotLayer = counterTranspotLayer;
        this.listIp = listIp;
        this.listTransport = listTransport;
        this.captureThread = captureThread;
    }

    public boolean isIfIp6() {
        return ifIp6;
    }

    public int getCounterIp() {
        return counterIp;
    }

    public int getCounterTranspotLayer() {
        return counterTranspotLayer;
    }

    public CaptureWithoutFilters invoke() {
        String str;
        String[] arrOfStr;//--------------------   IPv6  ---------------------------------------
        str = packetCapture;
        arrOfStr = str.split("Ip6:");
        if (arrOfStr.length == 2 || ifIp6) {
            ifIp6 = true;
            if (line > 16 && line < 28) {
                listIp.add(new Ip(packetCapture.replaceFirst("Ip6: *", "")));
            } else {
                listIp.add(new Ip(""));
            }
        }
        if (!captureThread.checkPacketUdp && ifIp6) {
            //--------------------   TCP  ---------------------------------------
            if (line > 27 && line < 49) {
                transportLayer(pcapPacket, tcp, udp, icmp, arp, http, packetCapture, listTransport, counterTranspotLayer);
                counterTranspotLayer++;
            } else {
                listTransport.add(new Transport(""));
            }
        } else if (captureThread.checkPacketUdp && ifIp6) {
            //--------------------   UDP  ---------------------------------------
            if (line > 27 && line < 35) {
                transportLayer(pcapPacket, tcp, udp, icmp, arp, http, packetCapture, listTransport, counterTranspotLayer);
                counterTranspotLayer++;
            } else {
                listTransport.add(new Transport(""));
            }
        }
        //--------------------   IPv4  ---------------------------------------
        if (!ifIp6) {
            if (line > 16 && line < 38) {
                listIp.add(new Ip(packetCapture.replaceFirst("Ip: *", "")));
                counterIp = 10;
            } else {
                listIp.add(new Ip(""));
            }
        }
        if (!captureThread.checkPacketUdp && !ifIp6) {
            //--------------------   TCP  ---------------------------------------
            if (line > 37 && line < 59) {
                transportLayer(pcapPacket, tcp, udp, icmp, arp, http, packetCapture, listTransport, counterTranspotLayer);
                counterTranspotLayer++;
            } else {
                listTransport.add(new Transport(""));
            }
        } else if (captureThread.checkPacketUdp && !ifIp6) {
            //--------------------   UDP  ---------------------------------------
            if (line > 37 && line < 45) {
                transportLayer(pcapPacket, tcp, udp, icmp, arp, http, packetCapture, listTransport, counterTranspotLayer);
                counterTranspotLayer++;
            } else {
                listTransport.add(new Transport(""));
            }
        }

        return this;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void transportLayer(PcapPacket pcapPacket, Tcp tcp, Udp udp, Icmp icmp, Arp arp,
                               Http http, String packetCapture, List<Transport> listTransport,
                               int counterTransportLayer
    ) {
        if (pcapPacket.hasHeader(tcp)) {
            listTransport.add(new Transport(packetCapture.replaceFirst("Tcp: *", "")));
            counterTransportLayer++;
        } else if (pcapPacket.hasHeader(udp)) {
            listTransport.add(new Transport(packetCapture.replaceFirst("Udp: *", "")));
            counterTransportLayer++;
        }
        //--------------------  ICMP  ---------------------------------------
        else if (pcapPacket.hasHeader(icmp)) {
            listTransport.add(new Transport(packetCapture.replaceFirst("Icmp: *", "")));
        }
        //--------------------  HTTP  ---------------------------------------
        else if (pcapPacket.hasHeader(http)) {
            listTransport.add(new Transport(packetCapture.replaceFirst("Http: *", "")));
        }
        //--------------------  ARP  ---------------------------------------
//        if (line > 17 && line < 29) {
//            listFiltr.add(new Filtr(packetCapture.replaceFirst("Arp: *", "")));
//        } else {
//            listFiltr.add(new Filtr(""));
//        }

    }
}

