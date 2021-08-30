package server.dine.pizza.domain;

public class WebsocketMessage {

    private String content;

    public WebsocketMessage() {
    }

    public WebsocketMessage(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

}
