package generatik.backend.routers;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {
  // i wrote this class to display error messages too
  // because in Spring boot 2.3+ it doesn't display even when forcing with
  // server.error.include-message=always
  // server.error.include-binding-errors=always
  // such example of why I did it :
  // below are displayed the responses when i try to catch an error
  /*
   * that's it previously
   * {
   * "path": "",
   * "error": "Bad Request",
   * "message": "Advertiser name or email already exists",
   * "timestamp": "2025-11-28T23:31:19.662384699Z",
   * "status": 400
   * }
   */

  /*
   * that's it now
   * {
   * "path": "",
   * "error": "Bad Request",
   * "message": "Advertiser name or email already exists",
   * "timestamp": "2025-11-28T23:31:19.662384699Z",
   * "status": 400
   * }
   */

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {

    int statusCode = ex.getStatusCode().value();
    HttpStatus httpStatus = HttpStatus.resolve(statusCode);

    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", Instant.now().toString());
    body.put("status", statusCode);
    body.put("error", httpStatus != null ? httpStatus.getReasonPhrase() : "Unknown");
    body.put("message", ex.getReason());
    body.put("path", "");

    return ResponseEntity.status(statusCode).body(body);
  }
}
