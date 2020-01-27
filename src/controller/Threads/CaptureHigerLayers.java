package controller.Threads;

import controller.TableViewColumn.Filters;
import controller.TableViewColumn.Ip;
import controller.TableViewColumn.Transport;
import controller.VisualisationController;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

import java.util.List;

public class CaptureHigerLayers {
    private PcapPacket pcapPacket;
    private CaptureThread captureThread;
    private VisualisationController vController;
    private Tcp tcp;
    private Udp udp;
    private Icmp icmp;
    private String packetCapture;
    private boolean ifIp6;
    private int line;
    private int counterIp;
    private List<Ip> listIp;
    private List<Transport> listTransport;
    private List<Filters> listFilters;

    public CaptureHigerLayers(PcapPacket pcapPacket, Tcp tcp, Udp udp, Icmp icmp, String packetCapture,
                              boolean ifIp6, int line, int counterIp, List<Ip> listIp,
                              List<Transport> listTransport, List<Filters> listFilters, CaptureThread captureThread,
                              VisualisationController vController
    ) {
        this.pcapPacket = pcapPacket;
        this.tcp = tcp;
        this.udp = udp;
        this.icmp = icmp;
        this.packetCapture = packetCapture;
        this.line = line;
        this.counterIp = counterIp;
        this.listIp = listIp;
        this.listTransport = listTransport;
        this.captureThread = captureThread;
        this.listFilters = listFilters;
        this.vController = vController;
        this.ifIp6 = ifIp6;
    }

    public int getCounterIp() {
        return counterIp;
    }

    public boolean isIfIp6() {
        return ifIp6;
    }

    public void setIfIp6(boolean ifIp6) {
        this.ifIp6 = ifIp6;
    }


    public CaptureHigerLayers invoke() {
        String str;
        String[] arrOfStr;
        str = packetCapture;
        arrOfStr = str.split("Ip6:");

        if (arrOfStr.length == 2 || ifIp6) {
            //--------------------   IPv6  ---------------------------------------
            ipVersion6();
            transportLayerForIP6(ifIp6, 27, 49, 35, 38);
            applicationLayer(ifIp6, 49, 62);
        } else if (!ifIp6) {
            //--------------------   IPv4  ---------------------------------------
            ipVersion4();
            transportLayerForIP6(!ifIp6, 37, 59, 45, 48);
            applicationLayer(ifIp6, 58, 73);
        }
        return this;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //-----------------------------------   Wartstwa Aplikacji  ---------------------------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void applicationLayer(boolean ifIp6, int i, int i2) {

        if (!captureThread.checkPacketUdp && ifIp6 ) {
            //--------------------   http  ---------------------------------------
            if (line > i && line < i2) {
                listFilters.add(new Filters(packetCapture.replaceFirst("Http: *", "")));
            } else {
                listFilters.add(new Filters(""));
            }
        } else if (captureThread.checkPacketUdp && ifIp6 ) {
            if (line > i && line < i2) {
                listFilters.add(new Filters(packetCapture.replaceFirst("Http: *", "")));
            } else {
                listFilters.add(new Filters(""));
            }
        } else {
             /*
              *     włączenie obu dla protokolu UDP i TCP
              *     wyłączenie Filtru w tCP i UDP dla HTTP
             */
            listFilters.add(new Filters(""));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //------------------------------------------  IP version 4  ---------------------------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void transportLayerForIP6(boolean ifIp6, int i, int i2, int i3, int i4) {
        if (vController.filtersGroup.getSelectedToggle() == vController.icmpButton) {
            //--------------------   ICMP  ---------------------------------------
            //TODO wyswietla protokol ICMP razem z TCP i UDP
            if (line > i && line < i4) {
                transportLayer(pcapPacket, tcp, udp, icmp, packetCapture);
            } else {
                listTransport.add(new Transport(""));
                listFilters.add(new Filters(""));
            }
        } else {
            if (!captureThread.checkPacketUdp && ifIp6) {
                //--------------------   TCP  ---------------------------------------
                if (line > i && line < i2) {
                    transportLayer(pcapPacket, tcp, udp, icmp, packetCapture);
                } else {
                    listTransport.add(new Transport(""));
                        listFilters.add(new Filters(""));
                }
            } else if (captureThread.checkPacketUdp && ifIp6) {
                //--------------------   UDP  ---------------------------------------
                if (line > i && line < i3) {
                    transportLayer(pcapPacket, tcp, udp, icmp, packetCapture);
                } else {
                    listTransport.add(new Transport(""));
                        listFilters.add(new Filters(""));
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //------------------------------------------  IP version 4  ---------------------------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void ipVersion4() {
        if (line > 16 && line < 38) {
            listIp.add(new Ip(packetCapture.replaceFirst("Ip: *", "")));
            counterIp = 10;
        } else {
            listIp.add(new Ip(""));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //------------------------------------------  IP version 6  ---------------------------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void ipVersion6() {
        ifIp6 = true;
        if (line > 16 && line < 28) {
            listIp.add(new Ip(packetCapture.replaceFirst("Ip6: *", "")));
        } else {
            listIp.add(new Ip(""));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //------------------------------------  Warstwa Transportowa  ---------------------------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void transportLayer(PcapPacket pcapPacket, Tcp tcp, Udp udp, Icmp icmp,
                               String packetCapture
    ) {
        //--------------------  TCP  ---------------------------------------
        if (pcapPacket.hasHeader(tcp)) {
            listTransport.add(new Transport(packetCapture.replaceFirst("Tcp: *", "")));
        }
        //--------------------  UDP  ---------------------------------------
        else if (pcapPacket.hasHeader(udp)) {
            listTransport.add(new Transport(packetCapture.replaceFirst("Udp: *", "")));
        }
        //--------------------  ICMP  ---------------------------------------
        else if (pcapPacket.hasHeader(icmp)) {
            listTransport.add(new Transport(packetCapture.replaceFirst("Icmp: *", "")));
        }
    }
}

