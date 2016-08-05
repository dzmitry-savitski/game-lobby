package com.upwork.dsavitski.lobby.service.impl;

import com.upwork.dsavitski.lobby.message.Message;
import com.upwork.dsavitski.lobby.message.MessageType;
import com.upwork.dsavitski.lobby.service.UserService;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserServiceImpl implements UserService {

    private static final Set<Session> userSet =
            Collections.synchronizedSet(new HashSet<Session>());

    private static UserServiceImpl instance = new UserServiceImpl();

    private final Logger logger = Logger.getLogger(getClass().getName());

    private UserServiceImpl() {
    }

    public static UserServiceImpl getInstance() {
        return instance;
    }

    /**
     * Sends given message to all online users.
     */
    @Override
    public void sendMessage(Message mess) {
        for (Session user : userSet) {
            try {
                user.getBasicRemote().sendObject(mess);
            } catch (IOException | EncodeException e) {
                logger.log(Level.WARNING, "Error sending message", e);
            }
        }
    }

    /**
     * Logging in given user to lobby.
     * Sends appropriate message to confirm or decline login.
     */
    @Override
    public void loginUser(Session session, Message message) {
        try {
            String userLogin = message.getMessage();

            // checking empty login
            if (message.getMessage().isEmpty()) {
                session.getBasicRemote().sendObject(message);
                return;
            }

            // checking login already present
            for (Session sess : userSet) {
                if (userLogin.equals(sess.getUserProperties().get("login"))) {
                    message.setMessage("");
                    session.getBasicRemote().sendObject(message);
                    return;
                }
            }
            session.getUserProperties().put("login", userLogin);
            userSet.add(session);
            session.getBasicRemote().sendObject(message);
            updateUserList();

        } catch (IOException | EncodeException e) {
            logger.log(Level.WARNING, "login failed", e);
        }
    }

    /**
     * Logging out user with given session.
     */
    @Override
    public void logoutUser(Session session) {
        userSet.remove(session);
        updateUserList();
    }

    /**
     * Sends actual user list to all users.
     */
    private void updateUserList() {
        Message message = new Message();
        message.setType(MessageType.USERS);
        message.setMessage(getUserJsonArray());
        sendMessage(message);
    }

    /**
     * Creates json array from users set.
     */
    private String getUserJsonArray() {
        final JsonArrayBuilder users = Json.createArrayBuilder();
        for (Session session : userSet) {
            users.add((String) session.getUserProperties().get("login"));
        }
        return users.build().toString();
    }
}
