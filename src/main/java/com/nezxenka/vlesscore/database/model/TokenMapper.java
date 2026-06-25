package com.nezxenka.vlesscore.database.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class TokenMapper {

    private TokenMapper() {}

    public static Token fromResultSet(ResultSet rs) throws SQLException {
        Token t = new Token();
        t.setId(rs.getInt("id"));
        t.setToken(rs.getString("token"));
        t.setStatus(Token.Status.valueOf(rs.getString("status")));
        t.setCreatedAt(rs.getLong("created_at"));
        t.setExpiresAt(rs.getLong("expires_at"));
        t.setBytesUp(rs.getLong("bytes_up"));
        t.setBytesDown(rs.getLong("bytes_down"));
        t.setConnections(rs.getInt("connections"));
        return t;
    }
}
