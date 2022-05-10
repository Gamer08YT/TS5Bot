package com.github.manevolent.ts3j.protocol.packet.handler.client;

import com.github.manevolent.ts3j.command.Command;
import com.github.manevolent.ts3j.command.SingleCommand;
import com.github.manevolent.ts3j.command.parameter.CommandSingleParameter;
import com.github.manevolent.ts3j.protocol.Packet;
import com.github.manevolent.ts3j.protocol.ProtocolRole;
import com.github.manevolent.ts3j.protocol.TS3DNS;
import com.github.manevolent.ts3j.protocol.client.ClientConnectionState;
import com.github.manevolent.ts3j.protocol.packet.PacketBody2Command;
import com.github.manevolent.ts3j.protocol.packet.PacketBody8Init1;
import com.github.manevolent.ts3j.protocol.socket.client.LocalTeamspeakClientSocket;
import com.github.manevolent.ts3j.util.Ts3Crypt;
import com.github.manevolent.ts3j.util.Ts3Debugging;
import com.github.manevolent.ts3j.util.Pair;
import org.bouncycastle.math.ec.ECPoint;

import java.io.IOException;
import java.math.BigInteger;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class LocalClientHandlerConnecting extends LocalClientHandler {
    private static final byte[] INIT1_VERSION = new byte[]{0x0C, (byte)0xFF, (byte)0xD2, (byte)0xFE};

    private byte[] randomBytes;
    private byte[] alphaBytes;

    public LocalClientHandlerConnecting(LocalTeamspeakClientSocket client) {
        super(client);
    }

    @Override
    public void onAssigned() throws IOException, TimeoutException {
        PacketBody8Init1 packet = new PacketBody8Init1(ProtocolRole.CLIENT);

        // Initialize connection

        PacketBody8Init1.Step0 step = new PacketBody8Init1.Step0();
        Random random = new Random();
        randomBytes = new byte[4];
        random.nextBytes(randomBytes);
        step.setRandom(randomBytes);
        step.setTimestamp((int) ((System.currentTimeMillis() / 1000L)));
        packet.setStep(step);

        sendInit1(packet);

        //getClient().writePacket(new PacketBody2Command(ProtocolRole.CLIENT, createInitIv()));
    }

    private void sendInit1(PacketBody8Init1 packet) throws IOException, TimeoutException {
        packet.setVersion(INIT1_VERSION);

        getClient().writePacket(packet);
    }

    @Override
    public void handlePacket(Packet packet) throws IOException, TimeoutException {
        if (packet.getBody() instanceof PacketBody8Init1) {
            PacketBody8Init1 init1 = (PacketBody8Init1) packet.getBody();

            Ts3Debugging.debug("Handle Init1 step " + init1.getStep().getNumber());
            PacketBody8Init1.Step step;

            switch (init1.getStep().getNumber()) {
                case 1:
                    PacketBody8Init1.Step1 serverReplyStep1 = (PacketBody8Init1.Step1) init1.getStep();

                    // Check nonce.  It's received backwards, so walk backwards over the array received
                    for (int i = 0; i < 4; i++) {
                        if (randomBytes[3 - i] != serverReplyStep1.getA0reversed()[i]) {
                            Ts3Debugging.debug("[WARNING] random byte mismatch!");
                            break;
                        }
                    }

                    // Build response

                    PacketBody8Init1.Step2 step2 = new PacketBody8Init1.Step2();
                    step2.setA0reversed(serverReplyStep1.getA0reversed());
                    step2.setServerStuff(serverReplyStep1.getServerStuff());
                    step = step2;
                    break;
                case 3:
                    PacketBody8Init1.Step3 serverReplyStep3 = (PacketBody8Init1.Step3) init1.getStep();

                    // Calculate 'y'
                    // which is the result of x ^ (2 ^ level) % n as an unsigned
                    // BigInteger. Padded from the lower side with '0x00' when shorter
                    // than 64 bytes.
                    // CITE: https://github.com/Splamy/TS3AudioBot/blob/master/TS3Client/Full/Ts3Crypt.cs
                    // Prepare solution
                    if (serverReplyStep3.getLevel() < 0 || serverReplyStep3.getLevel() > 1_000_000)
                        throw new IllegalArgumentException("RSA challenge level is not within an acceptable range");

                    BigInteger x = new BigInteger(1, serverReplyStep3.getX());
                    BigInteger n = new BigInteger(1, serverReplyStep3.getN());

                    byte[] y = new byte[64];

                    byte[] solution =
                            x.modPow(BigInteger.valueOf(2L).pow(serverReplyStep3.getLevel()), n).toByteArray();

                    System.arraycopy(
                            solution, Math.abs(solution.length - 64),
                            y, solution.length < 64 ? 64 - solution.length : 0,
                            solution.length < 64 ? solution.length : 64
                    );

                    // Build response
                    PacketBody8Init1.Step4 step4 = new PacketBody8Init1.Step4();

                    step4.setLevel(serverReplyStep3.getLevel());
                    step4.setX(serverReplyStep3.getX());
                    step4.setN(serverReplyStep3.getN());
                    step4.setY(y);
                    step4.setServerStuff(serverReplyStep3.getServerStuff());

                    step4.setClientIVcommand(createInitIv().build().getBytes(Charset.forName("UTF8")));
                    step = step4;
                    break;
                case 127:
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    onAssigned();
                    return;
                default:
                    throw new IllegalArgumentException("unexpected Init1 server step: " + init1.getStep().getNumber());
            }

            sendInit1(new PacketBody8Init1(getClient().getRole().getOut(), step));
        } else if (packet.getBody() instanceof PacketBody2Command) {
            SingleCommand command = ((PacketBody2Command) packet.getBody()).parse().simplifyOne();

            Ts3Debugging.debug(command.build());

            if (command.getName().equalsIgnoreCase("initivexpand")) {
                byte[] alpha = Base64.getDecoder().decode(command.get("alpha").getValue()); // alpha
                byte[] beta = Base64.getDecoder().decode(command.get("beta").getValue()); // beta
                byte[] omega = Base64.getDecoder().decode(command.get("omega").getValue()); // omega

                getClient().setSecureParameters(Ts3Crypt.cryptoInit(alpha, beta, omega, getClient().getIdentity()));

                sendClientInit();

                getClient().setCommandProcessor(getClient());
                getClient().setState(ClientConnectionState.RETRIEVING_DATA);
            } else if (command.getName().equalsIgnoreCase("initivexpand2")) {
                // 3.2.2 initivexpand2 (Client <- Server)

                if (!command.get("ot").getValue().equals("1"))
                    throw new IllegalArgumentException("ot constant != 1: " + command.get("ot").getValue());

                byte[] license = Base64.getDecoder().decode(command.get("l").getValue());
                /*byte[] licsense_validation = !command.has("tvd") && command.get("tvd").getValue() != null ?
                        null :
                        Base64.getDecoder().decode(command.get("tvd").getValue());*/

                byte[] beta = Base64.getDecoder().decode(command.get("beta").getValue()); // beta
                byte[] omega = Base64.getDecoder().decode(command.get("omega").getValue()); // omega
                byte[] proof = Base64.getDecoder().decode(command.get("proof").getValue());

                Pair<byte[], byte[]> keyPair = Ts3Crypt.generateKeypair();

                // 3.2.1.1 Verify integrity
                // The proof parameter is the sign of the l parameter (not base64 encoded). The client can verify the l
                // parameter with the public key of the server which is sent in omega.

                ECPoint publicKey = Ts3Crypt.decodePublicKey(omega);

                if (!Ts3Crypt.verifySignature(publicKey, license, proof))
                    throw new SecurityException("invalid proof signature: " + Ts3Debugging.getHex(proof));

                byte[] signature = Ts3Crypt.generateClientEkProof(
                        keyPair.getKey(),
                        beta,
                        getClient().getIdentity()
                );

                getClient().writePacket(new PacketBody2Command(
                        ProtocolRole.CLIENT,
                        new SingleCommand(
                                "clientek", ProtocolRole.CLIENT,
                                new CommandSingleParameter("ek", Base64.getEncoder().encodeToString(keyPair.getKey())),
                                new CommandSingleParameter("proof", Base64.getEncoder().encodeToString(signature))
                        )
                ));

                getClient().setSecureParameters(Ts3Crypt.cryptoInit2(license, alphaBytes, beta, keyPair.getValue()));

                alphaBytes = null;

                sendClientInit();

                getClient().setCommandProcessor(getClient());
                getClient().setState(ClientConnectionState.RETRIEVING_DATA);
            } else if (command.getName().equals("error")) {
                getClient().setState(ClientConnectionState.DISCONNECTED);

                throw new IOException(command.get("msg").getValue());
            } else {
                throw new IOException("Unknown Init command: " + command.getName());
            }
        }
    }

    private SingleCommand createInitIv() {
        alphaBytes = new byte[10];
        new Random().nextBytes(alphaBytes);

        SingleCommand initiv = new SingleCommand("clientinitiv", ProtocolRole.CLIENT);
        initiv.add(new CommandSingleParameter("alpha", Base64.getEncoder().encodeToString(alphaBytes)));
        initiv.add(new CommandSingleParameter("omega", getClient().getIdentity().getPublicKeyString()));
        initiv.add(new CommandSingleParameter("ot", "1")); // constant, set to 1

        InetAddress address = getClient().getRemoteSocketAddress().getAddress();
        if (address != null)
            initiv.add(new CommandSingleParameter("ip", address.getHostAddress().substring(1)));

        return initiv;
    }

    private void sendClientInit() throws IOException, TimeoutException {
        Command clientinit = new SingleCommand(
                "clientinit",
                ProtocolRole.CLIENT,
                new CommandSingleParameter("client_nickname", getClient().getNickname()),
                new CommandSingleParameter(
                        "client_version",
                        getClient().getOption("client.version_string", String.class) != null ?
                                getClient().getOption("client.version_string", String.class) :
                                "3.?.? [Build: 5680278000]"
                ),
                new CommandSingleParameter("client_platform",
                        getClient().getOption("client.version_platform", String.class) != null ?
                                getClient().getOption("client.version_platform", String.class) :
                                "Windows"
                ),
                new CommandSingleParameter(
                        "client_version_sign",
                        getClient().getOption("client.version_sign", String.class) != null ?
                                getClient().getOption("client.version_sign", String.class) :
                                "DX5NIYLvfJEUjuIbCidnoeozxIDRRkpq3I9vVMBmE9L2qnekOoBzSenkzsg2lC9CMv8K5hkEzhr2TYUYSwUXCg=="
                ),
                new CommandSingleParameter("client_input_hardware", "1"),
                new CommandSingleParameter("client_output_hardware", "1"),
                new CommandSingleParameter("client_default_channel",
                        getClient().getOption("client.default_channel", String.class)
                ),
                new CommandSingleParameter("client_default_channel_password",
                        getClient().getOption("client.default_channel_password", String.class)
                ),
                new CommandSingleParameter("client_server_password",
                        getClient().getOption("client.server_password", String.class)
                ),
                new CommandSingleParameter("client_nickname_phonetic",
                        getClient().getOption("client.nickname_phonetic", String.class)
                ),
                new CommandSingleParameter("client_meta_data", ""),
                new CommandSingleParameter("client_default_token",
                        getClient().getOption("client.default_token", String.class)
                ),
                new CommandSingleParameter("client_key_offset",
                        Long.toString(getClient().getIdentity().getKeyOffset())
                ),
                new CommandSingleParameter(
                        "hwid",
                        getClient().getOption("client.hwid", String.class) != null ?
                                getClient().getOption("client.hwid", String.class) :
                                "+LyYqbDqOvEEpN5pdAbF8/v5kZ0="
                )
        );

        Ts3Debugging.debug(clientinit.build());

        getClient().writePacket(new PacketBody2Command(ProtocolRole.CLIENT, clientinit));
    }
}
