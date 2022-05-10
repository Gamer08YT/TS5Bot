package de.bytestore.commands;

import de.bytestore.core.CacheHandler;
import de.bytestore.core.FakeServer;
import de.bytestore.lib.Bot.Events.RoomEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class FakeCommand extends Command {

    @Override
    public void execute(RoomEvent eventIO, String[] argsIO) {
        if (argsIO.length > 2) {
            try {
                CacheHandler.clientIO.sendText(eventIO.getRoom_id(), "Joined on " + (CacheHandler.serversIO.size() + 1) + " Fake Servers, trying to join " + argsIO[1] + ".", null);

                // StringBuilder for Name.
                StringBuilder nameIO = new StringBuilder();

                // Foreach Line in Args.
                for (String lineIO : argsIO) {
                    if (!lineIO.equals(argsIO[0]) && !lineIO.equals(argsIO[1])) {
                        // Append Spacer after Args.
                        nameIO.append(lineIO).append(" ");
                    }
                }

                // Add new Fakeserver.
                FakeServer serverIO = new FakeServer(argsIO[2], nameIO.toString());
                CacheHandler.serversIO.add(serverIO);

                // Connect to Fakeserver.
                serverIO.connect();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            this.rebuild();
        } else {
            try {
                CacheHandler.clientIO.sendText(eventIO.getRoom_id(), "You forgot your Fakeserver with Servername and Address.", null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void rebuild() {
        // Store JSON Servers.
        JSONArray arrayIO = new JSONArray();

        // Foreach Fake Server.
        for (FakeServer fakeIO : CacheHandler.serversIO) {
            arrayIO.put(new JSONObject().put("address", fakeIO.getAddress()).put("name", fakeIO.getName()).put("uuid", fakeIO.getUUID()));
        }

        // Set ready Presence.
        // {"matrix":{"status_msg":""},"teamspeak":{"server_ip":"","server_name":""},"presence":0,"status_msg":"","servers":[{"address":"85.214.126.187","name":"Public Teamspeak || Free channel","uuid":"aWgSIBQgLOqopw0FGGlT2zWyT/o="},{"address":"beta.voice.teamspeak.com","name":"Official TeamSpeak Beta Server","uuid":"mj0lDaHd5oTPZK+ZcbO3gk79WcI="}]}
        try {
            CacheHandler.clientIO.setPresence("online", "{\"matrix\":{\"status_msg\":\"www.byte-store.de\"},\"teamspeak\":{\"server_ip\":\"\",\"server_name\":\"\"},\"presence\":0,\"status_msg\":\"www.byte-store.de\",\"servers\": " + arrayIO.toString() + "}", null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String usage() {
        return "fake - Join a Fake Server.";
    }
}
