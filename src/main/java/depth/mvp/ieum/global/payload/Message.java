package depth.mvp.ieum.global.payload;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class Message {

    private String message;

    public Message(){};

    @Builder
    public Message(String message) {
        this.message = message;
    }
}
