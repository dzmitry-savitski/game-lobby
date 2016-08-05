package com.upwork.dsavitski.lobby.service;

import com.upwork.dsavitski.lobby.message.Message;

import javax.websocket.Session;

/**
 * Represents user service.
 */
public interface UserService {
    /**
     * Sends given message to all online users.
     */
    void sendMessage(Message mess);

    /**
     * Logging in given user to lobby.
     * Sends appropriate message to confirm or decline login.
     */
    void loginUser(Session session, Message message);

    /**
     * Logging out user with given session.
     */
    void logoutUser(Session session);
}
