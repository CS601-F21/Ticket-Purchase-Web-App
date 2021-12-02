package cs601.project4;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Holds configuration information used to run program
 * @author Wyatt Mumford
 */
public class Config {
    public boolean logging;
    public String logFile;
    public int poolSize;

    public int port;

    public String slackbotToken;

    // These variable names violate Java style guidelines
    // in order to be consistent with the naming conventions
    // in the Slack API
    public String redirect_uri;
    public String client_id;
    public String client_secret;

    /**
     * Reads config json file, populating fields
     */
    public Config(){
        Gson gson = new Gson();
        Config config = new Config();
        try {
            FileReader reader = new FileReader("Config.json");
            config = gson.fromJson(reader, Config.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
