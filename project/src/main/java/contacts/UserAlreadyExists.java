package contacts;

public class UserAlreadyExists extends Exception {

    private final String nickname;

    public UserAlreadyExists(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "UserAlreadyExists{" +
                "nickname='" + nickname + '\'' +
                '}';
    }
}
