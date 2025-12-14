package chatsystem.network;

import contacts.ActiveUserList;
import contacts.User;
import network.UDP.UDPSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.*;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class UDPTest {

    private DatagramSocket socket;

    /**
     * Sets up the socket for UDP communication before each test.
     */
    @BeforeEach
    void setUp() throws IOException {
        socket = new DatagramSocket(4445);
    }

    /**
     * Closes the socket after each test.
     */
    @AfterEach
    void tearDown() {
        socket.close();
    }

    /**
     * Tests sending and receiving a broadcast UDP message.
     */
    @Test
    void sendBroadcast() throws IOException {
        String message = "Hello, World!";
        InetAddress address = InetAddress.getByName("255.255.255.255");
        Thread senderThread = new Thread(() -> {
            try {
                UDPSender.sendBroadcast(address, 4445, message);
            } catch (IOException e) {
                fail("Failed to send UDP broadcast message");
            }
        });
        senderThread.start();
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        String receivedMessage = new String(packet.getData(), 0, packet.getLength());
        assertEquals(message, receivedMessage);
    }

    /**
     * Tests updating the list of active users.
     */
    @Test
    void updateActiveUsers() {
        HashSet<User> activeUsers = new HashSet<>();
        User user1 = new User("User1", socket.getLocalAddress());
        User user2 = new User("User2", socket.getLocalAddress());
        activeUsers.add(user1);
        activeUsers.add(user2);
        ActiveUserList.setActiveUsers(activeUsers);
        assertTrue(ActiveUserList.userExists(user1));
        assertTrue(ActiveUserList.userExists(user2));
    }
}