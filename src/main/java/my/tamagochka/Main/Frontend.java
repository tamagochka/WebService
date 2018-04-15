package my.tamagochka.Main;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Frontend extends HttpServlet {

    private String login = "";

    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse responce)
            throws ServletException, IOException {
        Map<String, Object> pageVars = createPageVarsMap(request);
        pageVars.put("message", "");
        responce.getWriter().println(PageGenerator.instance().getPage("page.html", pageVars));
        responce.setContentType("text/html;charset=utf-8");
        responce.setStatus(HttpServletResponse.SC_OK);

    }

    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse responce)
            throws ServletException, IOException {

    }

    private static Map<String, Object> createPageVarsMap(HttpServletRequest request) {
        Map<String, Object> pageVars = new HashMap<>();
        pageVars.put("metod", request.getMethod());
        pageVars.put("URL", request.getRequestURL().toString());
        pageVars.put("pathInfo", request.getPathInfo());
        pageVars.put("sessionId", request.getSession().getId());
        pageVars.put("parameters", request.getParameterMap().toString());
        return pageVars;
    }

}
