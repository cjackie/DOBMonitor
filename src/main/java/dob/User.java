package dob;

/**
 * Created by chaojiewang on 11/12/17.
 */
public class User {
    private String username;
    private String email;
    private int active;

    ////////////////////////////////////////////
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }
}
