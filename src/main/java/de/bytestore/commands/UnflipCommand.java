package de.bytestore.commands;

import de.bytestore.core.CacheHandler;
import de.bytestore.lib.Bot.Events.RoomEvent;

import java.io.IOException;

public class UnflipCommand extends Command {
    @Override
    public void execute(RoomEvent eventIO, String[] argsIO) {
        try {
            CacheHandler.clientIO.sendText(eventIO.getRoom_id(), "┬─┬ ノ( ゜-゜ノ)", null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String usage() {
        return "unflip - Send a ┬─┬ ノ( ゜-゜ノ).";
    }
}
