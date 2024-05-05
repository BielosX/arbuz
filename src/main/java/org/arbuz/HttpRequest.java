package org.arbuz;

import lombok.Data;

@Data
public class HttpRequest {
  private HttpMethod method;
  private String path;
  private final HttpHeaders headers = new HttpHeaders();
  private String body;
  private String version;
}
