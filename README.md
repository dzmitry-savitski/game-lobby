# Game lobby prototype
This is simple prototype of game lobby site. A “lobby” in games jargon, is a place where players can see one another and invite one another to a game session. This task was given to me as an assignment by one of upwork.com clients.

## Main features:
 - user login and logout
 - user list updated dynamically in real time
 - thread safe (can be opened from different tabs in browser / different browsers)
 - real-time chat for all users
 - real-time list of games, whis can be modified by users

## Used technologies:
 - Java 7, WebSocket API, JSON API - for server side
 - HTML, CSS, JQuery, Bootstrap - for clients
 - JUnit4, Embedded Jetty - for tests

## Requirements:
- Apache tomcat 7 for server part of application
- Browser, supporting web sockets for runnning clients

## Getting started:
 1. Simply deploy war file to tomcat
 2. Correct first line of ```api.js``` to set up your server ip.

## API description:
#### Messages format:
All messages are JSON objects in format:
```
Message{type=TYPE, message=MESSAGE}
```

Message types can be:
```
LOGIN, USERS, CHAT, GAMES, CREATE_GAME, START_GAME
```

```MESSAGE``` can be string or JSON array of strings

#### Connecting to socket:
Connecting peforms automatically, when user opens new tab with client page. If server is unreachable error message will be shown on the top of the page.

#### User login:
User sends to server message of type ```LOGIN``` with wanted login as String in message. For example:
```
Message{type=LOGIN, message='login1'}
```
If everything is ok, server replies with the sanme message.
If user sends empty login, or such login already in use - client receives message of type ```LOGIN``` with empty message field, like this one:
```
Message{type=LOGIN, message=''}
```
This message considered as error by client. User will see box with error message on the top of the page.

#### Updating user list:
All users receive message of type ```USERS``` with JSON array of users after any users succesfully login or logout.
Example:
```
Message{type=USERS, message='["login2","login1"]'}
```
#### User logout:
There is no special message for logout. User can simply leave page and server will automatically sent to all active clients updated user list.

#### Chat message:
User sent to server message of type ```CHAT``` and server will automatically resend it to all active clients. Example:
```
Message{type=CHAT, message='Hello world!'}
```
#### Creating game:
Any user can create game, sending message of type ```CREATE_GAME``` with game name as message. For example:
```
Message{type=CREATE_GAME, message='My game'}
```
Game will be stored on server and all users will receive update game list message.

### Starting game:
Any user can start game, sending message of type ```START_GAME``` with game name as message. For example:
```
Message{type=CREATE_GAME, message='My game'}
```
Game will be deleted from server and all users will receive update game list message.

#### Updating game:
All users will receive message of type ```GAMES``` with actual list of games (as JSON array of strings) in case of any changes in game storage on server.
After logging in user also receive message with actual game list.
Game list will be empty if there are no games.
For example:
```
Message{type=GAMES, message='["game1","game2"]'}
```
Games are created and started regardless presenting online creator of games. It means, that user can create game and logout, but game will still be available for other users.

## Some screenshots:
##### Login page
[![Login page](https://docs.google.com/uc?id=0B9dr_t3FnLQ4cTBKVElPektoNTA)](https://docs.google.com/uc?id=0B9dr_t3FnLQ4cTBKVElPektoNTA)
##### Connection error
[![Connection error](https://docs.google.com/uc?id=0B9dr_t3FnLQ4UmdiTjBLOVN2OGs)](https://docs.google.com/uc?id=0B9dr_t3FnLQ4UmdiTjBLOVN2OGs)
##### Main page
[![Main page](https://docs.google.com/uc?id=0B9dr_t3FnLQ4ckdCb0M4U0E4Qmc)](https://docs.google.com/uc?id=0B9dr_t3FnLQ4ckdCb0M4U0E4Qmc)