package de.bytestore.core;

import de.bytestore.commands.*;
import de.bytestore.lib.Bot.Client;
import de.bytestore.lib.Bot.LoginData;
import de.bytestore.lib.Callbacks.LoginCallback;
import de.bytestore.listener.CommandListener;
import de.bytestore.listener.FriendListener;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BotHandler {
    private static ExecutorService poolIO = Executors.newFixedThreadPool(10);

    public static void start() {
        BotHandler.login();
        BotHandler.commands();
    }

    public static void login() {
        // Create new Client for TS Matrix Reverse Proxy.
        //CacheHandler.clientIO = new Client("https://beta.voice.teamspeak.com");
        CacheHandler.clientIO = new Client("https://chat.teamspeak.com");

        try {
            CacheHandler.clientIO.login(CacheHandler.userIO, CacheHandler.tokenIO, new LoginCallback() {
                @Override
                public void onResponse(LoginData data) throws IOException {
                    System.out.println(data.getUser_id());

                    // Set Debug Preference.
                    String preferenceIO = "{\"matrix\":{\"status_msg\":\"Starting system...\"},\"teamspeak\":{\"server_ip\":\"\",\"server_name\":\"\"},\"presence\":0,\"status_msg\":\"www.byte-store.de\",\"servers\": []}";
                    CacheHandler.clientIO.setPresence("0", preferenceIO, null);

                    //CacheHandler.serversIO.add(new FakeServer("alpha.voice.teamspeak.com", "Official TeamSpeak Alpha Server"));
                    //CacheHandler.serversIO.add(new FakeServer("beta.voice.teamspeak.com", "Official TeamSpeak Beta Server"));
                    //CacheHandler.serversIO.add(new FakeServer("voice.teamspeak.com", "Official TeamSpeak Public Test Server"));
                    CacheHandler.serversIO.add(new FakeServer("byte-store.de", "Official TeamSpeak Public Test Server"));

                    // Join Banned Room.
                    CacheHandler.clientIO.joinRoom("#beta.de:chat.teamspeak.com", null);
                    //System.out.println("{\"matrix\":{\"status_msg\":\"www.byte-store.de\"},\"teamspeak\":{\"server_ip\":\"\",\"server_name\":\"\"},\"presence\":2,\"status_msg\":\"www.byte-store.de\",\"servers\":" + arrayIO.toString() + "}");

                    // Rebuild Fake Server.
                    FakeCommand.rebuild();

                    for (FakeServer serverIO : CacheHandler.serversIO) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                serverIO.connect();
                            }
                        }).start();
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Register Room Listener.
        CacheHandler.clientIO.registerRoomEventListener(new FriendListener());
        CacheHandler.clientIO.registerRoomEventListener(new CommandListener());
    }

    public static void commands() {
        CacheHandler.commandsIO.put("help", new HelpCommand());
        CacheHandler.commandsIO.put("gif", new GIFCommand());
        CacheHandler.commandsIO.put("list", new ListCommand());
        CacheHandler.commandsIO.put("shrug", new ShrugCommand());
        CacheHandler.commandsIO.put("flip", new FlipCommand());
        CacheHandler.commandsIO.put("unflip", new UnflipCommand());
        CacheHandler.commandsIO.put("lenny", new LennyCommand());
        CacheHandler.commandsIO.put("join", new FakeCommand());
        CacheHandler.commandsIO.put("rp", new RepeatCommand());
        CacheHandler.commandsIO.put("search", new SearchCommand());
    }
}
