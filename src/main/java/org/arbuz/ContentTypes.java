package org.arbuz;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContentTypes {
  TEXT_HTML("text/html"),
  APPLICATION_JSON("application/json");

  private final String value;
}
