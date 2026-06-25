package com.nezxenka.vlesscore.core.session;

import com.nezxenka.vlesscore.core.protocol.VlessConnection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionManager {

    private static final Logger log = LoggerFactory.getLogger(
        SessionManager.class
    );

    private final Map<String, VlessSession> sessions =
        new ConcurrentHashMap<>();

    public VlessSession createSession(VlessConnection connection) {
        VlessSession session = new VlessSession(connection);
        sessions.put(session.getSessionId(), session);
        log.debug("Session created: {}", session.getSessionId());
        return session;
    }

    public VlessSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public void closeSession(String sessionId) {
        VlessSession session = sessions.remove(sessionId);
        if (session != null) {
            session.setState(SessionState.CLOSED);
            log.debug("Session closed: {}", sessionId);
        }
    }

    public int getActiveSessionCount() {
        return (int) sessions
            .values()
            .stream()
            .filter(VlessSession::isActive)
            .count();
    }

    public int getTotalSessionCount() {
        return sessions.size();
    }

    public void cleanupStale() {
        sessions.entrySet().removeIf(entry -> !entry.getValue().isActive());
    }
}
