package de.bytestore.core;

import com.github.javafaker.Faker;
import com.github.manevolent.ts3j.api.Client;
import com.github.manevolent.ts3j.command.Command;
import com.github.manevolent.ts3j.command.CommandException;
import com.github.manevolent.ts3j.command.SingleCommand;
import com.github.manevolent.ts3j.command.parameter.CommandSingleParameter;
import com.github.manevolent.ts3j.event.ClientJoinEvent;
import com.github.manevolent.ts3j.event.ClientMovedEvent;
import com.github.manevolent.ts3j.event.TS3Listener;
import com.github.manevolent.ts3j.identity.LocalIdentity;
import com.github.manevolent.ts3j.protocol.ProtocolRole;
import com.github.manevolent.ts3j.protocol.packet.PacketBody2Command;
import com.github.manevolent.ts3j.protocol.socket.client.LocalTeamspeakClientSocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class FakeServer {
    // Store IP of Server.
    private String addressIO;

    // Store Name of Server.
    private String nameIO;

    // Store uuid of Client.
    private String uuidIO = "mj0lDaHd5oTPZK+ZcbO3gk79WcI=";

    // Store TeamSpeak Client.
    private LocalTeamspeakClientSocket socketIO;

    public FakeServer(String addressIO, String nameIO) {
        this.addressIO = addressIO;
        this.nameIO = nameIO;
    }

    public String getAddress() {
        return addressIO;
    }

    public void setAddress(String addressIO) {
        this.addressIO = addressIO;
    }

    public String getName() {
        return nameIO;
    }

    public void setName(String nameIO) {
        this.nameIO = nameIO;
    }

    public String getUUID() {
        return this.uuidIO;
    }

    public void connect() {
        this.socketIO = new LocalTeamspeakClientSocket();

        try {
            // Enable Debugging.
            //Ts3Debugging.setEnabled(true);

            // Enable Multi Threading.
            this.socketIO.setEventMultiThreading(true);

            // Generate new Identity.
            this.socketIO.setIdentity(LocalIdentity.generateNew(15));

            // Fake HWID.
            this.socketIO.setHWID("TestTestTest");

            // Set Fake Client Version.
            this.socketIO.setClientVersion("Windows", "5.0.0-beta70-rc2 [Build: 1651730595]", "xbNW9T0AlIt9LyYHO9C8HIORDJqD5Z6K4f5YvTp1hTVJK2NesjX4BDXfHZHzC7cqUd4qBgagVg+C9gSRFDJHBQ==");

            // Set custom MyTS Data.
            this.socketIO.setOption("client_meta_data", ("{\"myts_token\":\"CkA4KTbrqfljG9QcXvQp23xGvbe8VLgZ2qAi5n95/0kUJHqQm7aGF+rZbACAGqhuP/HK5DEWBCugrTgH7eTwfSgPEnABAPv6QAZa4Z0kMfn3BCX60CCLzvljKzmWTPm1w3G4QwouAA2hsoATRU0AAAAAQlRlYW1TcGVhayBTeXN0ZW1zIEdtYkgAABc/o5rMvXLyKgtQcGF3OkQRJBHXBVzf1CAyBqMfJqe/BhGNcNoRtWgnGO+4v5MGImEKHkphWG5Qcml2YXRlQGNoYXQudGVhbXNwZWFrLmNvbQobSmFYblByaXZhdGVAbXl0ZWFtc3BlYWsuY29tCiJKYVhuUHJpdmF0ZUB0c2NoYXQtMS50ZWFtc3BlYWsuY29t\",\"tag\":\"JaXnPrivate@myteamspeak.com\"}"));
            this.socketIO.setOption("client_myteamspeak_id", "ARMw3vrsmG/+Bxy/YWsJpCf62hIrYHV0vRfOohgzWeBb");
            this.socketIO.setOption("client_signed_badges", "2bf80270-8efe-46dc-a472-3280a0479145,c2368518-3728-4260-bcd1-8b85e9f8984c,b78a0f3e-8758-4572-b102-42a79b4a0342");

            // Set Fake Username.
            this.socketIO.setNickname(new Faker().name().firstName());

            // Set Fake Description.
            //this.socketIO.setDescription(new Faker().book().genre());

            String urlIO[] = this.addressIO.split(":");

            System.out.println(urlIO.length);

            // Connect to Server.
            this.socketIO.connect(new InetSocketAddress((urlIO.length == 1 ? this.addressIO : urlIO[0]), (urlIO.length == 1 ? 9987 : Integer.valueOf(urlIO[1]))), null, 10000L);

            // Reinit Client.
            Command initIO = new SingleCommand(
                    "clientinit",
                    ProtocolRole.CLIENT,
                    new CommandSingleParameter("client_meta_data", CacheHandler.chatIO));

            // Write to Server.
            this.socketIO.writePacket(new PacketBody2Command(ProtocolRole.CLIENT, initIO));


            System.out.println("Connected to " + this.addressIO + ".");

            // Subscribe to all Channels.
            this.socketIO.subscribeAll();

            this.socketIO.addListener(new TS3Listener() {
                @Override
                public void onClientJoin(ClientJoinEvent e) {
                    try {
                        System.out.println(e.getClientFromId());
                        System.out.println(e.getClientNickname());
                        System.out.println(socketIO.getClientInfo(e.getClientId()).toString());
                    } catch (IOException | TimeoutException | InterruptedException | CommandException ex) {
                        System.out.println(ex.getMessage());
                    }
                }

                @Override
                public void onClientMoved(ClientMovedEvent e) {
                    try {
                        System.out.println(e.getClientId());
                        System.out.println(socketIO.getClientInfo(e.getClientId()).toString());
                        socketIO.setOption("client_signed_badges", "2bf80270-8efe-46dc-a472-3280a0479145,c2368518-3728-4260-bcd1-8b85e9f8984c,b78a0f3e-8758-4572-b102-42a79b4a0342");
                    } catch (IOException | TimeoutException | InterruptedException | CommandException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            });

            // List Clients in current Channel.
            for (Client clientIO : this.socketIO.listChannelClients(this.socketIO.getClientInfo(this.socketIO.getClientId()).getChannelId())) {
                System.out.println("NEW CLIENT " + clientIO.getNickname());
                // System.out.println(socketIO.getClientInfo(clientIO.getId()).toString());
                System.out.println(clientIO.toString());
            }
        } catch (TimeoutException | IOException | GeneralSecurityException | InterruptedException | CommandException |
                 ExecutionException e) {
            System.out.println(e.getMessage());
        }
    }

}
