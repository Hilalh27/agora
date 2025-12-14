package controller;

import contacts.User;
import network.TCP.TCPSender;
import network.TCP.TCPServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MainController {

    private static final Logger LOGGER = LogManager.getLogger(MainController.class);

    public static TCPSender client;
    public static TCPServer server;
    public static DatagramSocket socket;

    /**
     * Returns the current DatagramSocket
     */
    public DatagramSocket getSocket() {
        return socket;
    }

    /**
     * Initializes the session by setting up the UDP socket, TCP server, and TCP client
     */
    public static void initSession(int port_udp, int port_tcp)
    {
        // Setup socket for UDP
        try {
            socket = new DatagramSocket(port_udp);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        // Setup socket for TCP server
        server = new TCPServer();
        Thread serverThread = new Thread(() -> server.start(port_tcp));
        serverThread.start();
        while (!server.isReady()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for server to be ready.");
            }
        }

        // Setup socket for TCP client
        client = new TCPSender();
    }

    /**
     * Returns the main user, including their nickname and local IP address
     */
    public static User getMainUser() throws UnknownHostException {
        InetAddress ip_address = InetAddress.getByName(TextingController.getLocalIPAddress());
        String nickname = ContactController.getNickname();

        return new User(nickname, ip_address);
    }

    /**
     * Closes the session by stopping the TCP client, TCP server, and closing the UDP socket
     */
    public static void closeSession() {
        client.stopConnection();
        server.stop();
        if (socket != null && !socket.isClosed()) {
            LOGGER.info("Closing socket on port: " + socket.getLocalPort());
            socket.close();
        }
    }
}