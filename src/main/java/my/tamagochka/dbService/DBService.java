package my.tamagochka.dbService;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class DBService {

    private final EntityManagerFactory entityManagerFactory;

    public DBService () {
        entityManagerFactory = Persistence.createEntityManagerFactory("my.tamagochka.WebService");
    }

    public void closeDB() {
        entityManagerFactory.close();
    }

    public void addUser(String login, String password) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        em.persist(new UsersDataSet(login, password));
        em.getTransaction().commit();
        em.close();
    }

    public List<UsersDataSet> getUsersByLogin(String login) {
        EntityManager em = entityManagerFactory.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UsersDataSet> query = cb.createQuery(UsersDataSet.class);
        Root<UsersDataSet> root = query.from(UsersDataSet.class);
        query.select(root);
        query.where(cb.equal(root.get("login"), login));
        return em.createQuery(query).getResultList();
    }

}
