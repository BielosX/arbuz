package org.arbuz;

import java.util.*;

public class HttpHeaders {
  private final Map<String, List<String>> headers;

  public HttpHeaders() {
    this.headers = new HashMap<>();
  }

  public void addHeader(String name, String headerValue) {
    headers.computeIfAbsent(name.toLowerCase(), (key) -> new ArrayList<>());
    headers.computeIfPresent(
        name.toLowerCase(),
        (key, value) -> {
          value.addLast(headerValue);
          return value;
        });
  }

  public Optional<String> getHeader(String name) {
    return Optional.ofNullable(this.headers.get(name.toLowerCase())).map(List::getFirst);
  }

  public List<String> getHeaderValues(String name) {
    return Optional.ofNullable(this.headers.get(name.toLowerCase()))
        .orElse(Collections.emptyList());
  }
}
