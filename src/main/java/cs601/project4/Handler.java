package cs601.project4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * interface for Handler class. Each Handler processes an endpoint request
 * @author Wyatt Mumford
 */
public interface Handler {

    Logger LOGGER = Logger.getLogger(HTTPServer.class.getName());

    /**
     * Reads headers, saving them into headers array
     * @param reader stream from client
     * @param headers are added to headers array
     * @return contentLength
     */
    default int setHeaders(BufferedReader reader, ArrayList<String> headers){
        int contentLength = 0;
        String header;
        boolean linesLeft = true;
        while (linesLeft){
            try {
                header = reader.readLine();
                LOGGER.info("Header: " + header);
                if (header.isBlank()) {
                    LOGGER.info("End of headers.");
                    linesLeft = false;
                } else {
                    headers.add(header);
                    if (header.startsWith(HTTPConstants.CONTENT_LENGTH)){
                        contentLength = Integer.parseInt(header.split(" ")[1]);
                    }
                }

            } catch (IOException e) {
                linesLeft = false;
                e.printStackTrace();
            }
        }
        return contentLength;
    }

    /**
     * @param reader input stream from client
     * @param writer output to client
     * @param request request line
     */
    void handle(BufferedReader reader, PrintWriter writer, String[] request);
}
