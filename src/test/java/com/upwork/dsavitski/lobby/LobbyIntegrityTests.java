package com.upwork.dsavitski.lobby;

import com.upwork.dsavitski.lobby.api.LobbyApi;
import com.upwork.dsavitski.lobby.config.TestClient;
import com.upwork.dsavitski.lobby.message.Message;
import com.upwork.dsavitski.lobby.message.MessageType;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.ServerContainer;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class LobbyIntegrityTests {
    private static Server server;

    @BeforeClass
    public static void startServer() throws Exception {
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.addConnector(connector);

        // Setup the basic application "context" for this application at "/"
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Initialize javax.websocket layer
        ServerContainer wscontainer = WebSocketServerContainerInitializer.configureContext(context);

        // Add WebSocket endpoint to javax.websocket layer
        wscontainer.addEndpoint(LobbyApi.class);

        server.start();
    }

    @AfterClass
    public static void stopServer() throws Exception {
        server.stop();
    }

    @Test
    public void testLoginOk() throws Exception {
        final String testLogin = "testLogin";
        Message message = new Message(MessageType.LOGIN, testLogin);

        try (
                TestClient client = new TestClient()
        ) {
            client.sendMessage(message);
            String lastLoginMessage = client.getLastMessages().get(MessageType.LOGIN);

            assertEquals(testLogin, lastLoginMessage);
        }
    }

    @Test
    public void testEmptyLogin() throws Exception {
        final String emptyLogin = "";

        Message message = new Message();
        message.setType(MessageType.LOGIN);
        message.setMessage(emptyLogin);

        try (
                TestClient client = new TestClient()
        ) {
            client.sendMessage(message);
            final String lastLoginMessage = client.getLastMessages().get(MessageType.LOGIN);

            assertEquals(emptyLogin, lastLoginMessage);
        }
    }

    @Test
    public void testLoginWithSameName() throws Exception {
        final String login = "repeatedLogin";
        Message message = new Message();
        message.setType(MessageType.LOGIN);
        message.setMessage(login);

        try (
                TestClient client1 = new TestClient();
                TestClient client2 = new TestClient()
        ) {
            client1.sendMessage(message);
            assertEquals(login, client1.getLastMessages().get(MessageType.LOGIN));

            client2.sendMessage(message);
            assertEquals("", client2.getLastMessages().get(MessageType.LOGIN));
        }
    }

    @Test
    public void testUserMessageAfterLogin() throws Exception {
        try (
                TestClient client1 = new TestClient("login1");
                TestClient client2 = new TestClient("login2")
        ) {
            assertEquals(client1.getLastMessages().get(MessageType.USERS),
                    client2.getLastMessages().get(MessageType.USERS));
        }
    }

    @Test
    public void testLogout() throws Exception {
        try (
                TestClient client1 = new TestClient("login1")
        ) {
            final String beforeLogin = client1.getLastMessages().get(MessageType.USERS);
            TestClient client2 = new TestClient("login2");
            Thread.sleep(300);
            final String afterLogin = client1.getLastMessages().get(MessageType.USERS);
            client2.close();
            Thread.sleep(300);
            final String afterLogout = client1.getLastMessages().get(MessageType.USERS);

            assertEquals(beforeLogin, afterLogout);
            assertNotEquals(beforeLogin, afterLogin);
        }
    }
}