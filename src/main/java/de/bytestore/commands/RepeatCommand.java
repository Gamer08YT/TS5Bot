package de.bytestore.commands;

import de.bytestore.core.CacheHandler;
import de.bytestore.lib.Bot.Events.RoomEvent;

import java.io.IOException;

public class RepeatCommand extends Command {

    @Override
    public void execute(RoomEvent eventIO, String[] argsIO) {
        // Store Args Message.
        StringBuilder builderIO = new StringBuilder();

        // For each Args.
        for (String lineIO : argsIO) {
            if (lineIO != argsIO[0])
                builderIO.append(lineIO + " ");
        }

        try {
            CacheHandler.clientIO.sendText(eventIO.getRoom_id(), builderIO.toString(), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String usage() {
        return "rp - Repeat your message.";
    }
}
