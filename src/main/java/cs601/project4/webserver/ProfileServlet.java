package cs601.project4.webserver;

import cs601.project4.webserver.utilities.ClientInfo;
import cs601.project4.webserver.utilities.ServerConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpStatus;

import java.io.IOException;

//TODO::
public class ProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // retrieve the ID of this session
        String sessionId = req.getSession(true).getId();

        // determine whether the user is authenticated
        Object clientInfoObj = req.getSession().getAttribute(ServerConstants.CLIENT_INFO_KEY);
        if (clientInfoObj == null) {
            // not authed, need to log in
            resp.getWriter().println(ServerConstants.PAGE_HEADER);
            resp.getWriter().println("<p>You are not authenticated.</p>");
            resp.getWriter().println("<p><a href=\"/\">Log in</a></p>");
            resp.getWriter().println(ServerConstants.PAGE_FOOTER);
            return;
        }

        //get ClientInfo
        ClientInfo clientInfo = (ClientInfo) req.getSession().getAttribute(ServerConstants.CLIENT_INFO_KEY);

        req.getSession().setAttribute(ServerConstants.CLIENT_INFO_KEY, clientInfo);
        resp.setStatus(HttpStatus.OK_200);
        resp.getWriter().println(ServerConstants.PAGE_HEADER);


        String path = req.getServletPath();
        //TODO::
        if (path == "/profile/transfer") {
            resp.getWriter().println("Ticket transfered");
        } else if (path == "/profile/update"){
            resp.getWriter().println("Profile updated");
        } else {
            //profile
            //TODO:
            String name = "<p>Name: " + clientInfo.getName() + " "
                    + """
                    <form action="/profile/update">
                      <label for="name">Update name:</label>
                      <input type="text" id="name" name="name" maxlength="255" required="true">
                      <input type="submit" value="Submit">
                    </form></p>
                    """;
            resp.getWriter().println(name);
            String email = "<p>Email: " + clientInfo.getName() + " "
                    + """
                    <form action="/profile/update">
                      <label for="email">Update email:</label>
                      <input type="text" id="email" name="email" maxlength="255" required="true">
                      <input type="submit" value="Submit">
                    </form></p>
                    """;
            resp.getWriter().println(email);
            resp.getWriter().println("<h2>My tickets:</h2>");

        }
        resp.getWriter().println("<p><a href=\"/home\">Return to home</a></p>");
        resp.getWriter().println(ServerConstants.PAGE_FOOTER);
    }
}