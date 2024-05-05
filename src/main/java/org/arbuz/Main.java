package org.arbuz;

import com.sun.net.httpserver.Headers;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
  public static void main(String[] args) throws IOException {
    AtomicBoolean shouldExit = new AtomicBoolean(false);
    Runtime.getRuntime().addShutdownHook(new Thread(() -> shouldExit.set(true)));
    try (ServerSocket socket = new ServerSocket(8080)) {
      log.info("Server started");
      while (!shouldExit.get()) {
        Socket connection = socket.accept();
        connection.setSoTimeout(10000);
        log.info("Connection from {}", connection.getRemoteSocketAddress());
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(connection.getInputStream()));
        HttpRequest request = HttpParser.parseRequest(reader);
        log.info("Request: {}", request);
        BufferedOutputStream outputStream = new BufferedOutputStream(connection.getOutputStream());
        String responseBody = "<html><body><h1>Hello World!</h1></body></html>";
        Headers responseHeaders = new Headers();
        responseHeaders.set("Content-Type", "text/html");
        responseHeaders.set("Connection", "close");
        responseHeaders.set("Content-Length", String.valueOf(responseBody.getBytes().length));
        HttpResponse response = new HttpResponse("1.1", 200, "OK", responseHeaders, responseBody);
        response.send(outputStream);
        connection.close();
      }
    }
  }
}
