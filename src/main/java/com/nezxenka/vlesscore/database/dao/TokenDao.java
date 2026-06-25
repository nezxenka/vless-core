package com.nezxenka.vlesscore.database.dao;

import com.nezxenka.vlesscore.database.model.Token;
import java.sql.SQLException;
import java.util.List;

public interface TokenDao {
    Token createToken(int days) throws SQLException;
    Token createTokenWithExpiry(long expiresAt) throws SQLException;
    Token findByToken(String tokenStr) throws SQLException;
    void createMappedToken(String uuid, String tokenRef) throws SQLException;
    String resolveUuidToToken(String uuid) throws SQLException;
    boolean validateToken(String uuidOrToken) throws SQLException;
    Token extendToken(String tokenStr, int days) throws SQLException;
    void updateStatus(String tokenStr, Token.Status status) throws SQLException;
    void incrementConnections(String uuidOrToken) throws SQLException;
    void addTraffic(String uuidOrToken, long bytesUp, long bytesDown)
        throws SQLException;
    void deleteToken(String tokenStr) throws SQLException;
    List<Token> listAll() throws SQLException;
    int countActive() throws SQLException;
    int countAll() throws SQLException;
    int countByStatus(Token.Status status) throws SQLException;
}
