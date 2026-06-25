package com.nezxenka.vlesscore.database.repository;

import com.nezxenka.vlesscore.database.dao.TokenDao;
import com.nezxenka.vlesscore.database.model.Token;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class TokenRepository {

    private final TokenDao tokenDao;

    public TokenRepository(TokenDao tokenDao) {
        this.tokenDao = tokenDao;
    }

    public Token create(int days) throws SQLException {
        return tokenDao.createToken(days);
    }

    public Token createWithExpiry(long expiresAt) throws SQLException {
        return tokenDao.createTokenWithExpiry(expiresAt);
    }

    public Optional<Token> findByToken(String tokenStr) throws SQLException {
        return Optional.ofNullable(tokenDao.findByToken(tokenStr));
    }

    public Token extend(String tokenStr, int days) throws SQLException {
        return tokenDao.extendToken(tokenStr, days);
    }

    public void block(String tokenStr) throws SQLException {
        tokenDao.updateStatus(tokenStr, Token.Status.BLOCKED);
    }

    public void unblock(String tokenStr) throws SQLException {
        tokenDao.updateStatus(tokenStr, Token.Status.ACTIVE);
    }

    public void delete(String tokenStr) throws SQLException {
        tokenDao.deleteToken(tokenStr);
    }

    public List<Token> findAll() throws SQLException {
        return tokenDao.listAll();
    }

    public int countActive() throws SQLException {
        return tokenDao.countActive();
    }

    public int countAll() throws SQLException {
        return tokenDao.countAll();
    }

    public int countByStatus(Token.Status status) throws SQLException {
        return tokenDao.countByStatus(status);
    }
}
