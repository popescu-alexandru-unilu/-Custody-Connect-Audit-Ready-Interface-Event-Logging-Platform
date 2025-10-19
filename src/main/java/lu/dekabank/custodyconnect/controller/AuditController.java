package lu.dekabank.custodyconnect.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lu.dekabank.custodyconnect.model.AuditLog;

@Path("/audit")
@Produces(MediaType.APPLICATION_JSON)
public class AuditController {

  @GET
  public Response getAuditLogs(@QueryParam("max") @DefaultValue("10") int max) {
    try {
      // Return basic response for now - simplify to avoid service dependencies
      List<AuditLog> auditLogs = new ArrayList<>();
      return Response.ok(auditLogs).build();

    } catch (Exception e) {
      return Response.serverError()
          .entity(Map.of("error", "Failed to retrieve audit logs", "message", e.getMessage()))
          .build();
    }
  }

  @GET
  @Path("/count")
  public Response getAuditLogCount() {
    try {
      long total = 0;
      return Response.ok(Map.of("total", total)).build();

    } catch (Exception e) {
      return Response.serverError()
          .entity(Map.of("error", "Failed to count audit logs", "message", e.getMessage()))
          .build();
    }
  }
}
