package cs601.project3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * handles /slackbot request. Posts client's message to slackbot channel
 * @author Wyatt Mumford
 */
public class ChatHandler implements Handler {

    /**
     * Handles /slackbot request
     * @param reader input stream from client
     * @param writer output to client
     * @param request request line
     */
    @Override
    public void handle(BufferedReader reader, PrintWriter writer, String[] request) {
        String method = request[0];
        String path = request[1];

        ArrayList<String> headers = new ArrayList<>();
        int contentLength = setHeaders(reader, headers);

        String message = null;

        if (method.equals(HTTPConstants.GET)){
            //empty GET request
            if (path.equals("/slackbot")) {
                ServerUtils.send200(writer);
                writer.println(HTTPConstants.SLACKBOT_PAGE);
                //GET request with query in URL
            } else {
                if (!path.startsWith("/slackbot?message=") || path.contains("&")){
                    ServerUtils.send400(writer);
                } else {
                    ServerUtils.send200(writer);
                    message = path.split("\\?message=")[1];
                }
            }
        //POST request
        } else {
            //read body
            char[] bodyArr = new char[contentLength];
            try {
                reader.read(bodyArr, 0, bodyArr.length);

            } catch (IOException e) {
                e.printStackTrace();
            }
            String body = new String(bodyArr);
            LOGGER.info("Message body: " + body);
            if (!body.split("=")[0].equals("message")){
                LOGGER.log(Level.SEVERE, "Message body incorrect.");
                ServerUtils.send400(writer);
            } else {
                ServerUtils.send200(writer);
                message = body.split("=")[1];
            }
        }
        LOGGER.info("Message: " + message);
        if (message != null){
            try {
                message = URLDecoder.decode(message, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            //create a client
            HttpClient client = HttpClient.newHttpClient();

            String authToken = "Bearer " + HTTPServer.config.slackbotToken;
            String body = "{\"channel\":\"" + HTTPServer.config.channelId + "\",\"text\":\"" + message + "\"}";
            LOGGER.info("Body: " + body);
            HttpRequest slackRequest = HttpRequest.newBuilder(URI.create("https://slack.com/api/chat.postMessage"))
                .header("Content-type", "application/json")
                .header("Authorization", authToken)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

            //send request to slackbot
            try {
                HttpResponse<String> response = client.send(slackRequest, HttpResponse.BodyHandlers.ofString());
                LOGGER.info("Response: " + response);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            writer.println(HTTPConstants.SLACKBOT_PAGE);
        }
    }
}
