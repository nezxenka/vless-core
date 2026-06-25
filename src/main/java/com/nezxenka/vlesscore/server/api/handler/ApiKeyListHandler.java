package com.nezxenka.vlesscore.server.api.handler;

import com.nezxenka.vlesscore.config.AppConfig;
import com.nezxenka.vlesscore.database.dao.TokenDao;
import com.nezxenka.vlesscore.database.model.Token;
import com.nezxenka.vlesscore.server.api.ApiAuthFilter;
import com.nezxenka.vlesscore.server.api.ApiResponseBuilder;
import com.nezxenka.vlesscore.server.api.JsonEscaper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;

public class ApiKeyListHandler implements HttpHandler {

    private final AppConfig config;
    private final TokenDao tokenDao;
    private final ApiAuthFilter auth;

    public ApiKeyListHandler(AppConfig config, TokenDao tokenDao) {
        this.config = config;
        this.tokenDao = tokenDao;
        this.auth = new ApiAuthFilter(config.getApiSecret());
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (!auth.check(ex)) return;

        try {
            List<Token> tokens = tokenDao.listAll();
            StringBuilder sb = new StringBuilder("{\"keys\":[");
            for (int i = 0; i < tokens.size(); i++) {
                Token t = tokens.get(i);
                if (i > 0) sb.append(",");
                sb.append(String.format(
                        "{\"token\":\"%s\",\"status\":\"%s\",\"expires_at\":%d,\"connections\":%d}",
                        t.getToken(), t.getStatus().name(), t.getExpiresAt(), t.getConnections()
                ));
            }
            sb.append("],\"count\":").append(tokens.size()).append("}");
            ApiResponseBuilder.sendJson(ex, 200, sb.toString());

        } catch (Exception e) {
            ApiResponseBuilder.sendJson(ex, 500,
                    "{\"error\":\"" + JsonEscaper.escape(e.getMessage()) + "\"}");
        }
    }
}
