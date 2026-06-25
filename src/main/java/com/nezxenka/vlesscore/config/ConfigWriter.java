package com.nezxenka.vlesscore.config;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public final class ConfigWriter {

    private static final Logger log = LoggerFactory.getLogger(
        ConfigWriter.class
    );

    private ConfigWriter() {}

    public static void writeDefaults(Path configPath) {
        AppConfig defaults = new AppConfig();

        LinkedHashMap<String, Object> ordered = new LinkedHashMap<>();
        ordered.put("server-name", defaults.getServerName());
        ordered.put("server-address", defaults.getServerAddress());
        ordered.put("port", defaults.getPort());
        ordered.put("tls-enabled", defaults.isTlsEnabled());
        ordered.put("tls-cert-path", defaults.getTlsCertPath());
        ordered.put("tls-key-path", defaults.getTlsKeyPath());
        ordered.put("database-file", defaults.getDatabaseFile());
        ordered.put("default-token-days", defaults.getDefaultTokenDays());
        ordered.put("worker-threads", defaults.getWorkerThreads());
        ordered.put("log-level", defaults.getLogLevel());
        ordered.put("silent-mode", defaults.isSilentMode());

        DumperOptions opts = new DumperOptions();
        opts.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        opts.setPrettyFlow(true);
        opts.setIndent(2);

        Yaml yaml = new Yaml(opts);

        try (
            Writer writer = Files.newBufferedWriter(
                configPath,
                StandardCharsets.UTF_8
            )
        ) {
            writer.write("# ===================================\n");
            writer.write("# VLESS Core - Server Configuration\n");
            writer.write("# ===================================\n");
            writer.write("# silent-mode: true = suppress connection logs\n");
            writer.write("# Toggle at runtime: /silent\n\n");
            yaml.dump(ordered, writer);
        } catch (IOException e) {
            log.error("Failed to create config file", e);
        }
    }
}
