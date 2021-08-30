package server.dine.pizza.core.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import server.dine.pizza.domain.WebsocketMessage;

@Controller
public class WebSocketController {

    private Logger logger = LoggerFactory.getLogger(WebSocketController.class);
    private final SimpMessagingTemplate template;

    public WebSocketController(SimpMessagingTemplate template) {
        this.template = template;
    }

    public void sendMessage(String customer, String message) {
        try {
            template.convertAndSend("/customer/" + customer, new WebsocketMessage(message));
        } catch (Throwable ex) {
            logger.error("sending message through websocket", ex);
        }
    }

}
