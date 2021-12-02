package cs601.project3;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Allows client to post to Slack API
 * @author Wyatt Mumford
 */
public class ChatApplication {
    public static void main(String[] args) {

        //set up Config
        Gson gson = new Gson();
        Config config = new Config();
        try {
            FileReader reader = new FileReader("Config.json");
            config = gson.fromJson(reader, Config.class);
            BufferedReader tokenReader = new BufferedReader(new FileReader("SlackbotToken.txt"));
            String token = tokenReader.readLine();
            config.setSlackbotToken(token);
        } catch (IOException e) {
            e.printStackTrace();
        }

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

        //start server
        int port = config.chatPort;
        HTTPServer server = new HTTPServer(port, config);
        server.addMapping("/slackbot", new ChatHandler());
        server.startup();
    }
}
