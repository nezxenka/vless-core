package com.nezxenka.vlesscore.service;

import com.nezxenka.vlesscore.database.dao.TokenDao;
import com.nezxenka.vlesscore.database.model.Token;
import java.sql.SQLException;

public class StatisticsService {

    private final TokenDao tokenDao;

    public StatisticsService(TokenDao tokenDao) {
        this.tokenDao = tokenDao;
    }

    public int getTotalKeys() throws SQLException {
        return tokenDao.countAll();
    }

    public int getActiveKeys() throws SQLException {
        return tokenDao.countActive();
    }

    public int getBlockedKeys() throws SQLException {
        return tokenDao.countByStatus(Token.Status.BLOCKED);
    }

    public int getExpiredKeys() throws SQLException {
        return tokenDao.countByStatus(Token.Status.EXPIRED);
    }
}
