package com.nezxenka.vlesscore;

import com.nezxenka.vlesscore.banner.StartupBanner;
import com.nezxenka.vlesscore.cli.CommandDispatcher;
import com.nezxenka.vlesscore.cli.ConsoleReader;
import com.nezxenka.vlesscore.cli.ConsoleWriter;
import com.nezxenka.vlesscore.config.AppConfig;
import com.nezxenka.vlesscore.config.ConfigLoader;
import com.nezxenka.vlesscore.database.connection.DatabaseManager;
import com.nezxenka.vlesscore.monitoring.HealthReporter;
import com.nezxenka.vlesscore.monitoring.SystemMetrics;
import com.nezxenka.vlesscore.server.api.ApiServer;
import com.nezxenka.vlesscore.server.socks5.Socks5Server;
import com.nezxenka.vlesscore.server.vless.ProxyServer;
import com.nezxenka.vlesscore.server.vless.VlessServerHandler;
import com.nezxenka.vlesscore.service.StatisticsService;
import com.nezxenka.vlesscore.service.TokenManagementService;
import com.nezxenka.vlesscore.service.TrafficService;
import com.nezxenka.vlesscore.service.VlessAuthService;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static final String VERSION = "1.0";

    public static void main(String[] args) {
        StartupBanner.print(VERSION);

        Path baseDir = Paths.get(".").toAbsolutePath().normalize();

        log.info("Loading configuration...");
        ConfigLoader configLoader = new ConfigLoader(baseDir);
        AppConfig config = configLoader.loadOrCreate();
        log.info(
            "Configuration loaded: port={}, tls={}",
            config.getPort(),
            config.isTlsEnabled()
        );

        log.info("Initializing database...");
        DatabaseManager dbManager = new DatabaseManager(baseDir, config);
        dbManager.initialize();
        log.info("Database ready.");

        VlessServerHandler.silentMode = config.isSilentMode();
        if (config.isSilentMode()) {
            log.info(
                "Silent mode ENABLED (silent-mode: true). Toggle with /silent"
            );
        }

        VlessAuthService authService = new VlessAuthService(
            dbManager.getTokenDao()
        );
        TokenManagementService tokenService = new TokenManagementService(
            dbManager.getTokenDao(),
            config
        );
        TrafficService trafficService = new TrafficService(
            dbManager.getTokenDao()
        );
        StatisticsService statsService = new StatisticsService(
            dbManager.getTokenDao()
        );
        SystemMetrics systemMetrics = new SystemMetrics();
        HealthReporter healthReporter = new HealthReporter(config, dbManager);

        ConsoleWriter console = new ConsoleWriter();
        CommandDispatcher dispatcher = new CommandDispatcher(
            tokenService,
            config,
            console,
            statsService,
            systemMetrics,
            healthReporter
        );

        ProxyServer proxyServer = new ProxyServer(
            config,
            dbManager.getTokenDao(),
            authService,
            trafficService
        );
        Thread vlessThread = new Thread(() -> {
            try {
                proxyServer.start();
            } catch (Exception e) {
                log.error("VLESS server start error", e);
                System.exit(1);
            }
        }, "proxy-server");
        vlessThread.setDaemon(false);
        vlessThread.start();

        ApiServer apiServer = null;
        if (config.isApiEnabled()) {
            try {
                apiServer = new ApiServer(
                    config,
                    dbManager.getTokenDao(),
                    tokenService
                );
                apiServer.start();
            } catch (Exception e) {
                log.error("API server start error", e);
            }
        }

        Socks5Server socks5Server = null;
        if (config.isSocks5Enabled()) {
            try {
                socks5Server = new Socks5Server(
                    config,
                    dbManager.getTokenDao(),
                    authService
                );

                Socks5Server finalSocks5Server = socks5Server;
                Thread socksThread = new Thread(() -> {
                    try {
                        finalSocks5Server.start();
                    } catch (Exception e) {
                        log.error("SOCKS5 server start error", e);
                    }
                }, "socks5-server");
                socksThread.setDaemon(false);
                socksThread.start();

                log.info("SOCKS5 enabled. Port: {}", config.getSocks5Port());
            } catch (Exception e) {
                log.error("SOCKS5 server init error", e);
            }
        }

        dispatcher.printHelp();

        ApiServer finalApiServer = apiServer;
        Socks5Server finalSocks5Server1 = socks5Server;
        Runtime.getRuntime().addShutdownHook(
            new Thread(() -> {
                log.info("Shutting down...");
                try {
                    proxyServer.stop();
                } catch (Exception ignored) {}
                try {
                    if (finalApiServer != null) finalApiServer.stop();
                } catch (Exception ignored) {}
                try {
                    if (finalSocks5Server1 != null) finalSocks5Server1.stop();
                } catch (Exception ignored) {}
                try {
                    dbManager.close();
                } catch (Exception ignored) {}
                log.info("Stopped.");
            }, "shutdown-hook")
        );

        ConsoleReader consoleReader = new ConsoleReader(dispatcher);
        consoleReader.start();
    }
}
