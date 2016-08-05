package com.upwork.dsavitski.lobby.message;

import javax.json.Json;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * Encodes message to Json format.
 */
public class MessageEncoder implements Encoder.Text<Message> {
    @Override
    public String encode(Message message) throws EncodeException {
        return Json.createObjectBuilder()
                .add("type", message.getType().toString())
                .add("message", message.getMessage())
                .build()
                .toString();
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
    }

    @Override
    public void destroy() {
    }
}
