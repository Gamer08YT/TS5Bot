package de.bytestore.lib.Callbacks;

import de.bytestore.lib.Bot.Events.RoomEvent;

import java.io.IOException;

public interface RoomEventCallback {
    void onEventReceived(RoomEvent roomEvent) throws IOException;
}
