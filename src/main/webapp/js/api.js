var server = "ws://localhost:8080/api2";
var wsocket;
var $login;
var $chatMessage;
var $gameName;

function onMessageReceived(jsonMessage) {
    var message = JSON.parse(jsonMessage.data);
    if (message.type == 'LOGIN') {
        checkLogin(message);
    } else if (message.type == 'USERS') {
        updateUserList(message);
    } else if (message.type == 'CHAT') {
        updateChat(message);
    } else if (message.type == 'GAMES') {
        updateGameList(message);
    } else if (message.type == 'START_GAME') {
        showStartedGameInfo(message);
    }
}

function connectToChatserver() {
    wsocket = new WebSocket(server);
    wsocket.onmessage = onMessageReceived;
}

function sendLogin() {
    if (wsocket.readyState != 1) {
        $('#error-connection').show();
    }

    var mess = {};
    mess.type = "LOGIN";
    mess.message = $login.val();
    $chatMessage.focus();
    var jsonMess = JSON.stringify(mess);
    wsocket.send(jsonMess);
}

function checkLogin(message) {
    if (message.message == "") {
        $('#error-alert').show();
    } else {
        $('.lobby-login').hide();
        $('.lobby-main').show();
        $('#userLogin').html(message.message);
    }
}

function updateUserList(message) {
    var users = JSON.parse(message.message);
    var usersTable = '';
    users.forEach(function (user) {
        usersTable += '<tr><td>' + user + '</td></tr>';
    });
    $('#usersTable').html(usersTable);
}

function updateGameList(message) {
    var games = JSON.parse(message.message);
    var gamessTable = '';
    games.forEach(function (game) {
        gamessTable += '<tr><td>' + game +
            "</td><td><button class='btn' onclick='startGame(\"" + game + "\")'>Start</button></td></tr>";
    });
    $('#gamesTable').html(gamessTable);
}

function createGame() {
    var mess = {};
    mess.type = "CREATE_GAME";
    mess.message = $gameName.val();
    $gameName.val('');
    var jsonMess = JSON.stringify(mess);
    wsocket.send(jsonMess);
}

function startGame(game) {
    var mess = {};
    mess.type = "START_GAME";
    mess.message = game;
    var jsonMess = JSON.stringify(mess);
    wsocket.send(jsonMess);
}

function showStartedGameInfo(message) {
    alert(message.message);
}

function updateChat(message) {
    $('#chat-window').append(message.message + '<br/>');
}

function sendChatMessage() {
    var mess = {};
    mess.type = "CHAT";
    mess.message = $chatMessage.val();
    $chatMessage.val('');
    var jsonMess = JSON.stringify(mess);
    wsocket.send(jsonMess);

}

$(document).ready(function () {
    $login = $('#login');
    $chatMessage = $('#chatMessage');
    $gameName = $('#gameName');

    connectToChatserver();

    $login.focus();


    $('#loginButton').click(function (evt) {
        evt.preventDefault();
        sendLogin();
        $chatMessage.focus();
    });

    $('#chatButton').click(function (evt) {
        evt.preventDefault();
        sendChatMessage();
    });

    $('#createGameButton').click(function (evt) {
        evt.preventDefault();
        createGame();
    });
});