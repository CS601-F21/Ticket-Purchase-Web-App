package cs601.project4;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * allows user to search Project 1 inverted index
 * @author Wyatt Mumford
 */
public class SearchApplication {
    public static void main(String[] args) {

        //set up Config
        Config config = new Config();

        //set up logger
        Logger LOGGER = Logger.getLogger(HTTPServer.class.getName());
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

        //start server
        int port = config.port;
        HTTPServer server = new HTTPServer(port, config);
        //The request GET /reviewsearch will be dispatched to the
        //handle method of the ReviewSearchHandler.
        server.addMapping("/reviewsearch", new ReviewSearchHandler());
        server.startup();
    }
}
