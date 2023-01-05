package patricksongzy.skylines.websocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TelemetryHandler extends AbstractWebSocketHandler {
    private static final Logger LOG = LogManager.getLogger(TelemetryHandler.class);
    private final ConcurrentHashMap<String, WebSocketSession> sessions;

    @Autowired
    public TelemetryHandler(ConcurrentHashMap<String, WebSocketSession> sessions) {
        this.sessions = sessions;
    }

    @Override
    public void afterConnectionEstablished(@Nullable WebSocketSession session) {
        if (session != null) {
            this.sessions.put(session.getId(), session);
        }
    }

    @Override
    public void afterConnectionClosed(@Nullable WebSocketSession session, @Nullable CloseStatus status) {
        if (session == null) {
            return;
        }

        try (WebSocketSession s = sessions.remove(session.getId())) {
            if (status != null) {
                s.close(status);
            }
        } catch (IOException e) {
            LOG.error("error: could not close session", e);
        }
    }
}
