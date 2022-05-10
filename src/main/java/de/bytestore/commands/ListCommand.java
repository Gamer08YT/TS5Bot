package de.bytestore.commands;

import de.bytestore.core.CacheHandler;
import de.bytestore.lib.Bot.Events.RoomEvent;
import de.bytestore.lib.Bot.Member;
import de.bytestore.lib.Callbacks.MemberCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ListCommand extends Command {
    public static ArrayList<String> cooldownIO = new ArrayList<String>();

    @Override
    public void execute(RoomEvent eventIO, String[] argsIO) {
        try {
            if (!cooldownIO.contains(eventIO.getSender())) {
                // Store members in Builder.
                StringBuilder builderIO = new StringBuilder();

                // Add Title to Message.
                builderIO.append("Found following Users in Room:\n");

                // Get Members from Room.
                CacheHandler.clientIO.getRoomMembers(eventIO.getRoom_id(), new MemberCallback() {
                    @Override
                    public void onResponse(List<Member> roomIO) throws IOException {
                        // Show max Player from Room.
                        int maxIO = 10;

                        for (Member memberIO : roomIO) {
                            if (maxIO > 0) {
                                builderIO.append((memberIO.getDisplay_name().startsWith("TS_") ? ":warning: " : "") + memberIO.getDisplay_name() + (memberIO.getDisplay_name().startsWith("TS_") ? ":warning: " : "") + "\n");
                            }

                            maxIO--;
                        }

                        // Print more Message on more Clients.
                        if (roomIO.size() > 10) {
                            builderIO.append("\nAnd " + (roomIO.size() - 10) + " other People.");
                        }

                        // Set Client into Cooldown.
                        ListCommand.cooldownIO.add(eventIO.getSender());

                        // Remove Client from Cooldown.
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                ListCommand.cooldownIO.remove(eventIO.getSender());
                            }
                        }, CacheHandler.cooldownIO);


                        // Send Message into Room / to Client.
                        CacheHandler.clientIO.sendText(eventIO.getRoom_id(), builderIO.toString(), null);
                    }
                });
            } else
                CacheHandler.clientIO.sendText(eventIO.getRoom_id(), ":clock1: You're currently in a cooldown.", null);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String usage() {
        return "list - List Clients in Room.";
    }
}
