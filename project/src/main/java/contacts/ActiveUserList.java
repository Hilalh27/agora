package contacts;

import database.DatabaseManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.*;

public class ActiveUserList {

    private static final Logger LOGGER = LogManager.getLogger(ActiveUserList.class);
    private static ActiveUserList INSTANCE = new ActiveUserList();

    /**
    * Singleton instance of ActiveUserList
    */
    public static ActiveUserList getInstance() {
        return INSTANCE;
    }

    /**
    * HashSet to store all active users (ensures uniqueness)
    */
    public static HashSet<User> activeUsers = new HashSet<>();
    private static boolean maj_users; // Tracks changes in the user list (new connection or disconnection)

    private ActiveUserList() {}

    public static synchronized boolean getMaj_users() {
        return maj_users;
    }
    public static synchronized void setMaj_users(boolean maj) {
        maj_users = maj;
    }

    /**
    * Adds a new user to the active user list.
    **/
    public static synchronized void addUser(User sender) throws UserAlreadyExists {
        if (nicknameUsed(sender.getNickname())) {
            throw new UserAlreadyExists(sender.getNickname());
        }
        activeUsers.add(sender);
        LOGGER.debug("User added: " + sender.getNickname());
        LOGGER.debug("Active users: " + activeUsers);
        DatabaseManager.getInstance().addOrUpdateUser(
                sender.getNickname(),
                sender.getStringAddress(),
                sender.getLastSeen()
        );
        setMaj_users(true);
    }

    /**
    * Checks if a nickname is already in use by any active user.
    **/
    public static synchronized boolean nicknameUsed(String name) {
        LOGGER.debug("Checking nickname: " + name);
        for (User user : activeUsers) {
            if (user.getNickname().equals(name)) {
                LOGGER.debug("Nickname found: " + name);
                return true;
            }
        }
        return false;
    }

    /**
    * Checks if a user exists in the active user list by comparing their IP and nickname.
    **/
    public static boolean userExists(User sender){
        Iterator<User> iterator = activeUsers.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            LOGGER.debug("Checking user: " + user.getNickname() + " (" + user.getIp_address() + ")");
            if (user.getIp_address().equals(sender.getIp_address())) {
                if (user.getNickname().equals(sender.getNickname())) {
                    user.updateLastSeen();
                    return true;
                }
                if (user.getLastSeen() < sender.getLastSeen()) {
                    iterator.remove();
                }
            }
        }
        return false;
    }

    /**
    * Removes users from the active list if their lastSeen timestamp exceeds the specified timeout.
    **/
    public static synchronized void removeInactiveUsers(int timeout) {
        long now = System.currentTimeMillis();
        synchronized (ActiveUserList.getInstance()) {
            Iterator<User> iterator = activeUsers.iterator();
            while (iterator.hasNext()) {
                User user = iterator.next();
                if (now - user.getLastSeen() > timeout) {
                    iterator.remove();
                    setMaj_users(true); // Protégé via synchronized
                }
            }
        }
    }

    /**
    * Returns a copy of all active users.
    **/
    public static synchronized HashSet<User> getAllContacts() {
        return new HashSet<>(activeUsers);
    }

    /**
    * Clears all active users and resets the database.
    **/
    public synchronized void clear() {
        activeUsers.clear();
        DatabaseManager.getInstance().clearDatabase();
    }

    /**
    * Replaces the activeUsers list with a new set of users.
    **/
    public static void setActiveUsers(HashSet<User> users) {
        activeUsers = users;
    }

    /**
    * Finds the nickname associated with a given IP address.
    **/
    public static synchronized String getNicknameByIp(String ipAddress) {
        for (User user : activeUsers) {
            if (user.getStringAddress().equals(ipAddress)) {
                return user.getNickname();
            }
        }
        return null;
    }
}