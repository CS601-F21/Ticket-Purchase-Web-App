package cs601.project4;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.PrintWriter;

/**
 * A utility class to send server response messages and other helper functions.
 * sources: https://github.com/CS601-F21/code-examples/blob/main/Web/src/main/java/examples/web/server/ServerUtils.java
 * https://github.com/CS601-F21/code-examples/blob/main/JettyLoginServer/src/main/java/utilities/LoginUtilities.java
 * @author Wyatt Mumford
 */
public class ServerUtils {
    /**
     * Send the status line of an HTTP 200 OK response.
     * @param writer writer where HTTP response is written
     */
    public static void send200(PrintWriter writer) {
        writer.printf("%s %s\r\n", ServerConstants.VERSION, ServerConstants.OK);
        writer.printf("%s \r\n\r\n", ServerConstants.CONNECTION_CLOSE);
    }
    /**
     * Send the status line of an HTTP 400 Bad Request response.
     * @param writer writer where HTTP response is written
     */
    public static void send400(PrintWriter writer) {
        writer.printf("%s %s\r\n", ServerConstants.VERSION, ServerConstants.BAD_REQUEST);
        writer.printf("%s \r\n\r\n", ServerConstants.CONNECTION_CLOSE);
        writer.println(ServerConstants.BAD_REQUEST_PAGE);
    }

    /**
     * Send the status line of an HTTP 404 Not Found response.
     * @param writer writer where HTTP response is written
     */
    public static void send404(PrintWriter writer) {
        writer.printf("%s %s\r\n", ServerConstants.VERSION, ServerConstants.NOT_FOUND);
        writer.printf("%s \r\n\r\n", ServerConstants.CONNECTION_CLOSE);
        writer.println(ServerConstants.NOT_FOUND_PAGE);
    }

    /**
     * Send the status line of an HTTP 405 Method Not Allowed response.
     * @param writer writer where HTTP response is written
     */
    public static void send405(PrintWriter writer) {
        writer.printf("%s %s\r\n", ServerConstants.VERSION, ServerConstants.NOT_ALLOWED);
        writer.printf("%s \r\n\r\n", ServerConstants.CONNECTION_CLOSE);
        writer.println(ServerConstants.NOT_ALLOWED_PAGE);
    }

    /**
     * Hash the session ID to generate a nonce.
     * Uses Apache Commons Codec
     * See https://www.baeldung.com/sha-256-hashing-java
     * @param sessionId unique id generated this session
     * @return nonce
     */
    public static String generateNonce(String sessionId) {
        String sha256hex = DigestUtils.sha256Hex(sessionId);
        return sha256hex;
    }

    /**
     * Generates the URL to make the initial request to the authorize API.
     * @param clientId
     * @param state
     * @param nonce
     * @param redirectURI
     * @return Slack authorize URL
     */
    public static String generateSlackAuthorizeURL(String clientId, String state, String nonce, String redirectURI) {

        String url = String.format("https://%s/%s?%s=%s&%s=%s&%s=%s&%s=%s&%s=%s&%s=%s",
                ServerConstants.HOST,
                ServerConstants.AUTH_PATH,
                ServerConstants.RESPONSE_TYPE_KEY,
                ServerConstants.RESPONSE_TYPE_VALUE,
                ServerConstants.SCOPE_KEY,
                ServerConstants.SCOPE_VALUE,
                ServerConstants.CLIENT_ID_KEY,
                clientId,
                ServerConstants.STATE_KEY,
                state,
                ServerConstants.NONCE_KEY,
                nonce,
                ServerConstants.REDIRECT_URI_KEY,
                redirectURI
        );
        return url;
    }
}

