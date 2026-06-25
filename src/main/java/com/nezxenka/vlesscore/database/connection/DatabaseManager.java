package com.nezxenka.vlesscore.database.connection;

import com.nezxenka.vlesscore.config.AppConfig;
import com.nezxenka.vlesscore.database.dao.TokenDao;
import com.nezxenka.vlesscore.database.dao.TokenDaoImpl;
import com.nezxenka.vlesscore.database.migration.MigrationRunner;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseManager {

    private static final Logger log = LoggerFactory.getLogger(
        DatabaseManager.class
    );

    private final String jdbcUrl;
    private Connection connection;
    private TokenDao tokenDao;

    public DatabaseManager(Path baseDir, AppConfig config) {
        SqliteConfig sqliteConfig = new SqliteConfig(baseDir, config);
        this.jdbcUrl = sqliteConfig.getJdbcUrl();
    }

    public void initialize() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(jdbcUrl);

            try (Statement st = connection.createStatement()) {
                st.execute("PRAGMA journal_mode=WAL");
                st.execute("PRAGMA synchronous=NORMAL");
                st.execute("PRAGMA foreign_keys=ON");
            }

            MigrationRunner.runMigrations(connection);
            tokenDao = new TokenDaoImpl(connection);
            log.info("SQLite connected: {}", jdbcUrl);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public TokenDao getTokenDao() {
        return tokenDao;
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            log.error("Error closing database", e);
        }
    }
}
