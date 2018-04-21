package my.tamagochka.servlets;

import my.tamagochka.accounts.AccountService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UsersServlet extends HttpServlet {

    private final AccountService accountService;

    public UsersServlet(AccountService accountService) { this.accountService = accountService; }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // todo: home work
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // todo: home work
    }

    public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // todo: home work
    }

    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // todo: home work
    }

}
