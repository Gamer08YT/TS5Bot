package de.bytestore.commands;

import de.bytestore.lib.Bot.Events.RoomEvent;

public class Command implements CommandInterface{
    @Override
    public void execute(RoomEvent eventIO, String[] argsIO) {
    }

    @Override
    public String usage() {
        return null;
    }
}
