package botapi.types;

/**
 * **************************************************************
 * <p>
 * Desc:
 * User: jianguangluo
 * Date: 2019-01-02
 * Time: 13:54
 * <p>
 * **************************************************************
 */
public class MessageEntity {
    private String type;
    private User user;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
