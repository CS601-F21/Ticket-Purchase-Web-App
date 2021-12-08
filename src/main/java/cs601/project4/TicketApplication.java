package cs601.project4;

import com.google.gson.Gson;
import cs601.project4.database.DBCPDataSource;
import cs601.project4.webserver.*;
import cs601.project4.webserver.utilities.ServerConstants;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Driver class for Ticket purchase web application
 * source: https://github.com/CS601-F21/code-examples/blob/main/JettyLoginServer/src/main/java/example/login/LoginServer.java
 * @author Wyatt Mumford
 */
public class TicketApplication {
    public static void main(String[] args) throws Exception {

        //set up Config
        Gson gson = new Gson();
        Config config = new Config();
        try {
            FileReader reader = new FileReader("Config.json");
            config = gson.fromJson(reader, Config.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //set up logger
        Logger LOGGER = Logger.getLogger(TicketApplication.class.getName());
        Level loggingLevel;
        if (config.logging){
            loggingLevel = Level.ALL;
        } else {
            loggingLevel = Level.INFO;
        }
        LOGGER.setLevel(loggingLevel);
        try {
            LOGGER.addHandler(new FileHandler(config.logFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.info("Logging set up. Level: " + loggingLevel);

        //set up database
        LOGGER.info("Setting up database connection...");
        DBCPDataSource.initialize(config);

        //TODO:

        // make the config information available across servlets by setting an attribute in the context
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setAttribute(ServerConstants.CONFIG_KEY, config);

        //configure server
        int port = config.port;
        LOGGER.info("Starting server...");
        Server server = new Server(port);

        // the default path will direct to a landing page with "Login with Slack" button
        context.addServlet(LandingServlet.class, "/");
        // Once authenticated, Slack will redirect the user back to /login
        context.addServlet(LoginServlet.class, "/login");
        // homepage
        context.addServlet(HomeServlet.class, "/home");
        //profile
        context.addServlet(ProfileServlet.class, "/profile");
        context.addServlet(ProfileServlet.class, "/profile/update");
        context.addServlet(ProfileServlet.class, "/profile/transfer");
        //events
        context.addServlet(EventServlet.class, "/event");
        context.addServlet(EventServlet.class, "/event/create");
        // handle logout
        context.addServlet(LogoutServlet.class, "/logout");

        //TODO:shutdown

        server.setHandler(context);
        server.start();
    }
}
