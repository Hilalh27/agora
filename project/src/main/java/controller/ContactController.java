package controller;

import contacts.ActiveUserList;
import contacts.User;
import contacts.UserAlreadyExists;
import network.UDP.UDPMessage;
import network.UDP.UDPSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ui.authentification.LoginPanel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class ContactController {

    /**
    * User selects a nickname at login and has a list of other users
    **/
    private static String nickname;
    private static final Logger LOGGER = LogManager.getLogger(ContactController.class);
    private static DatagramSocket socket;

    /**
     * Constructor for initializing the socket
     */
    public ContactController() {
        ContactController.socket = MainController.socket;
    }

    /**
     * Returns the current nickname
     */
    public static synchronized String getNickname() {
        return nickname;
    }

    /**
     * Sets a new nickname
     */
    public static synchronized void setNickname(String nickname) {
        ContactController.nickname = nickname;
    }

    /**
     * Handles incoming contact discovery messages and adds users to the active list
     */
    public static void handleContactDiscoveryMessage(UDPMessage message) throws UserAlreadyExists {
        if (message.content().startsWith("cs_nickname=")) {
            // Extract the nickname
            String nickname = message.content().substring("cs_nickname=".length());
            User sender = new User(nickname, message.origin());
            synchronized (ActiveUserList.getInstance()) {
                if (!ActiveUserList.userExists(sender)) {
                    ActiveUserList.addUser(sender);
                }
            }
        }
        else{
            LOGGER.error("Format of the nickname is wrong, it should start with cs_nickname=");
        }
    }

    /**
     * Sends the nickname as a broadcast message in the correct format
     */
    public static synchronized void sendNickname() throws IOException {
        String cs_nick = "cs_nickname=" + nickname;
        InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255"); // universal broadcast address
        int port = 4445;
        UDPSender.sendBroadcast(broadcastAddress, port, cs_nick);
    }

    /**
     * Attempts to change the nickname if it's not already in use
     */
    public static boolean changeNickname(String newNickname) throws UserAlreadyExists {
        if (!ActiveUserList.nicknameUsed(newNickname)) {
            nickname = newNickname;
            return true;
        }
        return false;
    }

    /**
     * Scans for users sending "cs_nickname=<nickname>" and adds them to the active user list
     */
    public static void addNewUsers(DatagramSocket socket, int timeout) throws UserAlreadyExists {
        long begin = System.currentTimeMillis();

        while (System.currentTimeMillis() - begin < timeout) {
            byte[] buf = new byte[256];

            DatagramPacket inPacket = new DatagramPacket(buf, buf.length);
            try {
                MainController.socket.setSoTimeout(timeout); // Sets a socket timeout
                MainController.socket.receive(inPacket);
            } catch (SocketTimeoutException e) {
                break; // Exit the loop if the timeout is reached
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String received = new String(inPacket.getData(), 0, inPacket.getLength());
            UDPMessage rec_message = new UDPMessage(received, inPacket.getAddress());
            ContactController.handleContactDiscoveryMessage(rec_message);
        }
    }

    /**
     * Retrieves a user from the active list based on their nickname
     */
    public static User getUserFromNickname(String nickname) {
        for (User user : ActiveUserList.activeUsers) {
            if (user.getNickname().equals(nickname)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Retrieves the IP address of a user based on their nickname
     */
    public static String getIpFromNickname(String nickname) {
        for (User user : ActiveUserList.activeUsers) {
            if (user.getNickname().equals(nickname)) {
                return user.getIp_address().getHostAddress();
            }
        }
        return null;
    }

    /**
     * Allows the user to register with a unique nickname, checking existing users first
     */
    public static boolean inscription(String userNickname) throws IOException, UserAlreadyExists {
        do {
            addNewUsers(MainController.socket, 4000); // Scan for connected users
            if (ActiveUserList.nicknameUsed(userNickname)) {
                return false;
            }
        } while (ActiveUserList.nicknameUsed(userNickname));

        setNickname(userNickname);
        sendNickname();
        return true;
    }

    /**
     * Handles user registration within the login panel
     */
    public static void inscriptionPanel(String userNickname, LoginPanel loginPanel) throws IOException, UserAlreadyExists, NicknameAlreadyUsedException {
        if (!inscription(userNickname)) {
            throw new NicknameAlreadyUsedException("Nickname already used. Try again.");
        }
        loginPanel.showConnexionMessage(); // Successful login
    }

    /**
     * Displays all active users
     */
    public static void displayActiveUsers(){
        System.out.println("====================\n");
        for (User user : ActiveUserList.getAllContacts()) {
            System.out.println(user);
        }
        System.out.println("\n");
    }

    /**
     * Exception thrown when a nickname is already in use
     */
    public static class NicknameAlreadyUsedException extends Exception {
        public NicknameAlreadyUsedException(String message) {
            super(message);
        }
    }
}