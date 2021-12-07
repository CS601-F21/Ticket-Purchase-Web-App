package cs601.project4.webserver.utilities;

/**
 * A class to maintain info about each client.
 * source: https://github.com/CS601-F21/code-examples/blob/main/JettyLoginServer/src/main/java/utilities/ClientInfo.java
 */
public class ClientInfo {

    private String name;

    /**
     * Constructor
     * @param name
     */
    public ClientInfo(String name) {
        this.name = name;
    }
    /**
     * return name
     * @return
     */
    public String getName() {
        return name;
    }
}