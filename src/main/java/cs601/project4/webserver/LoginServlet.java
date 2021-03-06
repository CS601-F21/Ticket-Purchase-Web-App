package cs601.project4.webserver;

import cs601.project4.Config;
import cs601.project4.database.DBCPDataSource;
import cs601.project4.database.DatabaseManager;
import cs601.project4.webserver.utilities.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpStatus;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Implements logic for the /login path where Slack will redirect requests after
 * the user has entered their auth info.
 * source: https://github.com/CS601-F21/code-examples/blob/main/JettyLoginServer/src/main/java/example/login/LoginServlet.java
 * @author Wyatt Mumford
 */
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // retrieve the ID of this session
        String sessionId = req.getSession(true).getId();

        // determine whether the user is already authenticated
        Object clientInfoObj = req.getSession().getAttribute(ServerConstants.CLIENT_INFO_KEY);
        if(clientInfoObj != null) {
            // already authed, no need to log in
            resp.getWriter().println(ServerConstants.PAGE_HEADER);
            resp.getWriter().println("<p>You are already authenticated.</p>");
            resp.getWriter().println("<p><a href=\"/home\">Return to home page</a>");
            resp.getWriter().println(ServerConstants.PAGE_FOOTER);
            return;
        }

        // retrieve the config info from the context
        Config config = (Config) req.getServletContext().getAttribute(ServerConstants.CONFIG_KEY);

        // retrieve the code provided by Slack
        String code = req.getParameter(ServerConstants.CODE_KEY);

        // generate the url to use to exchange the code for a token:
        // After the user successfully grants your app permission to access their Slack profile,
        // they'll be redirected back to your service along with the typical code that signifies
        // a temporary access code. Exchange that code for a real access token using the
        // /openid.connect.token method.
        String url = ServerUtils.generateSlackTokenURL(config.client_id, config.client_secret, code, config.redirect_uri);

        // Make the request to the token API
        String responseString = HTTPFetcher.doGet(url, null);
        Map<String, Object> response = ServerUtils.jsonStrToMap(responseString);

        ClientInfo clientInfo = ServerUtils.verifyTokenResponse(response, sessionId);

        resp.setStatus(HttpStatus.OK_200);
        resp.getWriter().println(ServerConstants.PAGE_HEADER);

        if(clientInfo == null) {
            resp.getWriter().println("<p>You are not authenticated.</p>");
            resp.getWriter().println("<p><a href=\"/\">Log in</a></p>");
        } else {

            try (Connection connection = DBCPDataSource.getConnection()) {
                ResultSet result = DatabaseManager.executeSelectUser(connection, clientInfo.getEmail());
                if (!result.next()){
                    //add new user to database
                    resp.getWriter().println("<p>Hello, " + clientInfo.getName() + ".</p>");
                    resp.getWriter().println("<p>Welcome to Ticket-Get-It. Creating your account now...</p>");

                    DatabaseManager.executeInsertUser(connection, clientInfo);

                } else {
                    clientInfo.setName(result.getString(1));
                    resp.getWriter().println("<p>Welcome back, " + clientInfo.getName() + ".</p>");
                    resp.getWriter().println("<p>You have been successfully logged in.</p>");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }



            req.getSession().setAttribute(ServerConstants.CLIENT_INFO_KEY, clientInfo);


            resp.getWriter().println("<p><a href=\"/home\">Continue to Home page</a></p>");
        }
        resp.getWriter().println(ServerConstants.PAGE_FOOTER);
    }
}
