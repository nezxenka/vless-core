package com.nezxenka.vlesscore.core.session;

import com.nezxenka.vlesscore.core.protocol.VlessConnection;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class VlessSession {

    private final String sessionId;
    private final VlessConnection connection;
    private final Instant startTime;
    private final AtomicLong bytesUploaded = new AtomicLong(0);
    private final AtomicLong bytesDownloaded = new AtomicLong(0);
    private volatile SessionState state = SessionState.ACTIVE;

    public VlessSession(VlessConnection connection) {
        this.sessionId = UUID.randomUUID().toString();
        this.connection = connection;
        this.startTime = Instant.now();
    }

    public String getSessionId() {
        return sessionId;
    }

    public VlessConnection getConnection() {
        return connection;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public SessionState getState() {
        return state;
    }

    public void setState(SessionState state) {
        this.state = state;
    }

    public long getBytesUploaded() {
        return bytesUploaded.get();
    }

    public long getBytesDownloaded() {
        return bytesDownloaded.get();
    }

    public void addBytesUploaded(long bytes) {
        bytesUploaded.addAndGet(bytes);
    }

    public void addBytesDownloaded(long bytes) {
        bytesDownloaded.addAndGet(bytes);
    }

    public boolean isActive() {
        return state == SessionState.ACTIVE;
    }
}
