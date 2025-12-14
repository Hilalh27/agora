package chatsystem.controller;

import contacts.ActiveUserList;
import contacts.User;
import contacts.UserAlreadyExists;
import controller.ContactController;
import controller.MainController;
import database.DatabaseManager;
import network.UDP.UDPMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.*;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    private static final Logger LOGGER = LogManager.getLogger(ControllerTest.class);

    private User user1;
    private User user2;
    private User user3;
    private DatagramSocket socket;
    private DatabaseManager dbManager;
    private int port = 5000;
    private MainController controller;

    /**
     * Sets up the test environment by initializing necessary objects and connections.
     */
    @BeforeEach
    void setUp() throws UnknownHostException {
        controller = new MainController();
        MainController.initSession(port,port+500);

        user1 = new User("test1", InetAddress.getByName("192.168.1.2"));
        user2 = new User("test2", InetAddress.getByName("192.168.1.3"));
        user3 = new User("test3", InetAddress.getByName("192.168.1.4"));
        dbManager = DatabaseManager.getInstance();
        dbManager.connect();
    }

    /**
     * Cleans up the test environment by clearing the user list and closing database connections.
     */
    @AfterEach
    void tearDown() {
        ActiveUserList.getInstance().clear();
        dbManager.closeConnection();
        MainController.closeSession();
    }

    /**
     * Tests handling of a contact discovery message.
     */
    @Test
    void handleContactDiscoveryMessage() throws UserAlreadyExists {
        UDPMessage msg1 = new UDPMessage("cs_nickname=" + user1.getNickname(), user1.getIp_address());
        UDPMessage msg2 = new UDPMessage("cs_nickname=" + user2.getNickname(), user2.getIp_address());

        assertFalse(ActiveUserList.userExists(user1));
        ContactController.handleContactDiscoveryMessage(msg1);
        assertTrue(ActiveUserList.userExists(user1));

        assertFalse(ActiveUserList.userExists(user2));
        ContactController.handleContactDiscoveryMessage(msg2);
        assertTrue(ActiveUserList.nicknameUsed(user2.getNickname()));
    }

    /**
     * Tests the nickname sending functionality.
     */
    @Test
    void sendNickname(){
        ContactController.setNickname("test_user");
        assertDoesNotThrow(ContactController::sendNickname, "Sending nickname should not throw an exception");
    }

    /**
     * Tests changing a user's nickname.
     */
    @Test
    void changeNickname() throws UserAlreadyExists {
        ActiveUserList.getInstance().addUser(user1);
        assertFalse(ContactController.changeNickname(user1.getNickname()));
        assertTrue(ContactController.changeNickname(user2.getNickname()));
        assertTrue(ContactController.changeNickname(user3.getNickname()));
        assertTrue(ContactController.changeNickname("nickname5"));
    }

    /**
     * Tests retrieving a user by their nickname.
     */
    @Test
    void getUserFromNickname() throws UserAlreadyExists {
        ActiveUserList.addUser(user1);
        User retrievedUser = ContactController.getUserFromNickname("test1");
        assertNotNull(retrievedUser, "User should be retrieved successfully by nickname");
        assertEquals(user1, retrievedUser, "Retrieved user should match the expected user");
    }

    /**
     * Tests retrieving an IP address by the user's nickname.
     */
    @Test
    void getIpFromNickname() throws UserAlreadyExists {
        ActiveUserList.addUser(user1);
        String ip = ContactController.getIpFromNickname("test1");
        assertEquals("192.168.1.2", ip, "IP address should be retrieved successfully");
    }

    /**
     * Tests user registration (inscription).
     */
    @Test
    void inscription() throws IOException, UserAlreadyExists {
        boolean result = ContactController.inscription("unique_user");
        assertTrue(result, "User should be successfully registered");
        assertEquals("unique_user", ContactController.getNickname(), "Nickname should be set correctly after inscription");
    }

    /**
     * Tests displaying the list of active users.
     */
    @Test
    void displayActiveUsers() throws UserAlreadyExists {
        ActiveUserList.addUser(user1);
        ActiveUserList.addUser(user2);
        assertDoesNotThrow(ContactController::displayActiveUsers, "Displaying active users should not throw exceptions");
    }

    /**
     * Tests closing the session and ensuring the socket is closed.
     */
    @Test
    public void closeSession() {
        assertNotNull(controller.getSocket());
        assertFalse(controller.getSocket().isClosed());
        controller.closeSession();
        assertTrue(controller.getSocket().isClosed());
    }
}