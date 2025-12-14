package network.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UDPSender {

    /**
    * Sends a UDP broadcast message to the specified address and port.
    **/
    public static void sendBroadcast(InetAddress adresse, int port, String message) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        byte[] buff = message.getBytes(StandardCharsets.UTF_8);
        DatagramPacket outPacket = new DatagramPacket(buff, buff.length, adresse, port);
        socket.setBroadcast(true);
        socket.send(outPacket);
        socket.close();
    }
}