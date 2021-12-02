package cs601.project4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * handles /reviewsearch request. Allows for searching of inverted index
 * @author Wyatt Mumford
 */
public class ReviewSearchHandler implements Handler{

    /**
     * handles /reviewsearch request
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

        String query = null;

        if (method.equals(ServerConstants.GET)){
            //empty GET request
             if (path.equals("/reviewsearch")) {
                ServerUtils.send200(writer);
                writer.println(ServerConstants.REVIEW_SEARCH_PAGE);
            //GET request with query in URL
            } else {
                 if (!path.startsWith("/reviewsearch?query=") || path.contains("&")){
                     ServerUtils.send400(writer);
                 } else {
                     ServerUtils.send200(writer);
                     query = path.split("\\?query=")[1];
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
            if (!body.split("=")[0].equals("query")){
                LOGGER.log(Level.SEVERE, "Message body incorrect.");
                ServerUtils.send400(writer);
            } else {
                ServerUtils.send200(writer);
                query = body.split("=")[1];
            }
        }
        LOGGER.info("Query: " + query);
        //display search results
        if (query != null){
            System.out.println("Results");
        }
    }
}
