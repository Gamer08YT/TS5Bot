package de.bytestore.core;

import de.bytestore.commands.Command;
import de.bytestore.lib.Bot.Client;

import java.util.ArrayList;
import java.util.HashMap;

public class CacheHandler {
    // Store Client Object of Bot.
    public static Client clientIO;

    // Store Invoke of Bot.
    public static String invokeIO = "$";

    // Store Commands of Bot.
    public static HashMap<String, Command> commandsIO = new HashMap<String, Command>();

    // Store Cooldown Time of Bot.
    public static Integer cooldownIO = 60;

    // Store Fake Servers of Bot.
    public static ArrayList<FakeServer> serversIO = new ArrayList<FakeServer>();

    // Maximal Limit of Forum Posts.
    public static int postsIO = 0;

    public static String chatIO = "{\"myts_token\":\"CkA4KTbrqfljG9QcXvQp23xGvbe8VLgZ2qAi5n95/0kUJHqQm7aGF+rZbACAGqhuP/HK5DEWBCugrTgH7eTwfSgPEnABAPv6QAZa4Z0kMfn3BCX60CCLzvljKzmWTPm1w3G4QwouAA2hsoATRU0AAAAAQlRlYW1TcGVhayBTeXN0ZW1zIEdtYkgAABc/o5rMvXLyKgtQcGF3OkQRJBHXBVzf1CAyBqMfJqe/BhGNcNoRtWgnGO+4v5MGImEKHkphWG5Qcml2YXRlQGNoYXQudGVhbXNwZWFrLmNvbQobSmFYblByaXZhdGVAbXl0ZWFtc3BlYWsuY29tCiJKYVhuUHJpdmF0ZUB0c2NoYXQtMS50ZWFtc3BlYWsuY29t\",\"tag\":\"JaXnPrivate@myteamspeak.com\"}";
    // Store Token of Bot (eq. Password not Session Token)
    public static String tokenIO = "CHANGEME==";

    // Store Username of Bot.
    public static String userIO = "CHANGEME WITHOUT @ and :server";
}
