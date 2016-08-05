package com.upwork.dsavitski.lobby.api;

import com.upwork.dsavitski.lobby.message.Message;
import com.upwork.dsavitski.lobby.message.MessageDecoder;
import com.upwork.dsavitski.lobby.message.MessageEncoder;
import com.upwork.dsavitski.lobby.message.MessageType;
import com.upwork.dsavitski.lobby.service.ChatService;
import com.upwork.dsavitski.lobby.service.GameService;
import com.upwork.dsavitski.lobby.service.UserService;
import com.upwork.dsavitski.lobby.service.impl.ChatServiceImpl;
import com.upwork.dsavitski.lobby.service.impl.GameServiceImpl;
import com.upwork.dsavitski.lobby.service.impl.UserServiceImpl;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates web socket connection point.
 */
@ServerEndpoint(value = "/api", encoders = MessageEncoder.class, decoders = MessageDecoder.class)
public class LobbyApi {
    private final Logger logger = Logger.getLogger(getClass().getName());
    private UserService userService;
    private GameService gameService;
    private ChatService chatService;

    public LobbyApi() {
        // managing dependencies
        userService = UserServiceImpl.getInstance();
        gameService = GameServiceImpl.getInstance();
        chatService = ChatServiceImpl.getInstance();
    }

    /**
     * Handles new session opening.
     */
    @OnOpen
    public void handleOpen(Session session) {
        logger.info("session opened");
    }

    /**
     * Handles receiving a message.
     * Dispatches message according to its' type.
     */
    @OnMessage
    public void handleMessage(Session session, Message message) {
        logger.info("Message received: " + message);
        if (message.getType() == MessageType.LOGIN) {
            userService.loginUser(session, message);
            gameService.updateGameList();
        } else if (message.getType() == MessageType.CHAT) {
            chatService.chatMessage(session, message);
        } else if (message.getType() == MessageType.CREATE_GAME) {
            gameService.createGame(session, message);
        } else if (message.getType() == MessageType.START_GAME) {
            gameService.startGame(session, message);
        } else {
            logger.warning("Unknown message type: " + message.getType());
        }
    }

    /**
     * Handles closing session.
     * Will be invoked also when connection lost.
     */
    @OnClose
    public void handleClose(Session session) {
        userService.logoutUser(session);
        logger.info("User logged out : " + session.getUserProperties().get("login"));
    }

    /**
     * Handles errors.
     */
    @OnError
    public void handleError(Throwable e) {
        logger.log(Level.WARNING, "Message error", e);
    }
}
