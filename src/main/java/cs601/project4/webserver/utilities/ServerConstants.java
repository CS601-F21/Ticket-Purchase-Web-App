package cs601.project4.webserver.utilities;

/**
 * A helper class to store various constants used for the HTTP server.
 * sources: https://github.com/CS601-F21/code-examples/blob/main/Web/src/main/java/examples/web/server/HttpConstants.java
 * https://github.com/CS601-F21/code-examples/blob/main/JettyLoginServer/src/main/java/example/login/LoginServerConstants.java
 */
public class ServerConstants {

    public static final String HOST = "slack.com";
    public static final String AUTH_PATH = "openid/connect/authorize";
    public static final String TOKEN_PATH = "api/openid.connect.token";
    public static final String RESPONSE_TYPE_KEY = "response_type";
    public static final String RESPONSE_TYPE_VALUE= "code";
    public static final String CODE_KEY= "code";
    public static final String SCOPE_KEY = "scope";
    public static final String SCOPE_VALUE = "openid%20profile%20email";
    public static final String CLIENT_ID_KEY = "client_id";
    public static final String CLIENT_SECRET_KEY = "client_secret";
    public static final String STATE_KEY = "state";
    public static final String NONCE_KEY = "nonce";
    public static final String REDIRECT_URI_KEY = "redirect_uri";
    public static final String OK_KEY = "ok";


    public static final String CONFIG_KEY = "config_key";
    public static final String CLIENT_INFO_KEY = "client_info_key";
    public static final String BUTTON_URL = "https://platform.slack-edge.com/img/sign_in_with_slack@2x.png";

    public static final String IS_AUTHED_KEY = "is_authed";
    public static final String NAME_KEY = "name";
    public static final String EMAIL_KEY = "email";

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String VERSION = "HTTP/1.1";

    public static final String OK = "200 OK";
    public static final String BAD_REQUEST = "400 Bad Request";
    public static final String NOT_FOUND = "404 Not Found";
    public static final String NOT_ALLOWED = "405 Method Not Allowed";

    public static final String CONTENT_LENGTH = "Content-Length:";
    public static final String CONNECTION_CLOSE = "Connection: close";

    public static final String PAGE_HEADER = "<!DOCTYPE html>\n" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
            "<head>\n" +
            "  <title>Log in with Slack</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<h1>Ticket-Get-It</h1>" +
            "\n";

    public static final String PAGE_FOOTER = "\n" +
            "</body>\n" +
            "</html>";


    public static final String REVIEW_SEARCH_PAGE = """
                    <!DOCTYPE html>
                    <html xmlns="http://www.w3.org/1999/xhtml">
                    <head>
                       <title>Review Search</title>
                    </head>
                    <body>

                       <form>
                           <label for="query">Query:</label><br/>
                           <input type="text" id="query" name="query"<br/>
                       </form>

                    </body>
                    </html>""";
    public static final String SLACKBOT_PAGE = """
                    <!DOCTYPE html>
                    <html xmlns="http://www.w3.org/1999/xhtml">
                    <head>
                       <title>Find</title>
                    </head>
                    <body>

                       <form>
                           <label for="message">message:</label><br/>
                           <input type="text" id="message" name="message"<br/>
                       </form>

                    </body>
                    </html>""";

    public static final String SHUTDOWN_PAGE = """
                    <!DOCTYPE html>
                    <html xmlns="http://www.w3.org/1999/xhtml">
                    <head>
                       <title>Shutdown</title>
                    </head>
                    <body>

                        <p>Shutting down server.</p>

                    </body>
                    </html>""";

    public static final String BAD_REQUEST_PAGE = """
            <!DOCTYPE html>
            <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
              <title>400 Bad Request</title>
            </head>
            <body>

              <p>Incorrect HTTP Request.</p>

            </body>
            </html>""";

    public static final String NOT_FOUND_PAGE = """
            <!DOCTYPE html>
            <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
              <title>404 Resource not found</title>
            </head>
            <body>

              <p>The resource you are looking for was not found.</p>

            </body>
            </html>""";

    public static final String NOT_ALLOWED_PAGE = """
            <!DOCTYPE html>
            <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
              <title>405 Method not allowed</title>
            </head>
            <body>

              <p>The method you requested is not allowed.</p>

            </body>
            </html>""";
}
