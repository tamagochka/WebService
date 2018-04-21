package my.tamagochka.servlets;

import my.tamagochka.accounts.AccountService;
import my.tamagochka.accounts.UserProfile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UsersServlet extends HttpServlet {

    private final AccountService accountService;

    public UsersServlet(AccountService accountService) { this.accountService = accountService; }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String login = request.getParameter("login");
        String pass = request.getParameter("password");
        accountService.addNewUser(new UserProfile(login, pass, "email"));
    }
}
