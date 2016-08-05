package com.upwork.dsavitski.lobby.service.impl;

import com.upwork.dsavitski.lobby.message.Message;
import com.upwork.dsavitski.lobby.message.MessageType;
import com.upwork.dsavitski.lobby.service.ChatService;
import com.upwork.dsavitski.lobby.service.UserService;

import javax.websocket.Session;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ChatServiceImpl implements ChatService {

    private static ChatServiceImpl instance = new ChatServiceImpl();

    private UserService userService;

    private ChatServiceImpl() {
        userService = UserServiceImpl.getInstance();
    }

    public static ChatServiceImpl getInstance() {
        return instance;
    }

    /**
     * Processes chat message sent by user.
     */
    @Override
    public void chatMessage(Session session, Message message) {
        String sentBy = (String) session.getUserProperties().get("login");

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss] ");
        String time = sdf.format(cal.getTime());

        Message mess = new Message();
        mess.setType(MessageType.CHAT);
        mess.setMessage(time + sentBy + " : " + message.getMessage());

        userService.sendMessage(mess);
    }
}
