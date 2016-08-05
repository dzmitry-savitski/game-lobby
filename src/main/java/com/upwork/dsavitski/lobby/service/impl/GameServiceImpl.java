package com.upwork.dsavitski.lobby.service.impl;

import com.upwork.dsavitski.lobby.message.Message;
import com.upwork.dsavitski.lobby.message.MessageType;
import com.upwork.dsavitski.lobby.service.GameService;
import com.upwork.dsavitski.lobby.service.UserService;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.websocket.Session;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GameServiceImpl implements GameService {

    private static final Set<String> gameSet =
            Collections.synchronizedSet(new HashSet<String>());

    private static GameServiceImpl instance = new GameServiceImpl();

    private UserService userService;

    private GameServiceImpl() {
        userService = UserServiceImpl.getInstance();
    }

    public static GameServiceImpl getInstance() {
        return instance;
    }

    /**
     * Creates new game from game creation message.
     */
    @Override
    public void createGame(Session session, Message message) {
        String createdBy = (String) session.getUserProperties().get("login");
        String gameName = message.getMessage() + " [created by: " + createdBy + "]";
        gameSet.add(gameName);
        updateGameList();

    }

    /**
     * Sends updated game list to all users.
     */
    @Override
    public void updateGameList() {
        Message message = new Message();
        message.setType(MessageType.GAMES);
        message.setMessage(getGameJsonArray());
        userService.sendMessage(message);
    }

    /**
     * Removes selected game from game list.
     * Sends appropriate message to all users.
     */
    @Override
    public void startGame(Session session, Message message) {
        gameSet.remove(message.getMessage());
        String mess = "The game " + message.getMessage() + " was started! Please, join it!";
        message.setMessage(mess);
        userService.sendMessage(message);
        updateGameList();
    }

    /**
     * Creates json array from game set.
     */
    private String getGameJsonArray() {
        final JsonArrayBuilder games = Json.createArrayBuilder();
        for (String game : gameSet) {
            games.add(game);
        }
        return games.build().toString();
    }
}
