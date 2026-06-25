package com.nezxenka.vlesscore.database.connection;

import com.nezxenka.vlesscore.config.AppConfig;
import java.nio.file.Path;

public class SqliteConfig {

    private final String jdbcUrl;

    public SqliteConfig(Path baseDir, AppConfig config) {
        Path dbPath = baseDir.resolve(config.getDatabaseFile());
        this.jdbcUrl = "jdbc:sqlite:" + dbPath.toAbsolutePath();
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }
}
