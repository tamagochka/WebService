package my.tamagochka.main;

import my.tamagochka.accounts.AccountService;
import my.tamagochka.accounts.UserProfile;
import my.tamagochka.servlets.SessionsServlet;
import my.tamagochka.servlets.UsersServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Main {

    public static void main(String[] args) throws Exception {
        AccountService accountService = new AccountService();
        accountService.addNewUser(new UserProfile("admin"));
        accountService.addNewUser(new UserProfile("test"));

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(new UsersServlet(accountService)), "/signup");
        context.addServlet(new ServletHolder(new SessionsServlet(accountService)), "/signin");

        Server server = new Server(8080);
        server.setHandler(context);

        server.start();
        server.join();
    }
}
