package cs601.project4;

/**
 * A helper class to store various constants used for the HTTP server.
 * source: https://github.com/CS601-F21/code-examples/blob/main/Web/src/main/java/examples/web/server/HttpConstants.java
 */
public class HTTPConstants {
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String VERSION = "HTTP/1.1";

    public static final String OK = "200 OK";
    public static final String BAD_REQUEST = "400 Bad Request";
    public static final String NOT_FOUND = "404 Not Found";
    public static final String NOT_ALLOWED = "405 Method Not Allowed";

    public static final String CONTENT_LENGTH = "Content-Length:";
    public static final String CONNECTION_CLOSE = "Connection: close";

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
    public static final String FIND_PAGE = """
                    <!DOCTYPE html>
                    <html xmlns="http://www.w3.org/1999/xhtml">
                    <head>
                       <title>Find</title>
                    </head>
                    <body>

                       <form>
                           <label for="asin">asin:</label><br/>
                           <input type="text" id="asin" name="asin"<br/>
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
