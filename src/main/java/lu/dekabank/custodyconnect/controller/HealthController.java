package lu.dekabank.custodyconnect.controller;

import java.util.Map;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/health")
public class HealthController {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Map<String, String> health() {
    return Map.of("status", "UP");
  }

  @GET
  @Path("/ping")
  @Produces(MediaType.TEXT_PLAIN)
  public String ping() {
    return "OK";
  }

  @GET
  @Path("/text")
  @Produces(MediaType.TEXT_PLAIN)
  public String textHealth() {
    return "OK";
  }
}
