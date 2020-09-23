package no.kristiania;

import java.io.IOException;
import java.net.Socket;

public class HttpClient {

    private int statusCode = 200;

    public HttpClient(String hostName, int port, String requestTarget) throws IOException {
        Socket socket = new Socket(hostName, port);

        String request = "GET " + requestTarget + " HTTP/1.1\r\n" +
                "Host: " + hostName + "\r\n\r\n";
        socket.getOutputStream().write(request.getBytes());

        int c;
        while((c = socket.getInputStream().read()) != -1) {
            if (c != '\n') break;
            System.out.print((char)c);
        }
    }

    public static void main(String[] args) throws IOException {
        new HttpClient("urlecho.appspot.com", 80, "/echo?status=200&body=Hello%20world!");


    }

    public int getStatusCode() {
        return statusCode;
    }
}
