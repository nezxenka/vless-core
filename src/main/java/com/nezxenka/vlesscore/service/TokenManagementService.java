package com.nezxenka.vlesscore.service;

import com.nezxenka.vlesscore.config.AppConfig;
import com.nezxenka.vlesscore.crypto.UuidConverter;
import com.nezxenka.vlesscore.database.dao.TokenDao;
import com.nezxenka.vlesscore.database.model.Token;
import java.sql.SQLException;
import java.util.List;

public class TokenManagementService {

    private final TokenDao tokenDao;
    private final AppConfig config;

    public TokenManagementService(TokenDao tokenDao, AppConfig config) {
        this.tokenDao = tokenDao;
        this.config = config;
    }

    public Token createToken(int days) throws SQLException {
        return tokenDao.createToken(days);
    }

    public Token createTokenWithExpiry(long expiresAt) throws SQLException {
        return tokenDao.createTokenWithExpiry(expiresAt);
    }

    public void createMapping(String uuid, String tokenRef)
        throws SQLException {
        tokenDao.createMappedToken(uuid, tokenRef);
    }

    public Token findByToken(String tokenStr) throws SQLException {
        return tokenDao.findByToken(tokenStr);
    }

    public Token extendToken(String tokenStr, int days) throws SQLException {
        return tokenDao.extendToken(tokenStr, days);
    }

    public void blockToken(String tokenStr) throws SQLException {
        tokenDao.updateStatus(tokenStr, Token.Status.BLOCKED);
    }

    public void unblockToken(String tokenStr) throws SQLException {
        tokenDao.updateStatus(tokenStr, Token.Status.ACTIVE);
    }

    public void deleteToken(String tokenStr) throws SQLException {
        tokenDao.deleteToken(tokenStr);
    }

    public List<Token> listTokens() throws SQLException {
        return tokenDao.listAll();
    }

    public String buildVlessLink(String uuid) {
        String security = config.isTlsEnabled() ? "tls" : "none";
        String name = config.getServerName().replace(" ", "%20");
        return String.format(
            "vless://%s@%s:%d?encryption=none&type=tcp&security=%s#%s",
            uuid,
            config.getServerAddress(),
            config.getPort(),
            security,
            name
        );
    }

    public String buildSocks5Card(String usernameUuid, String passwordToken) {
        return (
            config.getServerAddress() +
            ":" +
            config.getSocks5Port() +
            "\n" +
            usernameUuid +
            "\n" +
            passwordToken +
            "\n" +
            "Socks5"
        );
    }
}
