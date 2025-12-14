package network;

import contacts.User;
import database.Message;

import java.util.HashSet;

public interface UDPObserver {
    void update(HashSet<User> activeUsers);
}