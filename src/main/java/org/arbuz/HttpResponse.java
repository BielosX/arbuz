package org.arbuz;

import com.sun.net.httpserver.Headers;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public record HttpResponse(
    String version, int statusCode, String reason, Headers headers, String body) {

  public void send(OutputStream outputStream) throws IOException {
    String status = String.format("HTTP/%s %d %s\r\n", version, statusCode, reason);
    outputStream.write(status.getBytes());
    for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
      for (String headerValue : entry.getValue()) {
        String headerLine = String.format("%s: %s\r\n", entry.getKey(), headerValue);
        outputStream.write(headerLine.getBytes());
      }
    }
    outputStream.write("\r\n".getBytes());
    outputStream.write(body.getBytes());
    outputStream.flush();
  }
}
