package io.github.hero821.example.springboot.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@EnableWebSocket
@EnableScheduling
public class WebSocketConfig implements WebSocketConfigurer {
    public static final Map<WebSocketSession, JsonNode> cache = new HashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketHandler(), "/ws").setAllowedOrigins("*");
    }

    static class WebSocketHandler extends TextWebSocketHandler {
        @Override
        public void afterConnectionEstablished(WebSocketSession session) {
            log.debug("连接成功");
        }

        @Override
        public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            JsonNode json = mapper.readTree(message.getPayload());
            if ("subscribe".equals(json.get("action").asText())) {
                cache.put(session, json);
                ArrayNode resources = (ArrayNode) json.get("resources");
                for (JsonNode resource : resources) {
                    ObjectNode item = (ObjectNode) resource;
                    // todo 查询数据库，根据type+id，更新属性
                    item.put("label", "");
                    item.put("color", "");
                }
                json = ((ObjectNode) mapper.readTree(mapper.writeValueAsString(json))).put("action", "refresh");
                session.sendMessage(new TextMessage(mapper.writeValueAsString(json)));
            } else {
                log.error("无法识别的action");
            }
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) {
            log.error("传输异常", exception);
            cache.remove(session);
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
            log.debug("连接关闭, Status:{}", status);
            cache.remove(session);
        }
    }

    @Scheduled(cron = "0/5 * * * * ?")
    private void generateAlarm() throws Exception {
        // todo 模拟告警，根据type+id，更新属性
        for (Map.Entry<WebSocketSession, JsonNode> entry : cache.entrySet()) {
            WebSocketSession session = entry.getKey();
            JsonNode json = entry.getValue();

            ArrayNode resources1 = (ArrayNode) json.get("resources");
            ArrayNode resources2 = mapper.createArrayNode();
            for (JsonNode resource1 : resources1) {
                ObjectNode item = (ObjectNode) resource1;
                if ("upf".equals(item.get("type").asText()) && "001".equals(item.get("id").asText())) {
                    item.put("label", "告警级别：紧急");
                    item.put("color", "0xff0000");
                    resources2.add(item);
                }
            }

            if (!resources2.isEmpty()) {
                json = ((ObjectNode) mapper.readTree(mapper.writeValueAsString(json))).put("action", "refresh").set("resources", resources2);
                session.sendMessage(new TextMessage(mapper.writeValueAsString(json)));
            }
        }
    }
}