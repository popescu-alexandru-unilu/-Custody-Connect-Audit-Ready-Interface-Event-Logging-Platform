package lu.bank.custodyconnect.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lu.bank.custodyconnect.service.AuditService;

@Path("/audit")
@Produces(MediaType.APPLICATION_JSON)
public class AuditController {

  @Inject AuditService auditService;
  @Inject lu.bank.custodyconnect.dev.DevDataStore dev;

  @GET
  public Response getAuditLogs(@QueryParam("max") @DefaultValue("10") int max) {
    return Response.ok(dev.latestAudits(max)).build();
  }
}
