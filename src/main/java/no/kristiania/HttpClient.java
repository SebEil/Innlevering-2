package no.kristiania;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpClient {

    private int statusCode;
    private Map<String, String> responseHeaders = new HashMap<>();
    private String responseBody;
    private HttpMessage responseMessage;

    public HttpClient(String hostName, int port, String requestTarget) throws IOException {
        Socket socket = new Socket(hostName, port);

        HttpMessage requestMessage = new HttpMessage("GET " + requestTarget + " HTTP/1.1");
        requestMessage.setHeader("Host", hostName);
        requestMessage.write(socket);

        String responseLine = HttpMessage.readLine(socket);
        String[] responseLineParts = responseLine.split(" ");
        responseMessage = new HttpMessage(responseLine);

        statusCode = Integer.parseInt(responseLineParts[1]);

        String headerLine;
        while (!(headerLine = HttpMessage.readLine(socket)).isEmpty()) {
            int colonPos = headerLine.indexOf(':');

            String headerName = headerLine.substring(0, colonPos);
            String headerValue = headerLine.substring(colonPos+1).trim();

            responseMessage.setHeader(headerName, headerValue);
            responseHeaders.put(headerName, headerValue);
        }


        int contentLength = Integer.parseInt(getResponseHeader("Content-Length"));
        StringBuilder body = new StringBuilder();
        for (int i = 0 ; i < contentLength; i++) {
            body.append((char)socket.getInputStream().read());
        }
        responseBody = body.toString();
    }

    public static void main(String[] args) throws IOException {
        HttpClient client = new HttpClient("urlecho.appspot.com", 80, "/echo?status=200&body=Hello%20world!");
        System.out.println(client.getResponseBody());

    }

    public int getStatusCode() {
        String[] responseLineParts = responseMessage.getStartLine().split(" ");
        int statusCode = Integer.parseInt(responseLineParts[1]);
        return statusCode;
    }

    public String getResponseHeader(String headerName) {
        return responseHeaders.get(headerName);
    }

    public String getResponseBody() {
        return responseBody;
    }
}
