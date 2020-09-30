package no.kristiania;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpServerTest {

    @Test
    void shouldReturnSuccessfulErrorCode() throws IOException {
        new HttpServer(10001);
        HttpClient client = new HttpClient("localhost", 10001, "/echo");
        assertEquals(200, client.getStatusCode());
    }

    @Test
    void shouldReturnUnsuccessfulErrorCode() throws IOException {
        new HttpServer(10002);
        HttpClient client = new HttpClient("localhost", 10002, "/echo?status=404");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldReturnHttpHeaders() throws  IOException {
        new HttpServer(10003);
        HttpClient client = new HttpClient("localhost", 10003, "/echo?body=HelloWorld");
        assertEquals("10", client.getResponseHeader("Content-Length"));
    }

    @Test
    void shouldReturnResponseBody() throws  IOException {
        new HttpServer(10004);
        HttpClient client = new HttpClient("localhost", 10004, "/echo?body=HelloWorld");
        assertEquals("HelloWorld", client.getResponseBody());
    }

    @Test
    void shouldReturnFileContent() throws  IOException {
        HttpServer server = new HttpServer(10005);
        File contentRoot = new File("target");
        server.setContentRoot(contentRoot);
        String fileContent = "Hello " + new Date();
        Files.writeString(new File(contentRoot, "index.html").toPath(), fileContent);
        HttpClient client = new HttpClient("localhost", 10005, "/index.html");
        assertEquals(fileContent, client.getResponseHeader("Content-Type"));
    }

    @Test
    void shouldReturn404onMissingFile() throws  IOException {
        HttpServer server = new HttpServer(10006);
        server.setContentRoot(new File("target"));
        HttpClient client = new HttpClient("localhost", 10006, "/missingFile");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldReturnCorrectContentType() throws IOException {
        HttpServer server = new HttpServer(10007);
        File contentRoot = new File("target/");
        server.setContentRoot(contentRoot);
        Files.writeString(new File(contentRoot, "index.html").toPath(), "text/html");
        HttpClient client = new HttpClient("localhost", 10007, "/index.html");
        assertEquals("text/html", client.getResponseHeader("Content-Type"));
    }






}
