package com.nezxenka.vlesscore.monitoring;

import com.nezxenka.vlesscore.config.AppConfig;
import com.nezxenka.vlesscore.database.connection.DatabaseManager;

public class HealthReporter {

    private final AppConfig config;
    private final DatabaseManager dbManager;

    public HealthReporter(AppConfig config, DatabaseManager dbManager) {
        this.config = config;
        this.dbManager = dbManager;
    }

    public boolean isDatabaseHealthy() {
        try {
            return (
                dbManager.getConnection() != null &&
                !dbManager.getConnection().isClosed()
            );
        } catch (Exception e) {
            return false;
        }
    }

    public String getServerName() {
        return config.getServerName();
    }

    public int getPort() {
        return config.getPort();
    }

    public boolean isTlsEnabled() {
        return config.isTlsEnabled();
    }

    public String getHealthReport() {
        return String.format(
            "Server: %s | Port: %d | TLS: %s | DB: %s",
            getServerName(),
            getPort(),
            isTlsEnabled() ? "ON" : "OFF",
            isDatabaseHealthy() ? "OK" : "ERROR"
        );
    }
}
