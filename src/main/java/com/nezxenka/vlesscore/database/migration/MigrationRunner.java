package com.nezxenka.vlesscore.database.migration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MigrationRunner {

    private static final Logger log = LoggerFactory.getLogger(
        MigrationRunner.class
    );

    private MigrationRunner() {}

    public static void runMigrations(Connection connection) {
        try {
            SchemaVersion.ensureTable(connection);

            int currentVersion = SchemaVersion.getCurrentVersion(connection);

            if (currentVersion < 1) {
                log.info("Migration v1: creating tables...");
                MigrationV1.apply(connection);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Migration failed", e);
        }
    }
}
