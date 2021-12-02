package project4test;

import cs601.project4.Config;
import cs601.project4.HTTPServer;
import cs601.project4.Handler;
import cs601.project4.ReviewSearchHandler;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test cases for HTTPServer
 * @author Wyatt Mumford
 */
public class ServerTest {

    TestHelpers helpers = new TestHelpers();

    /**
     * Tests HTTPServer.encodeHtml method
     */
    @Test
    void testEncodeHtml(){
        String body = """
        Example body text.
        Special characters:
        !@#$%^&*()<>'\"""";
        String correctBody = """
        Example body text.
        Special characters:
        !@#$%^&amp;*()&lt;&gt;&#39;&quot;""";
        String encodedBody = null;
        //call encodeHtml()
        try {
            Method addMappingMethod = HTTPServer.class.getDeclaredMethod("encodeHtml", String.class);
            addMappingMethod.setAccessible(true);
            encodedBody = (String) addMappingMethod.invoke(null, body);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        assertEquals(correctBody, encodedBody);

    }

    /**
     * Tests that HTTPServer.addMapping() properly adds handler to handlers array
     */
    @Test
    void testAddMapping() {
        //server setup
        Config config = new Config();
        int port = config.port;
        HTTPServer server = new HTTPServer(port, config);
        ReviewSearchHandler reviewSearchHandler = new ReviewSearchHandler();
        //call AddMapping()
        try {
            Method addMappingMethod = HTTPServer.class.getDeclaredMethod("addMapping", String.class, Handler.class);
            addMappingMethod.setAccessible(true);
            addMappingMethod.invoke(server, "/path",reviewSearchHandler);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
        //check handlers hashmap for reviewSearchHandler
        //source: http://tutorials.jenkov.com/java-reflection/private-fields-and-methods.html
        HashMap<String, Handler> handlers = null;
        try {
            Field privateHashmapField = HTTPServer.class.getDeclaredField("handlers");
            privateHashmapField.setAccessible(true);
            handlers = (HashMap<String, Handler>) privateHashmapField.get(server);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        assertEquals(handlers.get("/path"), reviewSearchHandler);
    }
        /**
     * Tests that HTTPServer.startup() properly listens for connections on port
     */
    @Test
    void testStartup(){
        //server setup
        Config config = new Config();
        int port = config.port;
        HTTPServer server = new HTTPServer(port, config);
        //call startup()
        Thread serverThread = new Thread(server::startup);
        serverThread.start();
        
        //send request to /shutdown
        HttpClient client = HttpClient.newHttpClient();
        String uri = "http://localhost:" + port + "/shutdown";
        HttpRequest request = HttpRequest.newBuilder(URI.create(uri))
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            serverThread.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(200, response.statusCode());
    }

    /**
     * Tests server with an invalid endpoint
     */
    @Test
    void test404(){
        //server setup
        Config config = new Config();
        int port = config.port;
        Thread serverThread = helpers.serverSetup(config);

        //send request to /badpage
        HttpClient client = HttpClient.newHttpClient();
        String uri = "http://localhost:" + port + "/badpage";
        HttpRequest request = HttpRequest.newBuilder(URI.create(uri))
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(404, response.statusCode());

        //shutdown
        helpers.shutdown(port, serverThread);
    }

    /**
     * Tests server with an unsupported request
     */
    @Test
    void test405(){
        //server setup
        Config config = new Config();
        int port = config.port;
        Thread serverThread = helpers.serverSetup(config);

        //send DELETE request
        HttpClient client = HttpClient.newHttpClient();
        String uri = "http://localhost:" + port + "/slackbot";
        HttpRequest request = HttpRequest.newBuilder(URI.create(uri))
                .DELETE()
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(405, response.statusCode());

        //shutdown
        helpers.shutdown(port, serverThread);
    }

}
