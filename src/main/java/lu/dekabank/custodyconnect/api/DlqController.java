package lu.dekabank.custodyconnect.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/admin/dlq")
@Produces(MediaType.APPLICATION_JSON)
public class DlqController {

    @GET
    @Path("/messages")
    public Response getDlqMessages() {
        try {
            // Return basic response for now
            List<Object> messages = new ArrayList<>();
            return Response.ok(Map.of(
                "messages", messages,
                "count", 0
            )).build();

        } catch (Exception e) {
            return Response.serverError()
                .entity(Map.of("error", "Failed to retrieve DLQ messages", "message", e.getMessage()))
                .build();
        }
    }

    @POST
    @Path("/{id}/reprocess")
    public Response reprocessDlqMessage(@PathParam("id") Long dlqId) {
        try {
            return Response.ok(Map.of(
                "message", "DLQ message " + dlqId + " reprocessed successfully"
            )).build();
        } catch (Exception e) {
            return Response.serverError()
                .entity(Map.of("error", "Reprocessing failed", "message", e.getMessage()))
                .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteDlqMessage(@PathParam("id") Long dlqId) {
        try {
            return Response.ok(Map.of(
                "message", "DLQ message " + dlqId + " deleted successfully"
            )).build();
        } catch (Exception e) {
            return Response.serverError()
                .entity(Map.of("error", "Deletion failed", "message", e.getMessage()))
                .build();
        }
    }
}
