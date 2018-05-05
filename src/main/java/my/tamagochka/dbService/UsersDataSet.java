package my.tamagochka.dbService;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class UsersDataSet {

    @Id
    @GeneratedValue
    @Column
    private long id;
    @Column
    private String login;
    @Column
    private String password;

    public UsersDataSet() {}

    public UsersDataSet(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

}
