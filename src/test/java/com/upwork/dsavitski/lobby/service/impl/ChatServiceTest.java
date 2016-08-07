package com.upwork.dsavitski.lobby.service.impl;

import com.upwork.dsavitski.lobby.message.Message;
import com.upwork.dsavitski.lobby.message.MessageType;
import com.upwork.dsavitski.lobby.service.UserService;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.websocket.Session;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ChatServiceTest {

    @Test
    public void chatMessage() throws Exception {
        final String TEST_USER = "testUser";
        final String TEST_MESSAGE = "TestChat";
        UserService userService = mock(UserService.class);
        Session session = mock(Session.class);
        Map userProperties = mock(Map.class);
        when(session.getUserProperties()).thenReturn(userProperties);
        when(userProperties.get("login")).thenReturn(TEST_USER);
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        ChatServiceImpl chatService = ChatServiceImpl.getInstance();
        chatService.setUserService(userService);
        Message message = new Message(MessageType.CHAT, TEST_MESSAGE);

        chatService.chatMessage(session, message);

        verify(userService, times(1)).sendMessage(messageCaptor.capture());
        Message sent = messageCaptor.getValue();
        assertEquals(sent.getType(), MessageType.CHAT);
        assertTrue(sent.getMessage().contains(TEST_USER));
        assertTrue(sent.getMessage().contains(TEST_MESSAGE));
    }
}