package cs601.project3;

/**
 * Holds configuration information used to run program
 * @author Wyatt Mumford
 */
public class Config {
    public boolean logging;
    public String logFile;
    public int poolSize;

    public int searchPort;
    public String reviewJson;
    public String qaJson;

    public int chatPort;
    public String channelId;
    public String slackbotToken;

    public void setSlackbotToken(String token){
        slackbotToken = token;
    }
}
