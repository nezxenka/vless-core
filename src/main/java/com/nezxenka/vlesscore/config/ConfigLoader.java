package com.nezxenka.vlesscore.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class ConfigLoader {

    private static final Logger log = LoggerFactory.getLogger(
        ConfigLoader.class
    );
    private static final String CONFIG_NAME = "config.yml";
    private final Path configPath;

    public ConfigLoader(Path baseDir) {
        this.configPath = baseDir.resolve(CONFIG_NAME);
    }

    public AppConfig loadOrCreate() {
        if (!Files.exists(configPath)) {
            log.info("Configuration file not found. Creating config.yml...");
            ConfigWriter.writeDefaults(configPath);
            log.info("Created file: {}", configPath.toAbsolutePath());
        }

        try (InputStream is = Files.newInputStream(configPath)) {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(is);
            AppConfig config = AppConfig.fromMap(data);
            ConfigValidator.validate(config);
            return config;
        } catch (Exception e) {
            log.error("Error reading configuration", e);
            return new AppConfig();
        }
    }

    public Path getConfigPath() {
        return configPath;
    }
}
