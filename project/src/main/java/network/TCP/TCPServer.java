package network.TCP;

import controller.TextingController;
import database.DatabaseManager;
import database.Message;
import network.TCPObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.*;

public class TCPServer {
    private ServerSocket serverSocket;
    private static final Logger LOGGER = LogManager.getLogger(TCPServer.class);
    private boolean isReady = false;
    private boolean isClosed = false;

    /**
     * Checks if the server is closed.
     */
    public boolean isClosed() {
        return isClosed;
    }

    private static List<TCPObserver> TCPObservers = new ArrayList<>();

    /**
     * Adds an observer to the list of observers.
     */
    public void addObserver(TCPObserver TCPObserver) {
        TCPObservers.add(TCPObserver);
    }

    /**
     * Removes an observer from the list of observers.
     */
    public void removeObserver(TCPObserver TCPObserver) {
        TCPObservers.remove(TCPObserver);
    }

    /**
     * Notifies all observers of a new message.
     */
    private static void notifyObservers(Message message) {
        for (TCPObserver TCPObserver : TCPObservers) {
            TCPObserver.update(message);
        }
    }

    /**
     * Starts the TCP server and listens for incoming client connections.
     */
    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            isReady = true;
            isClosed = false;
            while (!isClosed) {
                try {
                    new ClientHandler(serverSocket.accept()).start();
                } catch (IOException e) {
                    if (!serverSocket.isClosed()) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    /**
     * Checks if the server is ready to accept connections.
     */
    public boolean isReady() {
        return isReady;
    }

    /**
     * Stops the server and releases resources.
     */
    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                isClosed = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles client communication in a separate thread.
     */
    private static class ClientHandler extends Thread{
        private final Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;

        /**
         * Creates a new ClientHandler with the given socket.
         */
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        /**
         * Runs the communication loop, reading and responding to client messages.
         */
        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.startsWith("cs_msg="))
                    {
                        if(!Objects.equals(clientSocket.getInetAddress().getHostAddress(), TextingController.getLocalIPAddress()))
                        {
                            inputLine = inputLine.substring("cs_msg=".length());
                            LOGGER.debug(inputLine);
                            Date date = new Date(Instant.now().toEpochMilli());
                            Message message = new Message(inputLine, date, clientSocket.getInetAddress().getHostAddress(), TextingController.getLocalIPAddress());
                            DatabaseManager.addMessage(message);
                            synchronized (this) {
                                notifyObservers(message);
                            }
                        }
                    }
                    out.println(inputLine);
                }
                in.close();
                clientSocket.close();
            } catch (IOException e) {
                LOGGER.debug(e.getMessage());
            }
        }
    }
}