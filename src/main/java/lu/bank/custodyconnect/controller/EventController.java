package lu.bank.custodyconnect.controller;

import java.util.Map;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lu.bank.custodyconnect.model.Event;
import lu.bank.custodyconnect.model.dto.EventDto;
import lu.bank.custodyconnect.service.EventQueryService;
import lu.bank.custodyconnect.service.ReconciliationService;

@Path("/events")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EventController {

  @Inject ReconciliationService reconciliationService;
  @Inject EventQueryService eventQueryService;
  @Inject lu.bank.custodyconnect.dev.DevDataStore dev;

  @POST
  public Response createEvent(@Valid EventDto dto) {
    try {
      Event event = reconciliationService.ingest(dto);
      return Response.accepted()
          .entity(Map.of("id", event.getId(), "message", "Event accepted for processing"))
          .build();
    } catch (Exception e) {
      return Response.serverError()
          .entity(Map.of("error", "Failed to process event", "message", e.getMessage()))
          .build();
    }
  }

  @GET
  public Response getEvents(@QueryParam("page") @DefaultValue("0") int page,
                            @QueryParam("size") @DefaultValue("10") int size,
                            @QueryParam("q") String query,
                            @QueryParam("status") String status) {
    // ignore q for now; return mock page
    return Response.ok(dev.pageEvents(page, size, status)).build();
  }

  @GET
  @Path("/count")
  public Response getEventCount(@QueryParam("status") String status) {
    return Response.ok(Map.of("total", dev.count(status))).build();
  }
}
