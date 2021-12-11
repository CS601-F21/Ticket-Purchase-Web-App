package cs601.project4.webserver.utilities;

/**
 * A class to maintain info about each client.
 * source: https://github.com/CS601-F21/code-examples/blob/main/JettyLoginServer/src/main/java/utilities/ClientInfo.java
 */
public class ClientInfo {

    private String name;
    private final String email;

    /**
     * Constructor
     * @param name name of user, initially from Slack
     * @param email email of user from Slack
     */
    public ClientInfo(String name, String email) {
        this.name = name;
        this.email = email;
    }
    /**
     * return name
     * @return name on slack profile
     */
    public String getName() {
        return name;
    }

    /**
     * return email
     * @return email address on slack profile
     */
    public String getEmail() {
        return email;
    }

    /**
     * set user's name
     */
    public void setName(String name){
        this.name = name;
    }

}