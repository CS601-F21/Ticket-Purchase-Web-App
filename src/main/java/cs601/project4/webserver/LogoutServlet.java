package cs601.project4.webserver;

import cs601.project4.webserver.utilities.ServerConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Handles a request to sign out
 */
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // log out by invalidating the session
        req.getSession().invalidate();
        resp.getWriter().println(ServerConstants.PAGE_HEADER);
        resp.getWriter().println("<p>You have been signed out.</p>");
        resp.getWriter().println("<p><a href=\"/\">Log in</a></p>");
        resp.getWriter().println(ServerConstants.PAGE_FOOTER);

    }
}
