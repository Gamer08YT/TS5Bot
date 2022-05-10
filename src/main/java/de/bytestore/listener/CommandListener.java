package de.bytestore.listener;

import de.bytestore.core.CacheHandler;
import de.bytestore.lib.Bot.Events.RoomEvent;
import de.bytestore.lib.Callbacks.RoomEventsCallback;

import java.io.IOException;
import java.util.List;

public class CommandListener implements RoomEventsCallback {

    @Override
    public void onEventReceived(List<RoomEvent> roomEvent) throws IOException {
        for (RoomEvent eventIO : roomEvent) {
                // Parse Command from Message.
                if (eventIO.getType().equals("m.room.message") && eventIO.getContent().has("body") && !eventIO.getSender().equals(CacheHandler.clientIO.getLoginData().getUser_id())) {
                    // Store Message as String.
                    String messageIO = eventIO.getContent().getString("body");

                    // Check if Command starts with Invoke.
                    if (messageIO.startsWith(CacheHandler.invokeIO)) {
                        // Split Empty Lines as Args.
                        String[] argsIO = messageIO.split(" ");

                        if (argsIO[0] != null) {
                            // Get Command from Args.
                            String commandIO = argsIO[0].replace(CacheHandler.invokeIO, "");

                            // Check if Command exits.
                            if (CacheHandler.commandsIO.containsKey(commandIO)) {
                                // Execute Command.
                                CacheHandler.commandsIO.get(commandIO).execute(eventIO, argsIO);
                            } else
                                CacheHandler.clientIO.sendText(eventIO.getRoom_id(), "Command " + commandIO + " not found.", null);
                        }

                    }
            }
        }
    }
}
