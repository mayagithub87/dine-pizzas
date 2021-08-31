package client.dine.pizza.listener;

import client.dine.pizza.listener.handler.CustomStompSessionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Component
public class WebSocketClientListener {


    public WebSocketClientListener(
            @Value("${dine-pizza-websocket-url}") String websocketUrl) {

        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        //Calls initialize() after the container applied all property values.
        taskScheduler.afterPropertiesSet();

        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompClient.setTaskScheduler(taskScheduler); // for heartbeats

        StompSessionHandler sessionHandler = new CustomStompSessionHandler();
        stompClient.connect(websocketUrl, sessionHandler);
    }


}
