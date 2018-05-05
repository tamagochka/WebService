package my.tamagochka.accounts;

import my.tamagochka.dbService.DBService;
import my.tamagochka.dbService.UsersDataSet;

public class AccountService {

    private final DBService dbService;

    public AccountService() {
        dbService = new DBService();
    }

    public void addNewUser(UsersDataSet userProfile) {
        dbService.addUser(userProfile);
    }

    public UsersDataSet getUserByLogin(String login) {
        if (dbService.getUsersByLogin(login).isEmpty()) return null;
        UsersDataSet result = dbService.getUsersByLogin(login).get(0);
        return result;
    }
}
