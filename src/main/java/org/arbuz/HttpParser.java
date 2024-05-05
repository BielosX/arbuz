package org.arbuz;

import static org.arbuz.HeaderKeys.CONTENT_LENGTH;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class HttpParser {
  protected final HttpRequest request;

  protected abstract HttpParser parse(BufferedReader reader) throws IOException;

  public static HttpRequest parseRequest(BufferedReader reader) throws IOException {
    HttpParser parser = new RequestLine(new HttpRequest());
    while (!parser.isFinished()) {
      parser = parser.parse(reader);
    }
    return parser.request;
  }

  public boolean isFinished() {
    return false;
  }

  private static class RequestLine extends HttpParser {
    private static final Pattern PATTERN =
        Pattern.compile(
            "^(GET|HEAD|POST|PUT|DELETE|CONNECT|OPTIONS|TRACE|PATCH) (/[/\\w.]*) HTTP/([\\d.]+)$");

    private RequestLine(HttpRequest request) {
      super(request);
    }

    @Override
    protected HttpParser parse(BufferedReader reader) throws IOException {
      String line = reader.readLine();
      Matcher matcher = PATTERN.matcher(line);
      if (matcher.matches()) {
        this.request.setMethod(HttpMethod.valueOf(matcher.group(1)));
        this.request.setPath(matcher.group(2));
        this.request.setVersion(matcher.group(3));
      }
      return new HeadersParser(this.request);
    }
  }

  private static class HeadersParser extends HttpParser {
    private static final Pattern PATTERN = Pattern.compile("^([\\w-]+): *(\\S+)$");

    public HeadersParser(HttpRequest request) {
      super(request);
    }

    @Override
    protected HttpParser parse(BufferedReader reader) throws IOException {
      String line = reader.readLine();
      Matcher matcher = PATTERN.matcher(line);
      if (matcher.matches()) {
        this.request.getHeaders().addHeader(matcher.group(1), matcher.group(2));
      } else if (line.isEmpty()) {
        return new BodyParser(this.request);
      }
      return this;
    }
  }

  private static class BodyParser extends HttpParser {

    public BodyParser(HttpRequest request) {
      super(request);
    }

    @Override
    protected HttpParser parse(BufferedReader reader) throws IOException {
      Integer contentLength =
          this.request
              .getHeaders()
              .getHeader(CONTENT_LENGTH.getValue())
              .map(Integer::parseInt)
              .orElse(null);
      if (contentLength == null) {
        return new Finished(this.request);
      }
      char[] body = new char[contentLength];
      int result = reader.read(body, 0, contentLength);
      if (result != -1) {
        this.request.setBody(new String(body));
        return new Finished(this.request);
      }
      return this;
    }
  }

  private static class Finished extends HttpParser {

    protected Finished(HttpRequest request) {
      super(request);
    }

    @Override
    protected HttpParser parse(BufferedReader reader) {
      return this;
    }

    @Override
    public boolean isFinished() {
      return true;
    }
  }
}
