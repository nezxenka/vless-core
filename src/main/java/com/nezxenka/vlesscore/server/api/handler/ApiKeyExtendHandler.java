package com.nezxenka.vlesscore.server.api.handler;

import com.nezxenka.vlesscore.config.AppConfig;
import com.nezxenka.vlesscore.database.dao.TokenDao;
import com.nezxenka.vlesscore.database.model.Token;
import com.nezxenka.vlesscore.server.api.ApiAuthFilter;
import com.nezxenka.vlesscore.server.api.ApiQueryParser;
import com.nezxenka.vlesscore.server.api.ApiResponseBuilder;
import com.nezxenka.vlesscore.server.api.JsonEscaper;
import com.nezxenka.vlesscore.service.TokenManagementService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class ApiKeyExtendHandler implements HttpHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyExtendHandler.class);

    private final AppConfig config;
    private final TokenDao tokenDao;
    private final TokenManagementService tokenService;
    private final ApiAuthFilter auth;

    public ApiKeyExtendHandler(AppConfig config, TokenDao tokenDao, TokenManagementService tokenService) {
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
            Map<String, String> params = ApiQueryParser.parse(ex.getRequestURI().getQuery());
            String tokenStr = params.getOrDefault("token", "");
            int days = Integer.parseInt(params.getOrDefault("days", "30"));

            Token result = tokenDao.extendToken(tokenStr, days);
            if (result == null) {
                ApiResponseBuilder.sendNotFound(ex);
                return;
            }

            ApiResponseBuilder.sendJson(ex, 200, String.format(
                    "{\"success\":true,\"token\":\"%s\",\"new_expires_at\":%d,\"remaining_days\":%d}",
                    result.getToken(), result.getExpiresAt(), result.getRemainingDays()
            ));
            log.info("[API] Key extended: {} by {} days",
                    tokenStr.substring(0, Math.min(20, tokenStr.length())), days);

        } catch (Exception e) {
            ApiResponseBuilder.sendJson(ex, 500,
                    "{\"error\":\"" + JsonEscaper.escape(e.getMessage()) + "\"}");
        }
    }
}
