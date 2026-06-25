package com.nezxenka.vlesscore.database.migration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

final class SchemaVersion {

    private SchemaVersion() {}

    static void ensureTable(Connection connection) throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute(
                """
                    CREATE TABLE IF NOT EXISTS schema_version (
                        version INTEGER NOT NULL
                    )
                """
            );
        }
    }

    static int getCurrentVersion(Connection connection) throws SQLException {
        try (Statement st = connection.createStatement()) {
            var rs = st.executeQuery(
                "SELECT version FROM schema_version ORDER BY version DESC LIMIT 1"
            );
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
}
