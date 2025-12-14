package chatsystem.network;

import network.TCP.TCPSender;
import network.TCP.TCPServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class TCPTest {
    private static final Logger LOGGER = LogManager.getLogger(TCPTest.class);
    private static TCPServer server;
    private static TCPSender client;
    private static TCPSender client2;
    private static TCPSender client3;

    /**
     * Sets up the server and initializes client connections before all tests.
     */
    @BeforeAll
    public static void setup() {
        server = new TCPServer();
        Thread serverThread = new Thread(() -> server.start(5004));
        serverThread.start();
        while (!server.isReady()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for server to be ready.");
            }
        }
        LOGGER.info("Server is ready for testing.");
    }

    /**
     * Cleans up by stopping all client connections and the server after all tests.
     */
    @AfterAll
    public static void tearDown() {
        if (client != null) client.stopConnection();
        if (client2 != null) client2.stopConnection();
        if (client3 != null) client3.stopConnection();
        if (server != null) server.stop();
    }

    /**
     * Tests the connection and message exchange between a client and the server.
     */
    @Test
    public void ClientConnectionAndMessageExchange() {
        client = new TCPSender();
        client.startConnection("localhost", 5004);
        LOGGER.info("Client connected to the TCP server.");
        String response = client.sendMessage("Hello, Server!");
        assertEquals("Hello, Server!", response, "Expected server response does not match.");
        client.stopConnection();
    }

    /**
     * Tests the connection and message exchange between multiple clients and the server.
     */
    @Test
    public void MultipleClientsConnection() {
        client2 = new TCPSender();
        client3 = new TCPSender();
        client2.startConnection("localhost", 5004);
        client3.startConnection("localhost", 5004);

        String response1 = client2.sendMessage("Client 2 here!");
        String response2 = client3.sendMessage("Client 3 here!");

        assertEquals("Client 2 here!", response1);
        assertEquals("Client 3 here!", response2);

        client2.stopConnection();
        client3.stopConnection();
    }
}