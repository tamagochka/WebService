package my.tamagochka.dbService.DAO;

import my.tamagochka.dbService.dataSets.UsersDataSet;
import my.tamagochka.dbService.executor.Executor;

import java.sql.Connection;
import java.sql.SQLException;

public class UsersDAO {

    private Executor executor;

    public UsersDAO(Connection connection) { this.executor = new Executor(connection); }

    public UsersDataSet get(long id) throws SQLException {
        return executor.execQuery("SELECT * FROM users WHERE id=" + id, result -> {
           result.next();
           return new UsersDataSet(result.getLong(1), result.getString(2));
        });
    }

    public long getUserId(String name) throws SQLException {
        return executor.execQuery("SELECT * FROM users WHERE user_name='" + name + "'", result -> {
            result.next();
            return result.getLong(1);
        });
    }

    public void insertUser(String name) throws SQLException {
        executor.execUpdate("INSERT INTO users (user_name) VALUES ('" + name + "')");
    }

    public void createTable() throws SQLException {
        executor.execUpdate("CREATE TABLE IF NOT EXISTS users (id BIGINT AUTO_INCREMENT, user_name VARCHAR(30), PRIMARY KEY (id))");
    }

    public void dropTable() throws SQLException {
        executor.execUpdate("DROP TABLE users");
    }

}
