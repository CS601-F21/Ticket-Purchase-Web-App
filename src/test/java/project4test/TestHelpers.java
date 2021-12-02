package project4test;

import com.google.gson.Gson;
import cs601.project4.*;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Helper functions used in Test classes
 * @author Wyatt Mumford
 */
public class TestHelpers {
    /**
     * read config json file and return config file
     * @return Config file
     */
    //TODO: delete
    public Config configSetup(){
        //set up Config
        Gson gson = new Gson();
        Config config = new Config();
        try {
            FileReader reader = new FileReader("Config.json");
            config = gson.fromJson(reader, Config.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return config;
    }

    /**
     * Starts chat server
     * @param config Config file
     * @return thread running server
     */
    public Thread chatSetup(Config config){
        //start server
        int port = config.chatPort;
        HTTPServer server = new HTTPServer(port, config);
        try {
            Method addMappingMethod = HTTPServer.class.getDeclaredMethod("addMapping", String.class, Handler.class);
            addMappingMethod.setAccessible(true);
            addMappingMethod.invoke(server, "/slackbot", new ChatHandler());
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        Thread serverThread = new Thread(server::startup);
        serverThread.start();
        return serverThread;
    }

    /**
     * send /shutdown to server, block until thread terminates
     */
    public void shutdown(int port, Thread thread){
        HttpClient client = HttpClient.newHttpClient();
        String uri = "http://localhost:" + port + "/shutdown";
        HttpRequest request = HttpRequest.newBuilder(URI.create(uri)).build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            thread.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
