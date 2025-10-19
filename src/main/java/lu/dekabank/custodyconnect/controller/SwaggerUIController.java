package lu.dekabank.custodyconnect.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/swagger")
public class SwaggerUIController {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response swaggerUI() {
        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Custody Connect API Documentation</title>
                    <link rel="stylesheet" type="text/css" href="https://unpkg.com/swagger-ui-dist@4.18.3/swagger-ui.css" />
                </head>
                <body>
                    <div id="swagger-ui"></div>
                    <script src="https://unpkg.com/swagger-ui-dist@4.18.3/swagger-ui-bundle.js"></script>
                    <script src="https://unpkg.com/swagger-ui-dist@4.18.3/swagger-ui-standalone-preset.js"></script>
                    <script>
                        window.onload = function() {
                            const ui = SwaggerUIBundle({
                                url: '/api/v1/openapi',
                                dom_id: '#swagger-ui',
                                deepLinking: true,
                                presets: [
                                    SwaggerUIBundle.presets.apis,
                                    SwaggerUIStandalonePreset
                                ],
                                plugins: [
                                    SwaggerUIBundle.plugins.DownloadUrl
                                ],
                                layout: "StandaloneLayout"
                            });
                        };
                    </script>
                </body>
                </html>
                """;
        return Response.ok(html).build();
    }
}
