package cs601.project4.webserver.utilities;

/**
 * A helper class to store various constants used for the HTTP server.
 * source: https://github.com/CS601-F21/code-examples/blob/main/JettyLoginServer/src/main/java/example/login/LoginServerConstants.java
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

    public static final String NAME_KEY = "name";
    public static final String EMAIL_KEY = "email";

    public static final String LOGIN_PATH = "/login";
    public static final String HOME_PATH = "/home";
    public static final String PROFILE_PATH = "/profile";
    public static final String PROFILE_UPDATE_PATH = PROFILE_PATH + "/update";
    public static final String PROFILE_TRANSFER_PATH = PROFILE_PATH + "/transfer";
    public static final String PROFILE_PURCHASE_PATH = PROFILE_PATH + "/purchase";
    public static final String EVENT_PATH = "/event";
    public static final String EVENT_CREATE_PATH = EVENT_PATH + "/create";
    public static final String EVENT_DETAILS_PATH = EVENT_PATH +"/details";
    public static final String EVENT_DELETE_PATH = EVENT_PATH + "/delete";
    public static final String EVENT_MODIFY_PATH = EVENT_PATH + "/modify";
    public static final String LOGOUT_PATH = "/logout";

    public static final int PAGE_SIZE = 10;

    public static final String HOME_PAGE_LINK = "<p><a href='/home'>Return to Home Page</a></p>";
    public static final String PAGE_HEADER = """
        <!DOCTYPE html>
        <html xmlns="http://www.w3.org/1999/xhtml">
        <head>
          <title>Log in with Slack</title>
        </head>
        <body>
        <h1>Ticket-Get-It</h1>
        """;

    public static final String PAGE_FOOTER = """

        </body>
        </html>""";

    public static final String EVENT_FORM = """
        <form action="/event/create">
          <label for="name">Event name:</label><br/>
          <input type="text" id="name" name="name" maxlength="255" required="true">
          <br/>
          <label for="description">Description:</label><br/>
          <textarea rows = "5" cols = "60" name = "description" maxlength="512" required="true">
          </textarea>
          <br/>
          <label for="datetime">Date and time:</label><br/>
          <input type="datetime-local" id="datetime" name="datetime" min='2021-01-01T00:00' required="true">
          <br/>
          <label for="base_price">Base Price:</label><br/>
          <input type="text" id="base_price" name="base_price"
            pattern="[0-9]{1,4}([.][0-9]{2})?" title="Price from 0.00-9999.99" required="true">
            <br/>
          <label for="student_price">Student Price (optional):</label><br/>
          <input type="text" id="student_price" name="student_price"
            pattern="[0-9]{1,4}([.][0-9]{2})?" title="Price from 0.00-9999.99">
            <br/>
          <label for="vip_price">VIP Price (optional):</label><br/>
          <input type="text" id="vip_price" name="vip_price"
            pattern="[0-9]{1,4}([.][0-9]{2})?" title="Price from 0.00-9999.99">
            <br/>
          <input type="submit" value="Submit">
        </form>
        """;

}
