package lu.dekabank.custodyconnect;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.flywaydb.core.Flyway;

@Singleton
@Startup
public class DatabaseMigrationService {

    @PersistenceContext(unitName = "custody-connect")
    private EntityManager em;

    @Resource(lookup = "java:jboss/datasources/ExampleDS")
    private javax.sql.DataSource dataSource;

    @PostConstruct
    public void migrate() {
        // For H2 in-memory, use the configured datasource
        // For production, Flyway will use the datasource configured in persistence.xml
        try {
            Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("db/migration")
                .load();
            flyway.migrate();
            System.out.println("Database migration completed successfully.");
        } catch (Exception e) {
            System.err.println("Database migration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
