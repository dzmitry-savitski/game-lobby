package com.upwork.dsavitski.lobby.service;

import com.upwork.dsavitski.lobby.message.Message;

import javax.websocket.Session;

/**
 * Service to work with games.
 */
public interface GameService {
    /**
     * Creates new game from game creation message.
     */
    void createGame(Session session, Message message);

    /**
     * Sends updated game list to all users.
     */
    void updateGameList();

    /**
     * Removes selected game from game list.
     * Sends appropriate message to all users.
     */
    void startGame(Session session, Message message);
}
