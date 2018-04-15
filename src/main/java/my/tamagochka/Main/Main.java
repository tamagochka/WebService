package my.tamagochka.Main;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Main {

    public static void main(String[] args) throws Exception{
        Frontend frontend = new Frontend();
        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler();
        server.setHandler(context);
        context.addServlet(new ServletHolder(frontend), "/authform");

        server.start();
        server.join();
    }
}
