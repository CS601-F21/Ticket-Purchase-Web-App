package cs601.project3;

import java.io.PrintWriter;

/**
 * A utility class to send server response messages.
 * source: https://github.com/CS601-F21/code-examples/blob/main/Web/src/main/java/examples/web/server/ServerUtils.java
 */
public class ServerUtils {
    /**
     * Send the status line of an HTTP 200 OK response.
     * @param writer
     */
    public static void send200(PrintWriter writer) {
        writer.printf("%s %s\r\n", HTTPConstants.VERSION, HTTPConstants.OK);
        writer.printf("%s \r\n\r\n", HTTPConstants.CONNECTION_CLOSE);
    }

    public static void send400(PrintWriter writer) {
        writer.printf("%s %s\r\n", HTTPConstants.VERSION, HTTPConstants.BAD_REQUEST);
        writer.printf("%s \r\n\r\n", HTTPConstants.CONNECTION_CLOSE);
        writer.println(HTTPConstants.BAD_REQUEST_PAGE);
    }

    /**
     * Send the status line of an HTTP 404 Not Found response.
     * @param writer
     */
    public static void send404(PrintWriter writer) {
        writer.printf("%s %s\r\n", HTTPConstants.VERSION, HTTPConstants.NOT_FOUND);
        writer.printf("%s \r\n\r\n", HTTPConstants.CONNECTION_CLOSE);
        writer.println(HTTPConstants.NOT_FOUND_PAGE);
    }

    /**
     * Send the status line of an HTTP 405 Method Not Allowed response.
     * @param writer
     */
    public static void send405(PrintWriter writer) {
        writer.printf("%s %s\r\n", HTTPConstants.VERSION, HTTPConstants.NOT_ALLOWED);
        writer.printf("%s \r\n\r\n", HTTPConstants.CONNECTION_CLOSE);
        writer.println(HTTPConstants.NOT_ALLOWED_PAGE);

    }
}

