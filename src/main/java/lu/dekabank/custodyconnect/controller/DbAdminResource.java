package lu.dekabank.custodyconnect.controller;

import java.util.Map;

import org.flywaydb.core.Flyway;

import jakarta.annotation.Resource;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/admin/db")
public class DbAdminResource {

    @Resource(lookup = "java:jboss/datasources/ExampleDS")
    private javax.sql.DataSource dataSource;

    @POST
    @Path("/migrate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response migrate() {
        try {
            Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("db/migration")
                .load();

            flyway.migrate();

            return Response.ok()
                .entity(Map.of(
                    "status", "success",
                    "message", "Database migrations executed successfully"
                ))
                .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of(
                    "status", "error",
                    "message", String.format("Migration failed: %s", e.getMessage())
                ))
                .build();
        }
    }
}
