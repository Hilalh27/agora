package contacts;

import java.net.InetAddress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class User {

    private static final Logger LOGGER = LogManager.getLogger(User.class);

    private String nickname;
    private InetAddress ip_address;
    private long lastSeen; // Dernier moment où l'utilisateur a été vu

    public User(String nickname, InetAddress ip_address) {
        this.nickname = nickname;
        this.ip_address = ip_address;
        this.lastSeen = System.currentTimeMillis();
    }

    public String getNickname() {
        return nickname;
    }

    public InetAddress getIp_address() {
        return ip_address;
    }

    public String getStringAddress() {
        return ip_address.getHostAddress();
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public void updateLastSeen() {
        this.lastSeen = System.currentTimeMillis(); // Mettre à jour le dernier moment vu
    }

    @Override
    public String toString() {
        return "Utilisateurs{" +
                "pseudo='" + nickname + '\'' +
                ", adresse_IP=" + this.getStringAddress() +
                '}';
    }
}
