package my.tamagochka.dbService;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DBService {

    private final EntityManagerFactory entityManagerFactory;

    public DBService () {
        entityManagerFactory = Persistence.createEntityManagerFactory("my.tamagochka.WebService");
    }



}
