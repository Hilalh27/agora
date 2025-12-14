package chatsystem.contacts;

import contacts.ActiveUserList;
import contacts.User;
import contacts.UserAlreadyExists;
import database.DatabaseManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class ActiveUserListTest {
    private static final Logger LOGGER = LogManager.getLogger(ActiveUserListTest.class);

    private User user1;
    private User user2;
    private DatabaseManager dbManager;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    void setUp() throws UnknownHostException {
        user1 = new User("hilal", InetAddress.getByName("192.168.1.2"));
        user2 = new User("ml", InetAddress.getByName("192.168.1.3"));
        dbManager = DatabaseManager.getInstance();
        dbManager.connect();
    }

    /**
     * Cleans up the test environment after each test.
     */
    @AfterEach
    public void tearDown() {
        ActiveUserList.getInstance().clear();
        dbManager.closeConnection();
    }

    /**
     * Tests adding a user to the active user list.
     */
    @Test
    void addUser() throws UserAlreadyExists {
        assertDoesNotThrow(() -> ActiveUserList.getInstance().addUser(user2));
        LOGGER.debug("ActiveList after adding ml: " + ActiveUserList.getInstance().getAllContacts());
        assertTrue(ActiveUserList.nicknameUsed("ml"));
        assertFalse(ActiveUserList.nicknameUsed("hilal"));

        assertDoesNotThrow(() -> ActiveUserList.getInstance().addUser(user1));
        LOGGER.debug("ActiveList after adding hilal: " + ActiveUserList.getInstance().getAllContacts());
        assertTrue(ActiveUserList.nicknameUsed("hilal"));
        assertTrue(ActiveUserList.nicknameUsed("ml"));
    }

    /**
     * Tests adding a user with an already existing nickname.
     */
    @Test
    void addUserWithExistingNickname() {
        assertDoesNotThrow(() -> ActiveUserList.getInstance().addUser(user1));
        User userDuplicate = new User("hilal", user2.getIp_address());
        assertThrows(UserAlreadyExists.class, () -> ActiveUserList.getInstance().addUser(userDuplicate));
    }

    /**
     * Tests checking if a nickname is already in use.
     */
    @Test
    void nicknameUsed() throws UserAlreadyExists {
        assertDoesNotThrow(() -> ActiveUserList.getInstance().addUser(user1));
        assertTrue(ActiveUserList.nicknameUsed("hilal"));
        assertFalse(ActiveUserList.nicknameUsed("ml"));
    }

    /**
     * Tests checking if a user exists in the active user list.
     */
    @Test
    void userExists() throws UserAlreadyExists {
        ActiveUserList.getInstance().addUser(user1);
        assertTrue(ActiveUserList.userExists(user1));
        assertFalse(ActiveUserList.userExists(user2));
    }

    /**
     * Tests setting and getting the major user flag.
     */
    @Test
    void setAndGetMajUsers() {
        ActiveUserList.setMaj_users(false);
        assertFalse(ActiveUserList.getMaj_users());

        ActiveUserList.setMaj_users(true);
        assertTrue(ActiveUserList.getMaj_users());
    }

    /**
     * Tests setting the list of active users.
     */
    @Test
    void setActiveUsers() {
        HashSet<User> users = new HashSet<>();
        users.add(user1);
        users.add(user2);

        ActiveUserList.setActiveUsers(users);
        assertEquals(2, ActiveUserList.getAllContacts().size());
        assertTrue(ActiveUserList.getAllContacts().contains(user1));
        assertTrue(ActiveUserList.getAllContacts().contains(user2));
    }

    /**
     * Tests retrieving a nickname by IP address.
     */
    @Test
    void getNicknameByIp() throws UserAlreadyExists {
        ActiveUserList.getInstance().addUser(user1);
        assertEquals("hilal", ActiveUserList.getNicknameByIp("192.168.1.2"));
        assertNull(ActiveUserList.getNicknameByIp("192.168.1.100")); // Unknown IP
    }

    /**
     * Tests removing inactive users from the active user list.
     */
    @Test
    void removeInactiveUsers() throws UserAlreadyExists {
        user1.setLastSeen(System.currentTimeMillis() - 10000);
        ActiveUserList.getInstance().addUser(user1);
        ActiveUserList.getInstance().removeInactiveUsers(6000);
        assertFalse(ActiveUserList.getAllContacts().contains(user1));
    }

    /**
     * Tests clearing the contact list.
     */
    @Test
    void clearContactList() throws UserAlreadyExists {
        ActiveUserList.getInstance().addUser(user1);
        ActiveUserList.getInstance().addUser(user2);
        ActiveUserList.getInstance().clear();
        assertTrue(ActiveUserList.getAllContacts().isEmpty());
    }
}