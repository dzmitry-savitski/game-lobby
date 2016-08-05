package com.upwork.dsavitski.lobby.message;

import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.io.StringReader;

/**
 * Decodes message from Json format.
 */
public class MessageDecoder implements Decoder.Text<Message> {
    @Override
    public Message decode(String inputMessage) throws DecodeException {
        Message message = new Message();
        JsonObject obj = Json
                .createReader(new StringReader(inputMessage))
                .readObject();
        message.setType(MessageType.valueOf(obj.getString("type")));
        message.setMessage(obj.getString("message"));
        return message;
    }

    @Override
    public boolean willDecode(String inputMessage) {
        return true;
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
    }

    @Override
    public void destroy() {
    }
}
