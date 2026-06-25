package com.nezxenka.vlesscore.config;

import java.util.Map;

public class AppConfig {

    private String serverName = "VLESS-Core";
    private String serverAddress = "c15.play2go.cloud";
    private int port = 443;
    private boolean tlsEnabled = false;
    private String tlsCertPath = "";
    private String tlsKeyPath = "";
    private String databaseFile = "data.db";
    private int defaultTokenDays = 30;
    private int workerThreads = 0;
    private String logLevel = "INFO";
    private boolean silentMode = true;
    private boolean socks5Enabled = true;
    private int socks5Port = 1080;
    private boolean socks5AuthEnabled = true;
    private boolean apiEnabled = true;
    private int apiPort = 8080;
    private String apiSecret = "CHANGE_ME_SECRET_KEY_123";

    public AppConfig() {}

    @SuppressWarnings("unchecked")
    public static AppConfig fromMap(Map<String, Object> map) {
        AppConfig c = new AppConfig();
        if (map == null) return c;

        if (map.containsKey("server-name")) c.serverName = (String) map.get(
            "server-name"
        );
        if (map.containsKey("server-address")) c.serverAddress =
            (String) map.get("server-address");
        if (map.containsKey("port")) c.port = (
            (Number) map.get("port")
        ).intValue();
        if (map.containsKey("tls-enabled")) c.tlsEnabled = (Boolean) map.get(
            "tls-enabled"
        );
        if (map.containsKey("tls-cert-path")) c.tlsCertPath = (String) map.get(
            "tls-cert-path"
        );
        if (map.containsKey("tls-key-path")) c.tlsKeyPath = (String) map.get(
            "tls-key-path"
        );
        if (map.containsKey("database-file")) c.databaseFile = (String) map.get(
            "database-file"
        );
        if (map.containsKey("default-token-days")) c.defaultTokenDays = (
            (Number) map.get("default-token-days")
        ).intValue();
        if (map.containsKey("worker-threads")) c.workerThreads = (
            (Number) map.get("worker-threads")
        ).intValue();
        if (map.containsKey("log-level")) c.logLevel = (String) map.get(
            "log-level"
        );
        if (map.containsKey("silent-mode")) c.silentMode = (Boolean) map.get(
            "silent-mode"
        );
        if (map.containsKey("api-enabled")) c.apiEnabled = (Boolean) map.get(
            "api-enabled"
        );
        if (map.containsKey("api-port")) c.apiPort = (
            (Number) map.get("api-port")
        ).intValue();
        if (map.containsKey("socks5-enabled")) c.socks5Enabled =
            (Boolean) map.get("socks5-enabled");
        if (map.containsKey("socks5-port")) c.socks5Port = (
            (Number) map.get("socks5-port")
        ).intValue();
        if (map.containsKey("socks5-auth-enabled")) c.socks5AuthEnabled =
            (Boolean) map.get("socks5-auth-enabled");
        if (map.containsKey("api-secret")) c.apiSecret = (String) map.get(
            "api-secret"
        );

        return c;
    }

    public Map<String, Object> toMap() {
        return Map.ofEntries(
            Map.entry("socks5-enabled", socks5Enabled),
            Map.entry("socks5-port", socks5Port),
            Map.entry("socks5-auth-enabled", socks5AuthEnabled),
            Map.entry("server-name", serverName),
            Map.entry("server-address", serverAddress),
            Map.entry("port", port),
            Map.entry("tls-enabled", tlsEnabled),
            Map.entry("tls-cert-path", tlsCertPath),
            Map.entry("tls-key-path", tlsKeyPath),
            Map.entry("database-file", databaseFile),
            Map.entry("default-token-days", defaultTokenDays),
            Map.entry("worker-threads", workerThreads),
            Map.entry("log-level", logLevel),
            Map.entry("silent-mode", silentMode),
            Map.entry("api-enabled", apiEnabled),
            Map.entry("api-port", apiPort),
            Map.entry("api-secret", apiSecret)
        );
    }

    public String getServerName() {
        return serverName;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public int getPort() {
        return port;
    }

    public boolean isTlsEnabled() {
        return tlsEnabled;
    }

    public String getTlsCertPath() {
        return tlsCertPath;
    }

    public String getTlsKeyPath() {
        return tlsKeyPath;
    }

    public String getDatabaseFile() {
        return databaseFile;
    }

    public int getDefaultTokenDays() {
        return defaultTokenDays;
    }

    public int getWorkerThreads() {
        return workerThreads;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public boolean isSilentMode() {
        return silentMode;
    }

    public boolean isApiEnabled() {
        return apiEnabled;
    }

    public int getApiPort() {
        return apiPort;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public boolean isSocks5Enabled() {
        return socks5Enabled;
    }

    public int getSocks5Port() {
        return socks5Port;
    }

    public boolean isSocks5AuthEnabled() {
        return socks5AuthEnabled;
    }

    public void setSilentMode(boolean silentMode) {
        this.silentMode = silentMode;
    }
}
