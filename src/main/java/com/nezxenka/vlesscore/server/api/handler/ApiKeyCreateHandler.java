package com.nezxenka.vlesscore.server.api.handler;

import com.nezxenka.vlesscore.config.AppConfig;
import com.nezxenka.vlesscore.crypto.UuidConverter;
import com.nezxenka.vlesscore.database.dao.TokenDao;
import com.nezxenka.vlesscore.database.model.Token;
import com.nezxenka.vlesscore.server.api.ApiAuthFilter;
import com.nezxenka.vlesscore.server.api.ApiQueryParser;
import com.nezxenka.vlesscore.server.api.ApiResponseBuilder;
import com.nezxenka.vlesscore.server.api.JsonEscaper;
import com.nezxenka.vlesscore.service.TokenManagementService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiKeyCreateHandler implements HttpHandler {

    private static final Logger log = LoggerFactory.getLogger(
        ApiKeyCreateHandler.class
    );

    private final AppConfig config;
    private final TokenDao tokenDao;
    private final TokenManagementService tokenService;
    private final ApiAuthFilter auth;

    public ApiKeyCreateHandler(
        AppConfig config,
        TokenDao tokenDao,
        TokenManagementService tokenService
    ) {
        this.config = config;
        this.tokenDao = tokenDao;
        this.tokenService = tokenService;
        this.auth = new ApiAuthFilter(config.getApiSecret());
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (!auth.check(ex)) return;
        if (!ex.getRequestMethod().equalsIgnoreCase("POST")) {
            ApiResponseBuilder.sendMethodNotAllowed(ex);
            return;
        }

        try {
            Map<String, String> qp = ApiQueryParser.parse(
                ex.getRequestURI().getQuery()
            );
            int days = qp.containsKey("days")
                ? Integer.parseInt(qp.get("days"))
                : config.getDefaultTokenDays();
            long expiresAt = qp.containsKey("expires_at")
                ? Long.parseLong(qp.get("expires_at"))
                : 0;

            Token token;
            if (expiresAt > 0) {
                token = tokenDao.createTokenWithExpiry(expiresAt);
            } else {
                token = tokenDao.createToken(days);
            }

            String clientUuid = UuidConverter.tokenToUuid(token.getToken());
            tokenDao.createMappedToken(clientUuid, token.getToken());

            String vlessLink = tokenService.buildVlessLink(clientUuid);

            String json = String.format(
                "{\"success\":true,\"token\":\"%s\",\"uuid\":\"%s\",\"vless_link\":\"%s\"," +
                    "\"expires_at\":%d,\"days\":%d,\"server_name\":\"%s\",\"address\":\"%s\",\"port\":%d}",
                token.getToken(),
                clientUuid,
                vlessLink,
                token.getExpiresAt(),
                days,
                config.getServerName(),
                config.getServerAddress(),
                config.getPort()
            );
            ApiResponseBuilder.sendJson(ex, 200, json);
            log.info(
                "[API] Key created: {} for {} days",
                token.getToken().substring(0, 20) + "...",
                days
            );
        } catch (Exception e) {
            log.error("[API] Key creation error", e);
            ApiResponseBuilder.sendJson(
                ex,
                500,
                "{\"error\":\"" + JsonEscaper.escape(e.getMessage()) + "\"}"
            );
        }
    }
}
