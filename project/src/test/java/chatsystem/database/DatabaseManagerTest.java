package chatsystem.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import database.DatabaseManager;
import database.Message;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseManagerTest {
    private static final Logger LOGGER = LogManager.getLogger(DatabaseManagerTest.class);
    private DatabaseManager dbManager;

    /**
     * Sets up the test environment by initializing the database manager and connecting to the database.
     */
    @BeforeEach
    public void setUp() {
        dbManager = DatabaseManager.getInstance();
        dbManager.connect();
    }

    /**
     * Cleans up the test environment by clearing the database and closing the connection.
     */
    @AfterEach
    public void tearDown() {
        dbManager.clearDatabase();
        dbManager.closeConnection();
    }

    /**
     * Tests the singleton instance of the DatabaseManager class.
     */
    @Test
    public void SingletonInstance() {
        DatabaseManager instance1 = DatabaseManager.getInstance();
        DatabaseManager instance2 = DatabaseManager.getInstance();
        assertSame(instance1, instance2, "Should return the same instance");
    }

    /**
     * Tests adding or updating a user by inserting a new user.
     */
    @Test
    public void AddOrUpdateUser_Insert() {
        dbManager.addOrUpdateUser("John", "192.168.1.1", System.currentTimeMillis());
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE ip_address = '192.168.1.1'")) {
            ResultSet rs = stmt.executeQuery();
            rs.next();
            assertEquals(1, rs.getInt(1), "User should be added");
        } catch (SQLException e) {
            fail("SQL Exception occurred: " + e.getMessage());
        }
    }

    /**
     * Tests adding or updating a user by updating an existing user's information.
     */
    @Test
    public void AddOrUpdateUser_Update() {
        long currentTime = System.currentTimeMillis();
        dbManager.addOrUpdateUser("John", "192.168.1.1", currentTime);
        dbManager.addOrUpdateUser("John Updated", "192.168.1.1", currentTime + 1000);

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT nickname, last_seen FROM users WHERE ip_address = '192.168.1.1'")) {
            ResultSet rs = stmt.executeQuery();
            rs.next();
            assertEquals("John Updated", rs.getString("nickname"), "Nickname should be updated");
            assertEquals(currentTime + 1000, rs.getLong("last_seen"), "Last seen should be updated");
        } catch (SQLException e) {
            fail("SQL Exception occurred: " + e.getMessage());
        }
    }

    /**
     * Tests displaying all users from the database.
     */
    @Test
    public void DisplayAllUsers() {
        dbManager.addOrUpdateUser("John", "192.168.1.1", System.currentTimeMillis());
        dbManager.addOrUpdateUser("Doe", "192.168.1.2", System.currentTimeMillis());
        dbManager.displayAllUsers();
    }

    /**
     * Tests adding a message to the database.
     */
    @Test
    public void AddMessage() {
        Message message = new Message("Hello", new Date(), "192.168.1.1", "192.168.1.2");
        dbManager.addMessage(message);
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM Message WHERE ip_address_source = '192.168.1.1'")) {
            ResultSet rs = stmt.executeQuery();
            rs.next();
            assertEquals(1, rs.getInt(1), "Message should be added");
        } catch (SQLException e) {
            fail("SQL Exception occurred: " + e.getMessage());
        }
    }

    /**
     * Tests retrieving messages from a specific source IP to a destination IP.
     */
    @Test
    public void GetMessagesFromIpSourceToIpDest() {
        Message message1 = new Message("Hello", new Date(), "192.168.1.1", "192.168.1.2");
        Message message2 = new Message("Hi", new Date(), "192.168.1.1", "192.168.1.2");
        dbManager.addMessage(message1);
        dbManager.addMessage(message2);

        List<Message> messages = dbManager.getMessagesFromIpSourceToIpDest("192.168.1.1", "192.168.1.2");
        assertEquals(2, messages.size(), "Should retrieve two messages");
    }

    /**
     * Tests removing a user from the database.
     */
    @Test
    public void RemoveUser() {
        dbManager.addOrUpdateUser("John", "192.168.1.1", System.currentTimeMillis());
        dbManager.removeUser("John");
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM users")) {
            ResultSet rs = stmt.executeQuery();
            rs.next();
            assertEquals(0, rs.getInt(1), "All users should be cleared");
        } catch (SQLException e) {
            fail("SQL Exception occurred: " + e.getMessage());
        }
    }
}