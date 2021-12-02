package cs601.project4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Server to accept HTTP requests
 * @author Wyatt Mumford
 */
public class HTTPServer {

    private final HashMap<String, Handler> handlers = new HashMap<>();

    private final ExecutorService pool;

    private volatile boolean running = true;
    private final int port;

    private final Logger LOGGER = Logger.getLogger(HTTPServer.class.getName());

    public static Config config;


    /**
     * Multithreaded class to process requests
     * source: https://github.com/CS601-F21/code-examples/blob/main/Web/src/main/java/examples/web/server/FileServer.java
     */
    private class RequestProcessor implements Runnable {
        Socket socket;
        ServerSocket server;

        RequestProcessor(Socket socket, ServerSocket server){
            this.socket = socket;
            this.server = server;
        }

        /**
         * Validates request line
         * @param inStream input from socket
         * @param writer PrintWriter to client
         * @return the request split into its 3 parts, or null if there was a problem with the request
         */
        private String[] validateRequest(BufferedReader inStream, PrintWriter writer){
            String requestLine;
            try {
                requestLine = inStream.readLine();
                LOGGER.info("Request: " + requestLine);
                String[] requestLineParts = requestLine.split(" ");

                //validate request line
                // confirm it contains three substrings
                if (requestLineParts.length != 3){
                    LOGGER.log(Level.SEVERE, "Request line has wrong number of elements.");
                    ServerUtils.send400(writer);
                    return null;
                }
                //only support POST and GET
                if (!requestLineParts[0].equals(ServerConstants.POST) && !requestLineParts[0].equals(ServerConstants.GET)){
                    LOGGER.log(Level.SEVERE, "Only POST and GET supported.");
                    ServerUtils.send405(writer);
                    return null;
                }
                //path not supported
                String basePath = requestLineParts[1].split("\\?")[0];
                if(!handlers.containsKey(basePath)){
                    if (!requestLineParts[1].equals("/shutdown")) {
                        LOGGER.log(Level.SEVERE, "Path not supported.");
                        ServerUtils.send404(writer);
                        return null;
                    }
                }
                //versions don't match
                if (!requestLineParts[2].equals(ServerConstants.VERSION)){
                    LOGGER.log(Level.SEVERE, "Wrong HTTP version.");
                    ServerUtils.send400(writer);
                    return null;
                }

                return requestLineParts;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * verify request and then execute it
         */
        @Override
        public void run() {
            try(
                    BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter writer = new PrintWriter(socket.getOutputStream())
            ) {

                //verify request
                String[] requestLine = validateRequest(inStream, writer);

                if (requestLine != null) {
                    //shutdown server
                    if (requestLine[1].equals("/shutdown")){
                        ServerUtils.send200(writer);
                        writer.println(ServerConstants.SHUTDOWN_PAGE);
                        server.close();

                    //if nothing was wrong in validateRequest, pass to appropriate Handler
                    } else {
                        String basePath = requestLine[1].split("\\?")[0];
                        Handler handler = handlers.get(basePath);
                        handler.handle(inStream, writer, requestLine);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * constructs new server with port to listen to
     * @param port server is listening on
     */
    public HTTPServer(int port, Config config) {
        this.port = port;
        HTTPServer.config = config;
        int poolSize = config.poolSize;
        pool = Executors.newFixedThreadPool(poolSize);
    }

    /**
     * escapes &, <, >, ', " for XHTML
     * @param html - unencoded html
     * @return encoded html
     */
    public static String encodeHtml(String html){
        //replaces instance of <br/> temporarily
        String breakEscape = "BREAK_CHARACTER";
        while (html.contains(breakEscape)){
            breakEscape = breakEscape + "*";
        }
        html = html.replaceAll("<br/>", breakEscape);

        //clean invalid characters
        html = html.replaceAll("[^a-zA-Z0-9!@#$%^&*()_\\-+=<,>.?/:;\"\'\\[{\\]}`~\s\n]", "");

        html = html.replaceAll("&", "&amp;");
        html = html.replaceAll("\"", "&quot;");
        html = html.replaceAll("'", "&#39;");
        html = html.replaceAll("<", "&lt;");
        html = html.replaceAll(">", "&gt;");

        html = html.replaceAll(breakEscape, "<br/>");
        return html;
    }

    void addMapping(String path, Handler handler){
        handlers.put(path, handler);
    }

    /**
     * start listening for new client connections
     */
    public void startup(){

        try (ServerSocket server = new ServerSocket(port)){
            while(running){
                //accept new client connection
                LOGGER.info("Server listening on port " + port);
                try {
                    Socket socket = server.accept();
                    LOGGER.info("New connection from " + socket.getInetAddress());
                    pool.execute(new RequestProcessor(socket, server));
                } catch (SocketException se){
                    LOGGER.info("Shutting down server...");
                    running = false;
                }
            }
            pool.shutdown();
            LOGGER.info("Server Shut down.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
