package com.nezxenka.vlesscore.server.socks5;

import com.nezxenka.vlesscore.database.dao.TokenDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Socks5AuthValidator {

    private static final Logger log = LoggerFactory.getLogger(Socks5AuthValidator.class);

    private final TokenDao tokenDao;

    public Socks5AuthValidator(TokenDao tokenDao) {
        this.tokenDao = tokenDao;
    }

    public boolean validate(String username, String password) {
        try {
            return tokenDao.validateToken(username) ||
                    tokenDao.validateToken(password);
        } catch (Exception e) {
            log.error("SOCKS5 auth validation error", e);
            return false;
        }
    }
}
