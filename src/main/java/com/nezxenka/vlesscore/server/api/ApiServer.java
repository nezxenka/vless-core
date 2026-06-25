package com.nezxenka.vlesscore.server.api;

import com.nezxenka.vlesscore.config.AppConfig;
import com.nezxenka.vlesscore.database.dao.TokenDao;
import com.nezxenka.vlesscore.service.TokenManagementService;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class ApiServer {

    private static final Logger log = LoggerFactory.getLogger(ApiServer.class);

    private final AppConfig config;
    private final TokenDao tokenDao;
    private final TokenManagementService tokenService;
    private HttpServer server;

    public ApiServer(AppConfig config, TokenDao tokenDao, TokenManagementService tokenService) {
        this.config = config;
        this.tokenDao = tokenDao;
        this.tokenService = tokenService;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(config.getApiPort()), 0);
        server.setExecutor(Executors.newFixedThreadPool(4));

        server.createContext("/api/keys/create",
                new ApiKeyCreateHandler(config, tokenDao, tokenService));
        server.createContext("/api/keys/extend",
                new ApiKeyExtendHandler(config, tokenDao, tokenService));
        server.createContext("/api/keys/freeze",
                new ApiKeyFreezeHandler(config, tokenDao));
        server.createContext("/api/keys/unfreeze",
                new ApiKeyUnfreezeHandler(config, tokenDao));
        server.createContext("/api/keys/delete",
                new ApiKeyDeleteHandler(config, tokenDao));
        server.createContext("/api/keys/info",
                new ApiKeyInfoHandler(config, tokenDao));
        server.createContext("/api/keys/list",
                new ApiKeyListHandler(config, tokenDao));
        server.createContext("/api/ping",
                new ApiPingHandler(config));

        server.start();
        log.info("HTTP API started on port {}", config.getApiPort());
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }
}
