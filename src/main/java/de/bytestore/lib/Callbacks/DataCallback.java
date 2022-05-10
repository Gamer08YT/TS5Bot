package de.bytestore.lib.Callbacks;

import java.io.IOException;

public interface DataCallback {
    void onData(Object data) throws IOException;
}
