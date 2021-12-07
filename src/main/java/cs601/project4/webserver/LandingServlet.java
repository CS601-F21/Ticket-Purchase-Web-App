package cs601.project4.webserver;

import cs601.project4.Config;
import cs601.project4.webserver.utilities.ServerUtils;
import cs601.project4.webserver.utilities.ServerConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpStatus;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Landing page that allows a user to request to login with Slack.
 * source: https://github.com/CS601-F21/code-examples/blob/main/JettyLoginServer/src/main/java/example/login/LandingServlet.java
 */
public class LandingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // retrieve the ID of this session
        String sessionId = req.getSession(true).getId();

        // determine whether the user is already authenticated
        Object clientInfoObj = req.getSession().getAttribute(ServerConstants.CLIENT_INFO_KEY);
        if(clientInfoObj != null) {
            // already authed, no need to log in
            resp.setStatus(HttpStatus.OK_200);
            resp.getWriter().println(ServerConstants.PAGE_HEADER);
            resp.getWriter().println("<p>You have already been authenticated.</p>");
            resp.getWriter().println("<p><a href=\"/home\">Return to home</a></p>");
            resp.getWriter().println(ServerConstants.PAGE_FOOTER);
            return;
        }

        // retrieve the config info from the context
        Config config = (Config) req.getServletContext().getAttribute(ServerConstants.CONFIG_KEY);

        /** From the OpenID spec:
         * state
         * RECOMMENDED. Opaque value used to maintain state between the request and the callback.
         * Typically, Cross-Site Request Forgery (CSRF, XSRF) mitigation is done by cryptographically
         * binding the value of this parameter with a browser cookie.
         *
         * Use the session ID for this purpose.
         */
        String state = sessionId;

        /** From the Open ID spec:
         * nonce
         * OPTIONAL. String value used to associate a Client session with an ID Token, and to mitigate
         * replay attacks. The value is passed through unmodified from the Authentication Request to
         * the ID Token. Sufficient entropy MUST be present in the nonce values used to prevent attackers
         * from guessing values. For implementation notes, see Section 15.5.2.
         */
        String nonce = ServerUtils.generateNonce(state);

        // Generate url for request to Slack
        String url = ServerUtils.generateSlackAuthorizeURL(config.client_id,
                state,
                nonce,
                config.redirect_uri);

        resp.setStatus(HttpStatus.OK_200);
        PrintWriter writer = resp.getWriter();
        //TODO::
        writer.println(ServerConstants.PAGE_HEADER);
        writer.println("<p>Please log in with Slack:</p>");
        writer.println("<a href=\""+url+"\"><img src=\"" + ServerConstants.BUTTON_URL +"\"/></a>");
        writer.println(ServerConstants.PAGE_FOOTER);
    }
}
