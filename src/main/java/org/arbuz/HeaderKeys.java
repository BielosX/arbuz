package org.arbuz;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HeaderKeys {
  CONTENT_LENGTH("Content-Length"),
  CONTENT_TYPE("Content-Type");

  private final String value;
}
