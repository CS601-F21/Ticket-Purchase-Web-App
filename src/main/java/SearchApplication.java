package cs601.project3;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
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
        Gson gson = new Gson();
        Config config = new Config();
        try {
            FileReader reader = new FileReader("Config.json");
            config = gson.fromJson(reader, Config.class);
        } catch (FileNotFoundException e) {
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
        LOGGER.info("Logging set up. Level: " + loggingLevel);

        //build inverted index
        HashMap<Integer, String> files = new HashMap<>();
        files.put(FileData.REVIEW, config.reviewJson);
        files.put(FileData.QA, config.qaJson);

        //read json files
        JsonParser jsonParser = new JsonParser();
        FileData data = new FileData();
        try {
            LOGGER.info("Reading first 5000 entries of " + files.get(FileData.REVIEW) + "...");
            jsonParser.readFile(files.get(FileData.REVIEW), data.getReviewHashMap(), FileData.REVIEW);
            LOGGER.info("Reading first 5000 entries of " + files.get(FileData.QA) + "...");
            jsonParser.readFile(files.get(FileData.QA), data.getQaHashMap(), FileData.QA);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "File not found.");
            System.exit(1);
        }

        //create inverted index
        LOGGER.info("Creating inverted index...");
        data.getReviewInvertedIndex().populate(data.getReviewHashMap());
        data.getQaInvertedIndex().populate(data.getQaHashMap());

        //start server
        int port = config.searchPort;
        HTTPServer server = new HTTPServer(port, config);
        //The request GET /reviewsearch will be dispatched to the
        //handle method of the ReviewSearchHandler.
        server.addMapping("/reviewsearch", new ReviewSearchHandler(data));
        //The request GET /find will be dispatched to the
        //handle method of the FindHandler.
        server.addMapping("/find", new FindHandler(data));
        server.startup();
    }
}
