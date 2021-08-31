package client.dine.pizza.listener.handler;

import client.dine.pizza.domain.WebsocketMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.*;

import java.lang.reflect.Type;

public class CustomStompSessionHandler implements StompSessionHandler {

    private Logger logger = LoggerFactory.getLogger(CustomStompSessionHandler.class);

    @Override
    public void handleException(StompSession stompSession, StompCommand stompCommand, StompHeaders stompHeaders, byte[] bytes, Throwable throwable) {
        logger.error("stomp error", throwable);
    }

    @Override
    public void handleTransportError(StompSession stompSession, Throwable throwable) {
        logger.error("transport error", throwable);
    }

    @Override
    public Type getPayloadType(StompHeaders stompHeaders) {
        return WebsocketMessage.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        WebsocketMessage msg = (WebsocketMessage) payload;
        logger.info("Dine Pizza Notification: " + msg.getContent());
    }

    @Override
    public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {

        stompSession.subscribe("/customer", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders stompHeaders) {
                return WebsocketMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders stompHeaders, Object payload) {
                WebsocketMessage msg = (WebsocketMessage) payload;
                logger.info("Dine Pizza Notification: " + msg.getContent());
            }
        });
    }
}
