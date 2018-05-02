package my.tamagochka.dbService;

import my.tamagochka.dbService.DAO.UsersDAO;
import my.tamagochka.dbService.dataSets.UsersDataSet;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

import java.sql.Connection;
import java.sql.SQLException;

public class DBService {

    private static final String hibernate_show_sql = "true";
    private static final String hibernate_hbm2ddl_auto = "create";

    private final SessionFactory sessionFactory;

    public DBService() {
        //Configuration config = getH2Configuration();
        Configuration config = getMySQLConfiguration();
        sessionFactory = createSessionFactory(config);
    }

    public UsersDataSet getUser(long id) throws DBException {
        try {
            Session session = sessionFactory.openSession();
            UsersDAO dao = new UsersDAO(session);
            UsersDataSet dataSet = dao.get(id);
            session.close();
            return dataSet;
        } catch(HibernateException e) {
            throw new DBException(e);
        }
    }

    public long addUser(String name) throws DBException {
        try {
            Session session = sessionFactory.openSession();
            Transaction tr = session.beginTransaction();
            UsersDAO dao = new UsersDAO(session);
            long id = dao.insertUser(name);
            tr.commit();
            session.close();
            return id;
        } catch(HibernateException e) {
            throw new DBException(e);
        }
    }

    public void printConnectInfo() {
        Session session = sessionFactory.openSession();
        session.doWork(connection -> {
            System.out.printf("DB name: " + connection.getMetaData().getDatabaseProductName());
            System.out.printf("DB version: " + connection.getMetaData().getDatabaseProductVersion());
            System.out.println("Driver: " + connection.getMetaData().getDriverName());
            System.out.println("Autocommit: " + connection.getAutoCommit());
        });
    }

    public static Configuration getMySQLConfiguration() {
        Configuration config = new Configuration();
        config.addAnnotatedClass(UsersDataSet.class);
        config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        config.setProperty("hibernate.connection.drive_class", "com.mysql.jdbc.Driver");
        config.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/db_example");
        config.setProperty("hibernate.connection.username", "root");
        config.setProperty("hibernate.connection.password", "123");
        config.setProperty("hibernate.show_sql", hibernate_show_sql);
        config.setProperty("hibernate.hbm2ddl.auto", hibernate_hbm2ddl_auto);
        return config;
    }

    public static Configuration getH2Configuration() {

        Configuration config = new Configuration();
        config.addAnnotatedClass(UsersDataSet.class);
        config.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        config.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        config.setProperty("hibernate.connection.url", "jdbc:h2:./h2db");
        config.setProperty("hibernate.connection.username", "root");
        config.setProperty("hibernate.connection.password", "123");
        config.setProperty("hibernate.show_sql", hibernate_show_sql);
        config.setProperty("hibernate.hbm2ddl.auto", hibernate_hbm2ddl_auto);
        return config;
    }

    private static SessionFactory createSessionFactory(Configuration config) {
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(config.getProperties());
        StandardServiceRegistry serviceRegistry = builder.build();
        return config.buildSessionFactory(serviceRegistry);
    }

    public void closeConnection() {
        sessionFactory.close();
    }

}
