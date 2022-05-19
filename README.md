# TS5Bot
Create a Chatbot for TeamSpeak5 Servers or Matrix Chatrooms.

For testing purposes only, it is not recommended to use the Bot in TeamSpeak Chats or TeamSpeak5 Servers. 
Because TeamSpeak can block the account for this.

## Set your Credentials.

Just complete following Strings in the CacheHandler Class:

```java
// Store Token of Bot (eq. Password not Session Token)
public static String tokenIO = "CHANGEME==";

// Store Username of Bot.
public static String userIO = "CHANGEME WITHOUT @ and :server";
```


For getting these Credentials you can use following Tool:

- [TeamSpeak5 Extractor](https://github.com/Gamer08YT/TS5Extractor)

## Available Commands
- bc (Broadcast)
- flip (Ascii Message Art)
- lenny (Ascii Message Art)
- shrug (Ascii Message Art)
- unflip (Ascii Message Art)
- gif (Random Gif)
- fake (Join TeamSpeak Server with Alpha Version)
- list (List Members in Group/Room)
- search (Search something in the TeamSpeak Forum)
