package de.bytestore.commands;

import de.bytestore.core.CacheHandler;
import de.bytestore.lib.Bot.Events.RoomEvent;

import java.io.IOException;

public class HelpCommand extends Command {
    @Override
    public void execute(RoomEvent eventIO, String[] argsIO) {
        // Store usage in Builder.
        StringBuilder builderIO = new StringBuilder();

        // Foreach Command one Usage.
        for (Command commandIO : CacheHandler.commandsIO.values()) {
            builderIO.append(CacheHandler.invokeIO + commandIO.usage() + "\n");
        }

        // Send Message into Room / to Client.
        try {
            CacheHandler.clientIO.sendText(eventIO.getRoom_id(), builderIO.toString(), null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String usage() {
        return "help - List all Commands.";
    }
}
