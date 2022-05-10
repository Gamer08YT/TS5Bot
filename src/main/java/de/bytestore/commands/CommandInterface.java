package de.bytestore.commands;

import de.bytestore.lib.Bot.Events.RoomEvent;

public interface CommandInterface {
    void execute(RoomEvent eventIO, String[] argsIO);

    String usage();
}
