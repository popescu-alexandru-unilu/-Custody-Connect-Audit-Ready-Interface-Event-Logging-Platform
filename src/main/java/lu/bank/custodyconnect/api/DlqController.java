package lu.bank.custodyconnect.api;

import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lu.bank.custodyconnect.model.DlqEvent;
import lu.bank.custodyconnect.service.DlqService;
import lu.bank.custodyconnect.service.OutboxService;

@Path("/admin/dlq")
@Produces(MediaType.APPLICATION_JSON)
public class DlqController {

    @Inject DlqService dlqService;
    @Inject OutboxService outboxService;

    @GET
    @Path("/messages")
    public Response getDlqMessages() {
        try {
            List<DlqEvent> messages = dlqService.getAllDlqMessages();
            return Response.ok(Map.of(
                "messages", messages,
                "count", messages.size()
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
            boolean success = dlqService.reprocessDlqMessage(dlqId, outboxService);
            if (success) {
                return Response.ok(Map.of(
                    "message", "DLQ message " + dlqId + " reprocessed successfully"
                )).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Failed to reprocess DLQ message " + dlqId))
                    .build();
            }
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
            boolean success = dlqService.deleteDlqMessage(dlqId);
            if (success) {
                return Response.ok(Map.of(
                    "message", "DLQ message " + dlqId + " deleted successfully"
                )).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "DLQ message " + dlqId + " not found"))
                    .build();
            }
        } catch (Exception e) {
            return Response.serverError()
                .entity(Map.of("error", "Deletion failed", "message", e.getMessage()))
                .build();
        }
    }
}
