package my.tamagochka.accounts;

public class UserProfile {

    private String login;
    private String pass;
    private String email;

    public UserProfile(String login, String pass, String email) {
        this.login = login;
        this.pass = pass;
        this.email = email;
    }

    public UserProfile(String login) {
        this.login = login;
        this.pass = login;
        this.email = login;
    }

    public String getLogin() { return login; }
    public String getPass() { return pass; }
    public String getEmail() { return email; }

}
