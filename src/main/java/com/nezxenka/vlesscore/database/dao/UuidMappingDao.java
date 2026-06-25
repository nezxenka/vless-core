package com.nezxenka.vlesscore.database.dao;

import com.nezxenka.vlesscore.database.model.Token;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UuidMappingDao {

    private final Connection conn;

    public UuidMappingDao(Connection conn) {
        this.conn = conn;
    }

    public synchronized void createMapping(String uuid, String tokenRef)
        throws SQLException {
        String sql =
            "INSERT OR REPLACE INTO token_uuid_map (uuid, token_ref) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid);
            ps.setString(2, tokenRef);
            ps.executeUpdate();
        }
    }

    public synchronized String resolveToken(String uuid) throws SQLException {
        String sql = "SELECT token_ref FROM token_uuid_map WHERE uuid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("token_ref");
                return null;
            }
        }
    }

    public synchronized void deleteByToken(String tokenRef)
        throws SQLException {
        String sql = "DELETE FROM token_uuid_map WHERE token_ref = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tokenRef);
            ps.executeUpdate();
        }
    }
}
