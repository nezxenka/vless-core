package com.nezxenka.vlesscore.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConfigValidator {

    private static final Logger log = LoggerFactory.getLogger(
        ConfigValidator.class
    );

    private ConfigValidator() {}

    public static void validate(AppConfig config) {
        if (config.getPort() < 1 || config.getPort() > 65535) {
            log.warn("Invalid port: {}. Using default 443.", config.getPort());
        }
        if (config.getSocks5Port() < 1 || config.getSocks5Port() > 65535) {
            log.warn(
                "Invalid SOCKS5 port: {}. Using default 1080.",
                config.getSocks5Port()
            );
        }
        if (config.getApiPort() < 1 || config.getApiPort() > 65535) {
            log.warn(
                "Invalid API port: {}. Using default 8080.",
                config.getApiPort()
            );
        }
        if (config.getDefaultTokenDays() < 1) {
            log.warn(
                "Invalid default-token-days: {}. Using 30.",
                config.getDefaultTokenDays()
            );
        }
        if ("CHANGE_ME_SECRET_KEY_123".equals(config.getApiSecret())) {
            log.warn(
                "API secret is set to default value. Change it in config.yml for security!"
            );
        }
    }
}
