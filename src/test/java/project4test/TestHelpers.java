package project4test;

import cs601.project4.webserver.utilities.Config;
import cs601.project4.webserver.utilities.ServerConstants;
import cs601.project4.webserver.LandingServlet;
import cs601.project4.webserver.LoginServlet;
import cs601.project4.webserver.LogoutServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper functions used in Test classes
 * @author Wyatt Mumford
 */
public class TestHelpers {

    /**
     * Starts server on new thread
     * @param config Config file
     * @return thread running server
     */
    public Thread serverSetup(Config config){

        //set up logger
        Logger LOGGER = Logger.getLogger(Server.class.getName());
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

        // make the config information available across servlets by setting an attribute in the context
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setAttribute(ServerConstants.CONFIG_KEY, config);

        //start server
        int port = config.port;
        Server server = new Server(port);

        // the default path will direct to a landing page with "Login with Slack" button
        context.addServlet(LandingServlet.class, "/");
        // Once authenticated, Slack will redirect the user back to /login
        context.addServlet(LoginServlet.class, "/login");
        //TODO
        // handle logout
        context.addServlet(LogoutServlet.class, "/logout");

        server.setHandler(context);

        //start thread
        Thread serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.start();
        return serverThread;

    }

    /**
     * send /shutdown to server, block until thread terminates
     */
    //TODO?
    public void shutdown(int port, Thread thread){
        HttpClient client = HttpClient.newHttpClient();
        String uri = "http://localhost:" + port + "/shutdown";
        HttpRequest request = HttpRequest.newBuilder(URI.create(uri)).build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            thread.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
