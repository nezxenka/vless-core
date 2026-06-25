package com.nezxenka.vlesscore.config;

public class SslSettings {

    private final boolean enabled;
    private final String certPath;
    private final String keyPath;

    public SslSettings(AppConfig config) {
        this.enabled = config.isTlsEnabled();
        this.certPath = config.getTlsCertPath();
        this.keyPath = config.getTlsKeyPath();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getCertPath() {
        return certPath;
    }

    public String getKeyPath() {
        return keyPath;
    }

    public boolean isConfigured() {
        return (
            enabled &&
            certPath != null &&
            !certPath.isEmpty() &&
            keyPath != null &&
            !keyPath.isEmpty()
        );
    }
}
