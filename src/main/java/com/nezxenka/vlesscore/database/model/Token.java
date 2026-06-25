package com.nezxenka.vlesscore.database.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Token {

    public enum Status {
        ACTIVE,
        BLOCKED,
        EXPIRED,
    }

    private int id;
    private String token;
    private Status status;
    private long createdAt;
    private long expiresAt;
    private long bytesUp;
    private long bytesDown;
    private int connections;

    public Token() {}

    public Token(String token, Status status, long createdAt, long expiresAt) {
        this.token = token;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public long getBytesUp() {
        return bytesUp;
    }

    public void setBytesUp(long bytesUp) {
        this.bytesUp = bytesUp;
    }

    public long getBytesDown() {
        return bytesDown;
    }

    public void setBytesDown(long bytesDown) {
        this.bytesDown = bytesDown;
    }

    public int getConnections() {
        return connections;
    }

    public void setConnections(int connections) {
        this.connections = connections;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }

    public boolean isUsable() {
        return status == Status.ACTIVE && !isExpired();
    }

    public String getExpiresFormatted() {
        return LocalDateTime.ofInstant(
            Instant.ofEpochMilli(expiresAt),
            ZoneId.systemDefault()
        ).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    public long getRemainingDays() {
        long diff = expiresAt - System.currentTimeMillis();
        return Math.max(0, diff / (1000L * 60 * 60 * 24));
    }
}
