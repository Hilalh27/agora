package network.UDP;

import contacts.ActiveUserList;
import contacts.User;
import contacts.UserAlreadyExists;
import controller.ContactController;
import database.DatabaseManager;
import network.UDPObserver;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class UDPServer extends Thread{

    DatagramSocket socket;

    private static List<UDPObserver> udpObservers = new ArrayList<>();

    public void addObserver(UDPObserver observer) {
        udpObservers.add(observer);
    }
    public void removeObserver(UDPObserver observer) {
        udpObservers.remove(observer);
    }

    private static void notifyObservers(HashSet<User> activeUsers) {
        for (UDPObserver UDPObserver : udpObservers) {
            UDPObserver.update(activeUsers);
        }
    }

    public UDPServer (DatagramSocket socket){
        this.socket = socket;
    }

    /**
     * A thread to periodically send the nickname to indicate online presence.
     **/
    public class SendNickNameThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    ContactController.sendNickname();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * A thread to periodically update the list of active users :
     * Every 4 seconds, we read the nicknames received with addNewUsers
     * If a user has not sent anything for 6 seconds, it is removed
     * If there has been a change, the users are displayed
     **/
    public class UpdateUserThread extends Thread {
        @Override
        public void run() {
            while (true){
                try {
                    ContactController.addNewUsers(socket,4000);
                } catch (UserAlreadyExists e) {
                    throw new RuntimeException(e);
                }
                ActiveUserList.removeInactiveUsers(6000);
                if (ActiveUserList.getMaj_users()){
                    synchronized (this) {
                        System.out.println("appel obseerver update");
                        notifyObservers(ActiveUserList.activeUsers);
                    }
                    DatabaseManager.displayAllUsers();
                    ActiveUserList.setMaj_users(false);
                }
            }
        }
    }
}