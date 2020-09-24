package no.kristiania;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    private static File documentRoot;

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
        } else if (!requestTarget.equals("/echo")){
            File targetFile = new File(documentRoot, requestTarget);

            if (!targetFile.exists()) {
                writeResponse(clientSocket, "404", requestTarget + " not found");
                return;
            }

            HttpMessage responseMessage = new HttpMessage("HTTP/1.1 200 OK");
            responseMessage.setHeader("Content-Length", String.valueOf(targetFile.length()));
            responseMessage.setHeader("Content-Type", "text/html");

            if (targetFile.getName().endsWith(".txt")) {
                responseMessage.setHeader("Content-Type", "text/plain");
            }
            responseMessage.write(clientSocket);

            try (FileInputStream inputStream = new FileInputStream(targetFile)) {
                inputStream.transferTo(clientSocket.getOutputStream());
            }
        }

        if (statusCode == null) statusCode = "200";
        if (body == null) body = "Hello <strong>World</strong>!";

        writeResponse(clientSocket, statusCode, body);
    }

    private static void writeResponse(Socket clientSocket, String statusCode, String body) throws IOException {
        HttpMessage responseMessage = new HttpMessage("HTTP/1.1 " + statusCode + " OK");
        responseMessage.setHeader("Content-Length", String.valueOf(body.length()));
        responseMessage.setHeader("Content-Type", "text/plain");
        responseMessage.write(clientSocket);
        clientSocket.getOutputStream().write(body.getBytes());
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = new HttpServer(8080);
        server.setDocumentRoot(new File("src/main/resources"));
    }

    public void setDocumentRoot(File documentRoot) {
        this.documentRoot = documentRoot;
    }
}

