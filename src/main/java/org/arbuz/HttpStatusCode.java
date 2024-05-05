package org.arbuz;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HttpStatusCode {
  OK(200),
  NO_CONTENT(204),
  BAD_REQUEST(400),
  NOT_FOUND(404);

  private final int value;
}
