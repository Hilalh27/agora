package network.TCP;

import controller.TextingController;
import database.DatabaseManager;
import database.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Date;

public class TCPSender {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean Connected;
    private static final Logger LOGGER = LogManager.getLogger(TCPSender.class);

    /**
     * Checks if the connection is established.
     */
    public boolean isConnected() {
        return Connected;
    }

    /**
     * Gets the current client socket.
     */
    public Socket getClientSocket() {
        return clientSocket;
    }

    /**
     * Starts a connection to a server at the specified IP and port.
     */
    public void startConnection(String ip, int port) {
        try {
            this.clientSocket = new Socket(ip, port);
            LOGGER.info("Connected to {}:{}", ip, port);
            System.out.println("Connected to ");

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            LOGGER.info("Output Stream initialized");

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            LOGGER.info("Input Stream initialized");
        } catch (UnknownHostException e) {
            LOGGER.error("Unknown host: {}:{}", ip, port);
        } catch (IOException e) {
            LOGGER.debug("Error when initializing connection :{}", e.getMessage());
        }
    }

    /**
     * Sends a message to the server and returns the server's response.
     */
    public String sendMessage(String msg) {
        try {
            Date date = new Date(Instant.now().toEpochMilli());
            Message message = new Message(msg, date, TextingController.getLocalIPAddress(), this.clientSocket.getInetAddress().getHostAddress());
            System.out.println("IP Source = " + TextingController.getLocalIPAddress());
            System.out.println("IP dest = " + clientSocket.getInetAddress().getHostAddress());
            DatabaseManager.addMessage(message);
            msg = "cs_msg=" + msg;
            System.out.println("Message envoyé : " + msg);
            out.println(msg);
            return in.readLine();
        }catch (IOException ioException) {
            LOGGER.error("Error when sending message", ioException);
            return null;
        }
    }

    /**
     * Stops the connection and releases resources.
     */
    public void stopConnection() {
        try {
            if (out != null) {
                out.close();
                out = null; // Réinitialiser pour éviter de fermer à nouveau
            }
            if (in != null) {
                in.close();
                in = null; // Réinitialiser pour éviter de fermer à nouveau
            }
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
                clientSocket = null; // Réinitialiser pour éviter de fermer à nouveau
            }
            LOGGER.info("Connection closed");
        } catch (IOException e) {
            LOGGER.error("Error closing the connection: {}", e.getMessage());
        }
    }
}