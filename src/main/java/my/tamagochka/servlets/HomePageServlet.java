package my.tamagochka.servlets;

import my.tamagochka.resourceServer.ResourceServer;
import resources.TestResource;
import my.tamagochka.sax.ReadXMLFileSAX;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HomePageServlet extends HttpServlet {

    public static final String PAGE_URL = "/resources";
    private final ResourceServer resourceServer;

    public HomePageServlet(ResourceServer resourceServer) {
        this.resourceServer = resourceServer;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        String path = request.getParameter("path");
        resourceServer.setTestResource((TestResource) ReadXMLFileSAX.read(path));
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
    }





}
