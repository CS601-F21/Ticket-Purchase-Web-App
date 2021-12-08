package cs601.project4;

/**
 * Holds configuration information used to run program
 * @author Wyatt Mumford
 */
public class Config {
    public boolean logging;
    public String logFile;

    public int port;

    // These variable names violate Java style guidelines
    // in order to be consistent with the naming conventions
    // in the Slack API
    public String redirect_uri;
    public String client_id;
    public String client_secret;

    public String database;
    public String username;
    public String password;


}
