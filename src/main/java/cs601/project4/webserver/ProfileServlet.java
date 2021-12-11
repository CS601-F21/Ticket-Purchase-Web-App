package cs601.project4.webserver;

import cs601.project4.database.DBCPDataSource;
import cs601.project4.database.DatabaseManager;
import cs601.project4.webserver.utilities.ClientInfo;
import cs601.project4.webserver.utilities.ServerConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpStatus;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        if (path.equals("/profile/transfer")) {
            resp.getWriter().println("<p>Ticket transferred.</p>");
        } else if (path.equals("/profile/update")){
            //todo switch no break
            resp.getWriter().println("<p>Profile updated.</p>");
            /* FALLTHROUGH */
        } else {
            //profile
            //TODO:
            String nameLine = """
                    <form action='/profile/update'>
                      <label for='name'>
                      <p>Name: """ +
                    clientInfo.getName() + """
                      </label>
                      <input type='text' id='name' name='name' maxlength='255' required='true'>
                      <input type='submit' value='Update name'>
                    </form></p>
                    """;
            resp.getWriter().println(nameLine);
            String emailLine = "<p>Email: " + clientInfo.getEmail() + "</p>";
            resp.getWriter().println(emailLine);
            //display tickets owned by user
            resp.getWriter().println("<h2>My tickets:</h2>");

            //display events created by user
            resp.getWriter().println("<h2>My events:</h2>");
            try (Connection connection = DBCPDataSource.getConnection()) {
                ResultSet result = DatabaseManager.executeSelectUsersEvents(connection, clientInfo.getEmail());
                resp.getWriter().print("<p>");
                while (result.next()){
                    String eventName = result.getString(1);
                    int id = result.getInt(2);
                    String eventListing = "<a href='/event/details?id=" + id + "'>" + eventName + "</a><br/>";
                    resp.getWriter().print(eventListing);
                }
                resp.getWriter().println("</p>");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        resp.getWriter().println("<p><a href='/home'>Return to Home Page</a></p>");
        resp.getWriter().println(ServerConstants.PAGE_FOOTER);
    }
}