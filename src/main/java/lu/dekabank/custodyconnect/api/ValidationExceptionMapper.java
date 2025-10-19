package lu.dekabank.custodyconnect.api;

import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
  @Override public Response toResponse(ConstraintViolationException ex) {
    var errors = ex.getConstraintViolations().stream()
        .collect(Collectors.toMap(
            v -> pathOf(v),
            ConstraintViolation::getMessage,
            (a,b) -> a));
    return Response.status(Response.Status.BAD_REQUEST)
        .type(MediaType.APPLICATION_JSON)
        .entity(Map.of("error","validation_failed","details",errors))
        .build();
  }
  private String pathOf(ConstraintViolation<?> v) {
    var p = v.getPropertyPath();
    return p == null ? "unknown" : p.toString();
  }
}
