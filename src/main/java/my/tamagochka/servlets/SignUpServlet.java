package my.tamagochka.servlets;

import my.tamagochka.accounts.AccountService;
import my.tamagochka.dbService.UsersDataSet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SignUpServlet extends HttpServlet {

    private final AccountService accountService;

    public SignUpServlet(AccountService accountService) { this.accountService = accountService; }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        String login = request.getParameter("login");
        String pass = request.getParameter("password");
        accountService.addNewUser(new UsersDataSet(login, pass));
    }
}
