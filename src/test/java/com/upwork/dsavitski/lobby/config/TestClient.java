package com.upwork.dsavitski.lobby.config;

import com.upwork.dsavitski.lobby.message.Message;
import com.upwork.dsavitski.lobby.message.MessageDecoder;
import com.upwork.dsavitski.lobby.message.MessageEncoder;
import com.upwork.dsavitski.lobby.message.MessageType;
import org.eclipse.jetty.util.component.LifeCycle;

import javax.websocket.*;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

@ClientEndpoint(encoders = MessageEncoder.class, decoders = MessageDecoder.class)
public class TestClient implements AutoCloseable {
    private static final String API_URL = "ws://localhost:8080/api";
    private static final int MESSAGE_WAIT_DURATION = 200;
    private static final int MESSAGE_WAIT_STEP = 100;
    private final Logger logger = Logger.getLogger(getClass().getName());
    private long lastMessageTimeMillis;
    private CountDownLatch latch = new CountDownLatch(1);

    private Map<MessageType, String> lastMessages = new ConcurrentHashMap<>();
    private Session session;

    public TestClient() throws Exception {
        URI uri = URI.create(API_URL);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);
    }

    public TestClient(String login) throws Exception {
        URI uri = URI.create(API_URL);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);

        Message message = new Message(MessageType.LOGIN, login);
        sendMessage(message);
    }

    public Map<MessageType, String> getLastMessages() throws InterruptedException {
        latch.await(); // waiting for first message
        while ((System.currentTimeMillis() - lastMessageTimeMillis) < MESSAGE_WAIT_DURATION) {
            Thread.sleep(MESSAGE_WAIT_STEP);
        }
        return lastMessages;
    }

    @OnMessage
    public void handleMessage(Message message) throws InterruptedException {
        lastMessageTimeMillis = System.currentTimeMillis();
        latch.countDown();
        lastMessages.put(message.getType(), message.getMessage());
        logger.info("Client received message : " + message);
    }

    @OnError
    public void handleError(Throwable unimportant) {
        // DO NOTHING
    }

    @Override
    public void close() throws Exception {
        WebSocketContainer container = session.getContainer();
        session.close();
        if (container instanceof LifeCycle) {
            ((LifeCycle) container).stop();
        }
    }

    public void sendMessage(Message message) throws Exception {
        session.getBasicRemote().sendObject(message);
    }
}
