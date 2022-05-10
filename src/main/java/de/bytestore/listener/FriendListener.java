package de.bytestore.listener;

import de.bytestore.core.CacheHandler;
import de.bytestore.lib.Bot.Events.RoomEvent;
import de.bytestore.lib.Callbacks.RoomEventsCallback;

import java.io.IOException;
import java.util.List;

public class FriendListener implements RoomEventsCallback {

    @Override
    public void onEventReceived(List<RoomEvent> roomEvent) throws IOException {
        for (RoomEvent eventIO : roomEvent) {
            // System.out.println(eventIO.getRaw());

            if (eventIO.getType().equals("m.room.join_rules")) {
                // On Friend Request.
                if (eventIO.getContent().getString("join_rule").equals("invite")) {
                    // Print Debug Message.
                    System.out.println("Received new Friend Request from " + eventIO.getSender() + ".");

                    // Join Private Room.
                    CacheHandler.clientIO.joinRoom(eventIO.getRoom_id(), null);

                    // Send Debug Message.
                    CacheHandler.clientIO.sendText(eventIO.getRoom_id(), "Hello nice to meet you :hand:", null);
                }

                // On Room Request.
                if (eventIO.getContent().getString("join_rule").equals("public")) {
                    // Join Public Room.
                    CacheHandler.clientIO.joinRoom(eventIO.getRoom_id(), null);

                    // Send Debug Message.
                    CacheHandler.clientIO.sendText(eventIO.getRoom_id(), "Hello i'm new in this Group :hand:", null);
                    CacheHandler.clientIO.sendText(eventIO.getSender(), "Joining Public Room with ID " + eventIO.getRoom_id() + ".", null);
                }
            }

            // On Room Leave.
            if (eventIO.getType().equals("m.room.member") && eventIO.getContent().getString("membership").equals("leave")) {
                // Print Debug Message.
                System.out.println("Received new Room Leave Event from " + eventIO.getSender() + ".");

                // Send Debug Message.
                CacheHandler.clientIO.sendText(eventIO.getRoom_id(), "Goodbye my friend :door:", null);
            }
        }
    }
}
