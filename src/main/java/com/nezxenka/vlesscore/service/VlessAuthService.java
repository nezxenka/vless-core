package com.nezxenka.vlesscore.service;

import com.nezxenka.vlesscore.database.dao.TokenDao;
import com.nezxenka.vlesscore.database.model.Token;
import java.sql.SQLException;

public class VlessAuthService {

    private final TokenDao tokenDao;

    public VlessAuthService(TokenDao tokenDao) {
        this.tokenDao = tokenDao;
    }

    public boolean authenticate(String uuidOrToken) {
        try {
            return tokenDao.validateToken(uuidOrToken);
        } catch (SQLException e) {
            return false;
        }
    }

    public void recordConnection(String uuidOrToken) {
        try {
            tokenDao.incrementConnections(uuidOrToken);
        } catch (SQLException ignored) {}
    }
}
