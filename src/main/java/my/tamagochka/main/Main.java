package my.tamagochka.main;

import my.tamagochka.accountServer.AccountServer;
import my.tamagochka.accountServer.AccountServerController;
import my.tamagochka.accountServer.AccountServerControllerMBean;
import my.tamagochka.accountServer.AccountServerI;
import my.tamagochka.servlets.HomePageServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class Main {

    public static void main(String[] args) throws Exception {

        int port = 8080;

        AccountServerI accountServer = new AccountServer(10);

        AccountServerControllerMBean serverStatistics = new AccountServerController(accountServer);
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName("Admin:type=AccountServerController");
        mbs.registerMBean(serverStatistics, name);

        Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(new HomePageServlet(accountServer)), HomePageServlet.PAGE_URL);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setResourceBase("static");

        HandlerList handlerList = new HandlerList();
        handlerList.setHandlers(new Handler[] {resourceHandler, context});
        server.setHandler(handlerList);

        server.start();
        java.util.logging.Logger.getGlobal().info("Server started");
        server.join();


    }
}
