package com.nezxenka.vlesscore.database.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MigrationV1 {

    private static final Logger log = LoggerFactory.getLogger(
        MigrationV1.class
    );

    private MigrationV1() {}

    public static void apply(Connection connection) throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute(
                """
                    CREATE TABLE IF NOT EXISTS tokens (
                        id          INTEGER PRIMARY KEY AUTOINCREMENT,
                        token       TEXT    NOT NULL UNIQUE,
                        status      TEXT    NOT NULL DEFAULT 'ACTIVE',
                        created_at  INTEGER NOT NULL,
                        expires_at  INTEGER NOT NULL,
                        bytes_up    INTEGER NOT NULL DEFAULT 0,
                        bytes_down  INTEGER NOT NULL DEFAULT 0,
                        connections INTEGER NOT NULL DEFAULT 0
                    )
                """
            );

            st.execute(
                "CREATE INDEX IF NOT EXISTS idx_tokens_token ON tokens(token)"
            );
            st.execute(
                "CREATE INDEX IF NOT EXISTS idx_tokens_status ON tokens(status)"
            );

            st.execute(
                """
                    CREATE TABLE IF NOT EXISTS token_uuid_map (
                        uuid       TEXT PRIMARY KEY,
                        token_ref  TEXT NOT NULL,
                        FOREIGN KEY (token_ref) REFERENCES tokens(token) ON DELETE CASCADE
                    )
                """
            );

            st.execute(
                """
                    CREATE TABLE IF NOT EXISTS user_groups (
                        id              INTEGER PRIMARY KEY AUTOINCREMENT,
                        name            TEXT    NOT NULL UNIQUE,
                        max_speed       INTEGER NOT NULL DEFAULT 0,
                        max_connections INTEGER NOT NULL DEFAULT 0
                    )
                """
            );

            st.execute(
                """
                    CREATE TABLE IF NOT EXISTS settings (
                        key   TEXT PRIMARY KEY,
                        value TEXT NOT NULL
                    )
                """
            );

            st.execute(
                "INSERT OR REPLACE INTO schema_version (version) VALUES (1)"
            );
            log.info("Migration v1 completed.");
        }
    }
}
