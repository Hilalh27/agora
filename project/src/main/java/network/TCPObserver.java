package network;

import database.Message;

public interface TCPObserver {
    void update(Message message);
}