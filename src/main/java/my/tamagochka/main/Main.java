package my.tamagochka.main;

import my.tamagochka.dbService.DBException;
import my.tamagochka.dbService.DBService;
import my.tamagochka.dbService.dataSets.UsersDataSet;

public class Main {

    public static void main(String[] args) throws Exception {
        DBService dbService = new DBService();
        dbService.printConnectInfo();
        try {
            long userId = dbService.addUser("test");
            System.out.println("Added user id: " + userId);

            UsersDataSet dataSet = dbService.getUser(userId);
            System.out.println("User data set: " + dataSet);

            dbService.closeConnection();
        } catch(DBException e) {
            e.printStackTrace();
        }
    }
}
