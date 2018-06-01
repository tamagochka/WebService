package my.tamagochka.servlets;

import my.tamagochka.accountServer.AccountServer;
import my.tamagochka.accountServer.AccountServerI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HomePageServletTest {

    private AccountServerI accountServer =  mock(AccountServer.class);

    private HttpServletResponse getMockedResponse(StringWriter stringWriter) throws IOException {
        HttpServletResponse response = mock(HttpServletResponse.class);
        final PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        return response;
    }

    private HttpServletRequest getMockedRequest(String url) {
        HttpSession httpSession = mock(HttpSession.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getSession()).thenReturn(httpSession);
        when(request.getPathInfo()).thenReturn(url);
        return request;
    }

    @org.junit.jupiter.api.Test
    void doRemove() throws Exception {

        when(accountServer.getUsersLimit()).thenReturn(10);

        final StringWriter stringWriter = new StringWriter();
        HttpServletResponse response = getMockedResponse(stringWriter);
        HttpServletRequest request = getMockedRequest(HomePageServlet.PAGE_URL);
//        when(request.getParameter("remove")).thenReturn("");
        HomePageServlet homePage = new HomePageServlet(accountServer);
        homePage.doGet(request, response);
        assertEquals("10", stringWriter.toString().trim());
//        verify(accountServer, times(1)).removeUser();
    }
}