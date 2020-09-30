package no.kristiania;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpServerTest {

    @Test
    void shouldReturnSuccessfulStatusCode() throws IOException {
        new HttpServer(10001);
        HttpClient client = new HttpClient("localhost", 10001, "/echo");
        assertEquals(200, client.getStatusCode());
    }

    @Test
    void shouldReturnUnsuccessfulStatusCode() throws IOException {
        new HttpServer(10002);
        HttpClient client = new HttpClient("localhost", 10002, "/echo?status=404");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldReturnContentLength() throws  IOException {
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
    @DisplayName("Return file from disc")
    void shouldReturnFileFromDisk() throws  IOException {
        HttpServer server = new HttpServer(10005);
        File contentRoot = new File("target/");
        server.setContentRoot(contentRoot);

        String fileContent = "Hello World " + new Date();
        Files.writeString(new File(contentRoot, "test.txt").toPath(), fileContent);

        HttpClient client = new HttpClient("localhost", 10005, "/test.txt");
        assertEquals(fileContent, client.getResponseBody());
        assertEquals("text/plain", client.getResponseHeader("Content-Type"));
    }

    @Test
    @DisplayName("Return contentType")
    void shouldReturnCorrectContentType() throws IOException {
        HttpServer server = new HttpServer(10006);
        File contentRoot = new File("target/");
        server.setContentRoot(contentRoot);

        Files.writeString(new File(contentRoot, "index.html").toPath(), "text/html");

        HttpClient client = new HttpClient("localhost", 10006, "/index.html");
        assertEquals("text/html", client.getResponseHeader("Content-Type"));
    }


    @Test
    void shouldReturn404onMissingFile() throws  IOException {
        HttpServer server = new HttpServer(10007);
        File contentRoot = new File("target/");
        server.setContentRoot(contentRoot);

        HttpClient client = new HttpClient("localhost", 10007, "/missingFile");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldPostNewWorker() throws IOException {
        HttpServer server = new HttpServer(10008);
        HttpClient client = new HttpClient("localhost", 10008, "/api/newWorker", "POST", "workerName=Sebastian&emailAddress=Sebastian%40mail");
        assertEquals(200, client.getStatusCode());
        assertEquals(List.of("Sebastian"), server.getWorkerNames());
    }

    @Test
    void shouldReturnExistingWorker() throws IOException {
        HttpServer server = new HttpServer(10009);
        server.getWorkerNames().add("Azad");
        HttpClient client = new HttpClient("localhost", 10009, "/api/workers");
        assertEquals("<ul><li>Azad</li></ul>", client.getResponseBody());
    }

}
