package my.tamagochka.dbService.DAO;

import my.tamagochka.dbService.dataSets.UsersDataSet;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class UsersDAO {

    private final Session session;

    public UsersDAO(Session session) { this.session = session; }

    public UsersDataSet get(long id) {
        return (UsersDataSet) session.get(UsersDataSet.class, id);
    }

    public long getUserId(String name) throws HibernateException {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<UsersDataSet> criteria = builder.createQuery(UsersDataSet.class);
        Root<UsersDataSet> root = criteria.from(UsersDataSet.class);
        criteria.select(root);
        criteria.where(builder.equal(root.get("name"), name));
        return session.createQuery(criteria).getSingleResult().getId();
    }

    public long insertUser(String name) {
        return (Long) session.save(new UsersDataSet(name));
    }

}
