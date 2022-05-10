package de.bytestore.commands;

import de.bytestore.core.CacheHandler;
import de.bytestore.lib.Bot.Events.RoomEvent;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class GIFCommand extends Command {
    public static ArrayList<String> cooldownIO = new ArrayList<String>();

    @Override
    public void execute(RoomEvent eventIO, String[] argsIO) {
        try {
            if (!GIFCommand.cooldownIO.contains(eventIO.getSender())) {
                // Set Client into Cooldown.
                GIFCommand.cooldownIO.add(eventIO.getSender());

                Thread threadIO = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // Generate new HTTP Request.
                            CloseableHttpClient clientIO = HttpClients.createDefault();
                            HttpGet getIO = new HttpGet("https://api.giphy.com/v1/gifs/random?api_key=Y4kEFmocFv9Jymki2QwBdy7FER37hkpW&tag=&rating=g");
                            CloseableHttpResponse responseIO = clientIO.execute(getIO);

                            // Convert Response to JsonObject.
                            JSONObject objectIO = new JSONObject(IOUtils.toString(responseIO.getEntity().getContent(), Charset.defaultCharset()));

                            // Send URL of GIF into Chat.
                            CacheHandler.clientIO.sendText(eventIO.getRoom_id(), objectIO.getJSONObject("data").getJSONObject("images").getJSONObject("preview").getString("mp4"), null);

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                // Run Thread for GIF.
                threadIO.start();

                // Remove Client from Cooldown.
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        GIFCommand.cooldownIO.remove(eventIO.getSender());
                    }
                }, CacheHandler.cooldownIO);
            } else
                CacheHandler.clientIO.sendText(eventIO.getRoom_id(), ":clock1: You're currently in a cooldown.", null);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String usage() {
        return "gif - Send Random GIF in Room.";
    }
}
