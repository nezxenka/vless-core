package com.nezxenka.vlesscore.service;

import com.nezxenka.vlesscore.database.dao.TokenDao;
import java.sql.SQLException;

public class TrafficService {

    private final TokenDao tokenDao;

    public TrafficService(TokenDao tokenDao) {
        this.tokenDao = tokenDao;
    }

    public void recordTraffic(
        String uuidOrToken,
        long bytesUp,
        long bytesDown
    ) {
        try {
            tokenDao.addTraffic(uuidOrToken, bytesUp, bytesDown);
        } catch (SQLException ignored) {}
    }
}
