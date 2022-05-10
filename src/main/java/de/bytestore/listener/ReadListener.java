package de.bytestore.listener;

import de.bytestore.core.CacheHandler;
import de.bytestore.lib.Bot.Events.RoomEvent;
import de.bytestore.lib.Callbacks.RoomEventsCallback;

import java.io.IOException;
import java.util.List;

public class ReadListener implements RoomEventsCallback {

    @Override
    public void onEventReceived(List<RoomEvent> roomEvent) throws IOException {
        for (RoomEvent eventIO : roomEvent) {
            // Set Read Status.
            CacheHandler.clientIO.sendReadReceipt(eventIO.getRoom_id(), eventIO.getEvent_id(), "m.fully_read", null);
        }
    }
}
