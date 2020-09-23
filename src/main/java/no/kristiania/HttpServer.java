package no.kristiania;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    public HttpServer(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);

        new Thread(() -> {
            try {
                Socket socket = serverSocket.accept();
                handleRequest(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void handleRequest(Socket clientSocket) throws IOException {
        String requestLine = HttpClient.readLine(clientSocket);
        System.out.println(requestLine);

        String requestTarget = requestLine.split(" ")[1];
        String statusCode = null;
        String body = null;

        int questionPos = requestTarget.indexOf('?');
        if (questionPos != -1){
            QueryString queryString = new QueryString(requestTarget.substring(questionPos + 1));
            statusCode = queryString.getParameter("status");
            body = queryString.getParameter("body");
        }

        if (statusCode == null) statusCode = "200";
        if (body == null) body = "Hello <strong>World</strong>!";

        writeResponse(clientSocket, statusCode, body);
    }

    private static void writeResponse(Socket clientSocket, String statusCode, String body) throws IOException {
        String response = "HTTP/1.1 " + statusCode + " OK\r\n" +
                "Content-Length:" + body.length() + "\r\n" +
                "Content-Type: text/html; charset=utf-8\r\n" +
                "\r\n" +
                body;

        clientSocket.getOutputStream().write(response.getBytes());
    }

    public static void main(String[] args) throws IOException {
        new HttpServer(8080);
    }

}
