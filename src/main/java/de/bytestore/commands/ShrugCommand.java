package de.bytestore.commands;

import de.bytestore.core.CacheHandler;
import de.bytestore.lib.Bot.Events.RoomEvent;
import de.bytestore.lib.Bot.Member;
import de.bytestore.lib.Callbacks.MemberCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ShrugCommand extends Command {
    @Override
    public void execute(RoomEvent eventIO, String[] argsIO) {
        try {
            CacheHandler.clientIO.sendText(eventIO.getRoom_id(), "¯\\_(ツ)_/¯", null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String usage() {
        return "shrug - Send a ¯\\_(ツ)_/¯.";
    }
}
