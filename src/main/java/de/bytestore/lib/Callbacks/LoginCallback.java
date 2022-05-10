package de.bytestore.lib.Callbacks;

import de.bytestore.lib.Bot.LoginData;

import java.io.IOException;

public interface LoginCallback {
    void onResponse(LoginData data) throws IOException;
}
