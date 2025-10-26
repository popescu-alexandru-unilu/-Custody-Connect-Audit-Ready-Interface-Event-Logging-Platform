package lu.bank.custodyconnect.controller;

import java.util.Map;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lu.bank.custodyconnect.service.RetentionService;

@Path("/admin/retention")
@Produces(MediaType.APPLICATION_JSON)
public class RetentionController {

    @Inject RetentionService retentionService;

    @POST
    @Path("/run")
    public Response runRetentionNow() {
        try {
            var result = retentionService.runRetentionNow();

            return Response.ok(Map.of(
                "eventsDeleted", result.eventsDeleted(),
                "auditLogsDeleted", result.auditLogsDeleted(),
                "reconResultsDeleted", result.reconResultsDeleted(),
                "cutoffDate", result.cutoffDate().toString(),
                "message", "Retention cleanup completed successfully"
            )).build();

        } catch (Exception e) {
            return Response.serverError()
                .entity(Map.of(
                    "error", "Retention job failed",
                    "message", e.getMessage()
                ))
                .build();
        }
    }
}
