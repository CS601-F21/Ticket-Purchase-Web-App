package project3test;

import com.google.gson.Gson;
import cs601.project3.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

/**
 * Helper functions used in Test classes
 * @author Wyatt Mumford
 */
public class TestHelpers {
    /**
     * read config json file and return config file
     * @return Config file
     */
    public Config configSetup(){
        //set up Config
        Gson gson = new Gson();
        Config config = new Config();
        try {
            FileReader reader = new FileReader("Config.json");
            config = gson.fromJson(reader, Config.class);
            BufferedReader tokenReader = new BufferedReader(new FileReader("SlackbotToken.txt"));
            String token = tokenReader.readLine();
            config.setSlackbotToken(token);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return config;
    }

    public Thread searchSetup(Config config){
        //build inverted index
        HashMap<Integer, String> files = new HashMap<>();
        files.put(FileData.REVIEW, config.reviewJson);
        files.put(FileData.QA, config.qaJson);

        //read json files
        JsonParser jsonParser = new JsonParser();
        FileData data = new FileData();
        try {
            System.out.println("Reading first 5000 entries of " + files.get(FileData.REVIEW) + "...");
            jsonParser.readFile(files.get(FileData.REVIEW), data.getReviewHashMap(), FileData.REVIEW);
            System.out.println("Reading first 5000 entries of " + files.get(FileData.QA) + "...");
            jsonParser.readFile(files.get(FileData.QA), data.getQaHashMap(), FileData.QA);
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            System.exit(1);
        }

        //create inverted index
        System.out.println("Creating inverted index...");
        data.getReviewInvertedIndex().populate(data.getReviewHashMap());
        data.getQaInvertedIndex().populate(data.getQaHashMap());

        //start server
        int port = config.searchPort;
        HTTPServer server = new HTTPServer(port, config);
        try {
            Method addMappingMethod = HTTPServer.class.getDeclaredMethod("addMapping", String.class, Handler.class);
            addMappingMethod.setAccessible(true);
            addMappingMethod.invoke(server, "/reviewsearch", new ReviewSearchHandler(data));
            addMappingMethod.invoke(server, "/find", new FindHandler(data));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        Thread serverThread = new Thread(server::startup);
        serverThread.start();
        return serverThread;
    }

    public static void testa(Integer a){
        a = a + 1;
        System.out.println(a);
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
