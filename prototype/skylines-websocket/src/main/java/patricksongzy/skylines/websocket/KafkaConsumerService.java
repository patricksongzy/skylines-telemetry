package patricksongzy.skylines.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.RawValue;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class KafkaConsumerService {
    private static final Logger LOG = LogManager.getLogger(KafkaConsumerService.class);
    private final ConcurrentHashMap<String, WebSocketSession> sessions;

    @Autowired
    public KafkaConsumerService(ConcurrentHashMap<String, WebSocketSession> sessions) {
        this.sessions = sessions;
    }

    @KafkaListener(topics = "telemetry.ambulance")
    public void ambulanceStream(List<ConsumerRecord<String, String>> messages) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        messages.forEach(message -> {
            if (message != null) {
                rootNode.putRawValue(message.key(), new RawValue(message.value()));
            }
        });

        for (WebSocketSession wss :sessions.values()) {
            try {
                wss.sendMessage(new TextMessage(rootNode.toString()));
            } catch (IOException e) {
                LOG.error("error: unable to send message to websocket", e);
            }
        }
    }
}
