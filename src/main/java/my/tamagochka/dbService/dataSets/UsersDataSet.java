package my.tamagochka.dbService.dataSets;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users")
public class UsersDataSet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", unique = true, updatable = false)
    private String name;

    public UsersDataSet() {}

    public UsersDataSet(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public UsersDataSet(String name) {
        this.id = -1;
        this.name = name;
    }

    public void setName(String name) { this.name = name; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public long getId() { return id; }

    @Override
    public String toString() {
        return "UsersDataSet{id=" + id + " name='" + name + "'}";
    }
}
