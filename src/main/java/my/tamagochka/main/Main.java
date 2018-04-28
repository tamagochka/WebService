package my.tamagochka.main;

import my.tamagochka.accounts.AccountService;
import my.tamagochka.accounts.UserProfile;
import my.tamagochka.dbService.DBException;
import my.tamagochka.dbService.DBService;
import my.tamagochka.dbService.dataSets.UsersDataSet;
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
/*        AccountService accountService = new AccountService();
        accountService.addNewUser(new UserProfile("admin"));
        accountService.addNewUser(new UserProfile("test"));

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(new UsersServlet(accountService)), "/api/v1/users");
        context.addServlet(new ServletHolder(new SessionsServlet(accountService)), "/api/v1/sessions");

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase("public_html");

        HandlerList handlerList = new HandlerList();
        handlerList.setHandlers(new Handler[] {resourceHandler, context});
        Server server = new Server(8080);
        server.setHandler(handlerList);

        server.start();
        server.join(); */
        DBService dbService = new DBService();
        dbService.printConnectInfo();
        try {
            long userId = dbService.addUser("test");
            System.out.println("Added user id: " + userId);

            UsersDataSet dataSet = dbService.getUser(userId);
            System.out.println("User data set: " + dataSet);

            dbService.cleanUp();
        } catch(DBException e) {
            e.printStackTrace();
        }
    }
}
