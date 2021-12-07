package project4test;

import com.google.gson.Gson;
import cs601.project4.webserver.utilities.Config;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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
 /**       try {
            Method addMappingMethod = HTTPServer.class.getDeclaredMethod("encodeHtml", String.class);
            addMappingMethod.setAccessible(true);
            encodedBody = (String) addMappingMethod.invoke(null, body);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        } */
        assertEquals(correctBody, encodedBody);

    }

    /**
     * Tests server with an invalid endpoint
     */
    @Test
    void test404(){
        //server setup

        //set up Config
        Gson gson = new Gson();
        Config config = new Config();
        try {
            FileReader reader = new FileReader("Config.json");
            config = gson.fromJson(reader, Config.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

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

        //set up Config
        Gson gson = new Gson();
        Config config = new Config();
        try {
            FileReader reader = new FileReader("Config.json");
            config = gson.fromJson(reader, Config.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

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
