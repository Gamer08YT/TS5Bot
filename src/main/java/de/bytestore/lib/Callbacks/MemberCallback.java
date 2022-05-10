package de.bytestore.lib.Callbacks;

import de.bytestore.lib.Bot.Member;

import java.io.IOException;
import java.util.List;

public interface MemberCallback {
    void onResponse(List<Member> roomMember) throws IOException;
}
