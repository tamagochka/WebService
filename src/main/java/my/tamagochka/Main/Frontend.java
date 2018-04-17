package my.tamagochka.Main;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Frontend extends HttpServlet {

    private String login = "";

    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse responce)
            throws ServletException, IOException {

        responce.getWriter().println(request.getParameter("key"));
        responce.setStatus(HttpServletResponse.SC_OK);



/*        Map<String, Object> pageVars = createPageVarsMap(request);
        pageVars.put("message", "");
        responce.getWriter().println(PageGenerator.instance().getPage("page.html", pageVars));
        responce.setContentType("text/html;charset=utf-8");
        responce.setStatus(HttpServletResponse.SC_OK);*/

    }

    /*
    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse responce)
            throws ServletException, IOException {
        Map<String, Object> pageVars = createPageVarsMap(request);
        String message = request.getParameter("message");
        responce.setContentType("text/html;charset=utf-8");
        if(message == null || message.isEmpty()) {
            responce.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            responce.setStatus(HttpServletResponse.SC_OK);
        }
        pageVars.put("message", message == null ? "" : message);
        responce.getWriter().println(PageGenerator.instance().getPage("page.html", pageVars));
    }

    private static Map<String, Object> createPageVarsMap(HttpServletRequest request) {
        Map<String, Object> pageVars = new HashMap<>();
        pageVars.put("method", request.getMethod());
        pageVars.put("URL", request.getRequestURL().toString());
        pageVars.put("pathInfo", request.getPathInfo());
        pageVars.put("sessionId", request.getSession().getId());
        pageVars.put("parameters", request.getParameterMap().toString());
        return pageVars;
    }
    */
}
