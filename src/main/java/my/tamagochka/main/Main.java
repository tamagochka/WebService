package my.tamagochka.main;

import my.tamagochka.resourceServer.ResourceServer;
import my.tamagochka.resourceServer.ResourceServerMBean;
import my.tamagochka.servlets.HomePageServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class Main {

    public static void main(String[] args) throws Exception {

        int port = 8080;

        ResourceServerMBean resourceServer = new ResourceServer();

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName("Admin:type=ResourceServerController");
        mbs.registerMBean(resourceServer, name);

        Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(new HomePageServlet((ResourceServer) resourceServer)), HomePageServlet.PAGE_URL);


        server.setHandler(context);

        server.start();
        java.util.logging.Logger.getGlobal().info("Server started");
        server.join();


    }
}
