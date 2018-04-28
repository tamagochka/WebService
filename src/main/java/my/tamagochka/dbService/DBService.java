package my.tamagochka.dbService;

import my.tamagochka.dbService.DAO.UsersDAO;
import my.tamagochka.dbService.dataSets.UsersDataSet;
import org.h2.jdbcx.JdbcDataSource;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBService {

    private final Connection connection;

    public DBService() {this.connection = getH2Connection(); }

    public UsersDataSet getUser(long id) throws DBException {
        try {
            return (new UsersDAO(connection).get(id));
        } catch(SQLException e) {
            throw new DBException(e);
        }
    }

    public long addUser(String name) throws DBException {
        try {
            connection.setAutoCommit(false);
            UsersDAO dao = new UsersDAO(connection);
            dao.createTable();
            dao.insertUser(name);
            connection.commit();
            return dao.getUserId(name);
        } catch(SQLException e) {
            try {
                connection.rollback();
            } catch(SQLException ignore) {}
            throw new DBException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch(SQLException ignore) {}
        }
    }

    public void cleanUp() throws DBException {
        UsersDAO dao = new UsersDAO(connection);
        try {
            dao.dropTable();
        } catch(SQLException e) {
            throw new DBException(e);
        }
    }

    public void printConnectInfo() {
        try {
            System.out.printf("DB name: " + connection.getMetaData().getDatabaseProductName());
            System.out.printf("DB version: " + connection.getMetaData().getDatabaseProductVersion());
            System.out.println("Driver: " + connection.getMetaData().getDriverName());
            System.out.println("Autocommit: " + connection.getAutoCommit());
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
    public static Connection getMySQLConnection() {
        try {
            DriverManager.registerDriver((Driver) Class.forName("com.mysql.jdbc.Driver").newInstance());
            StringBuilder url = new StringBuilder();
            url
                    .append("jdbc:mysql://")
                    .append("localhost:")
                    .append("3306/")
                    .append("db_example?")
                    .append("user=test&")
                    .append("password=test");
            System.out.print(url);
            return DriverManager.getConnection(url.toString());
        } catch(SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Connection getH2Connection() {
        try {
            String url = "jdbc:h2:./h2db";
            String name = "test";
            String pass = "test";

            JdbcDataSource ds = new JdbcDataSource();
            ds.setURL(url);
            ds.setUser(name);
            ds.setPassword(pass);
            return DriverManager.getConnection(url, name, pass);
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
