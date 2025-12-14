package controller;

import database.DatabaseManager;
import database.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;

public class TextingController {

    private static final Logger LOGGER = LogManager.getLogger(TextingController.class);

    /**
     * Starts a connection with the given IP address and port 4500
     */
    public static void startWithPerson(String ip_dest)
    {
        MainController.client.startConnection(ip_dest, 4500);
        LOGGER.info("Connected with " + ip_dest);
    }

    /**
     * Ends the current connection
     */
    public static void endWithCurrentPerson()
    {
        MainController.client.stopConnection();
        LOGGER.info("Disconnected with " + MainController.client.getClientSocket().getInetAddress().toString());
    }

    /**
     * Sends a message through the client
     */
    public static void sendMessage(String msg)
    {
        MainController.client.sendMessage(msg);
    }

    /**
     * Retrieves the list of messages exchanged between the current device and the given IP address
     */
    public static List<Message> getMessagesWith(String ip) throws UnknownHostException {
        return DatabaseManager.getMessagesBetween(TextingController.getLocalIPAddress(), ip);
    }

    /**
     * Retrieves the local IP address of the device
     */
    public static String getLocalIPAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (networkInterface.isUp() && !networkInterface.isLoopback()) {
                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = inetAddresses.nextElement();
                        if (!inetAddress.isLoopbackAddress() && inetAddress.getHostAddress().contains(".")) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null; // If no valid IP address is found
    }
}