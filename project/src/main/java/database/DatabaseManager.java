package database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_DIRECTORY = "bddfiles"; // Directory of the database
    private static final String DB_FILE = DB_DIRECTORY + "/users.db"; // Full path to users.db
    private static final String DB_URL = "jdbc:sqlite:" + DB_FILE; // URL for SQLite database
    private static Connection conn; // Database connection instance
    private static final Logger LOGGER = LogManager.getLogger(DatabaseManager.class); // Logger instance

    private static DatabaseManager instance; // Singleton instance of DatabaseManager

    /**
     * Constructor for DatabaseManager to initialize the database and tables.
     */
    public DatabaseManager() {
        // Ensure that the directory for the database exists and create it if not
        File directory = new File(DB_DIRECTORY);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("Directory '" + DB_DIRECTORY + "' created successfully.");
            } else {
                throw new RuntimeException("Unable to create directory '" + DB_DIRECTORY + "'.");
            }
        }
        // Create the users table if it does not exist
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nickname TEXT NOT NULL,
                    ip_address TEXT NOT NULL,
                    last_seen INTEGER NOT NULL
                );
            """;
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing database", e);
        }
        // Create the message table if it does not exist
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String createTableSQLM = """
                CREATE TABLE IF NOT EXISTS Message (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    data TEXT NOT NULL,
                    date INTEGER NOT NULL,
                    ip_address_source TEXT NOT NULL,
                    ip_address_dest TEXT NOT NULL
                );
            """;
            stmt.execute(createTableSQLM);
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing database", e);
        }
    }

    /**
     * Returns the singleton instance of DatabaseManager.
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Returns a connection to the SQLite database.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    /**
     * Connects to the SQLite database and logs the connection status.
     */
    public static Connection connect() {
        try {
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Connection to SQLite database successful.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    /**
     * Closes the current database connection.
     */
    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Connection to the database closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    /**
     * Adds or updates a user in the database based on the given IP address.
     */
    public void addOrUpdateUser(String nickname, String ipAddress, long lastSeen) {
        String checkSQL = "SELECT COUNT(*) FROM users WHERE ip_address = ?";
        String insertSQL = "INSERT INTO users (nickname, ip_address, last_seen) VALUES (?, ?, ?)";
        String updateSQL = "UPDATE users SET nickname = ?, last_seen = ? WHERE ip_address = ?";

        try (Connection conn = getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSQL)) {

            // Check if the IP address already exists
            checkStmt.setString(1, ipAddress);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // Update user if IP address exists
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
                        updateStmt.setString(1, nickname);
                        updateStmt.setLong(2, lastSeen);
                        updateStmt.setString(3, ipAddress);
                        updateStmt.executeUpdate();
                    }
                } else {
                    // Insert new user if IP address does not exist
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                        insertStmt.setString(1, nickname);
                        insertStmt.setString(2, ipAddress);
                        insertStmt.setLong(3, lastSeen);
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding or updating user", e);
        }
    }

    /**
     * Displays all users stored in the users table.
     */
    public static void displayAllUsers() {
        String sql = "SELECT * FROM users";

        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("List of registered users:");
            while (rs.next()) {
                System.out.println(
                        "ID: " + rs.getInt("id") +
                                ", Nickname: " + rs.getString("nickname") +
                                ", IP Address: " + rs.getString("ip_address") +
                                ", Last Seen: " + formatTimestamp(rs.getLong("last_seen"))
                );
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Adds a message to the Message table.
     */
    public static void addMessage(Message message)
    {
        String data = message.getData();
        long date = message.getDate().getTime();
        String ipAddressSource = message.getIpSource();
        String ipAddressDest = message.getIpDest();

        String insertMessageSQL = """
        INSERT INTO Message (data, date, ip_address_source, ip_address_dest)
        VALUES (?, ?, ?, ?);
    """;

        try (Connection conn = getConnection()) {
            // Insert the message into the Message table
            try (PreparedStatement pstmt = conn.prepareStatement(insertMessageSQL)) {
                pstmt.setString(1, data);
                pstmt.setLong(2, date);
                pstmt.setString(3, ipAddressSource);
                pstmt.setString(4, ipAddressDest);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding message to database", e);
        }
    }

    /**
     * Displays all messages stored in the Message table.
     */
    public static void displayAllMessages() {
        String sql = "SELECT * FROM Message";
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("List of registered messages:");
            while (rs.next()) {
                System.out.println(
                        "ID: " + rs.getInt("id") +
                                ", Data: " + rs.getString("data") +
                                ", Date: " + new java.util.Date(rs.getLong("date")) +
                                ", IP Source: " + rs.getString("ip_address_source") +
                                ", IP Dest: " + rs.getString("ip_address_dest")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error displaying messages: " + e.getMessage());
        }
    }

    /**
     * Retrieves messages sent from a specific source IP to a specific destination IP.
     */
    public static List<Message> getMessagesFromIpSourceToIpDest(String ipSource, String ipAdresse) {
        String sql = "SELECT * FROM Message WHERE ip_address_source = ? AND ip_address_dest = ?";
        List<Message> messages = new ArrayList<>();

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ipSource);
            pstmt.setString(2, ipAdresse);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Message message = new Message(
                        rs.getString("data"),
                        new Date(rs.getLong("date")),
                        rs.getString("ip_address_source"),
                        rs.getString("ip_address_dest")
                );
                messages.add(message);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving messages: " + e.getMessage());
        }

        return messages;
    }

    /**
     * Retrieves messages exchanged between two IPs in either direction.
     */
    public static List<Message> getMessagesBetween(String ip1, String ip2) {
        String sql = "SELECT * FROM Message WHERE (ip_address_source = ? AND ip_address_dest = ?) OR (ip_address_source = ? AND ip_address_dest = ?)";
        List<Message> messages = new ArrayList<>();

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ip1);
            pstmt.setString(2, ip2);
            pstmt.setString(3, ip2);
            pstmt.setString(4, ip1);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Message message = new Message(
                        rs.getString("data"),
                        new Date(rs.getLong("date")),
                        rs.getString("ip_address_source"),
                        rs.getString("ip_address_dest")
                );
                messages.add(message);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving messages: " + e.getMessage());
        }

        return messages;
    }

    /**
     * Converts a timestamp to a formatted string.
     */
    private static String formatTimestamp(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date date = new java.util.Date(timestamp);
        return dateFormat.format(date);
    }

    /**
    * Clear all data from the database
    **/
    public void clearDatabase() {
        try {
            String[] tables = {"users", "message"};
            for (String table : tables) {
                String query = "DELETE FROM " + table;
                try (PreparedStatement statement = conn.prepareStatement(query)) {
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            LOGGER.debug("Error when cleaning database: " + e.getMessage());
        }
    }

    /**
    * Remove a user from the database
    **/
    public void removeUser(String nickname) {
        String query = "DELETE FROM Users WHERE nickname = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, nickname);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                LOGGER.info("User deleted: " + nickname);
            } else {
                LOGGER.info("No user found with this nickname: " + nickname);
            }
        } catch (SQLException e) {
            LOGGER.info("Error when deleting user: " + e.getMessage());
        }
    }
}