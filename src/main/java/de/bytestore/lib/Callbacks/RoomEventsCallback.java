package de.bytestore.lib.Callbacks;

import de.bytestore.lib.Bot.Events.RoomEvent;

import java.io.IOException;
import java.util.List;

public interface RoomEventsCallback {
    void onEventReceived(List<RoomEvent> roomEvent) throws IOException;
}
