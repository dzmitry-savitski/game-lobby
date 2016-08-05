package com.upwork.dsavitski.lobby.service;

import com.upwork.dsavitski.lobby.message.Message;

import javax.websocket.Session;

/**
 * Service to work with chat.
 */
public interface ChatService {

    /**
     * Processes chat message sent by user.
     */
    void chatMessage(Session session, Message message);
}
