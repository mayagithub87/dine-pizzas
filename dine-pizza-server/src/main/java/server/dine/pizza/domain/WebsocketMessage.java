package server.dine.pizza.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WebsocketMessage {

    private String content;
    private String customer;
    private int countdown;

    public WebsocketMessage(String content) {
        this.content = content;
    }
}
