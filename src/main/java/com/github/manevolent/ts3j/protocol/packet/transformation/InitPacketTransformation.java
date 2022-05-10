package com.github.manevolent.ts3j.protocol.packet.transformation;


import com.github.manevolent.ts3j.protocol.header.PacketHeader;
import com.github.manevolent.ts3j.util.Pair;

public class InitPacketTransformation extends PacketTransformation {
    private static final byte[] key =
            {0x63, 0x3A, 0x5C, 0x77, 0x69, 0x6E, 0x64, 0x6F, 0x77, 0x73, 0x5C, 0x73, 0x79, 0x73, 0x74, 0x65};

    private static final byte[] nonce =
            {0x6D, 0x5C, 0x66, 0x69, 0x72, 0x65, 0x77, 0x61, 0x6C, 0x6C, 0x33, 0x32, 0x2E, 0x63, 0x70, 0x6C};

    public InitPacketTransformation() {
        super(key, new byte[8]);
    }

    @Override
    public Pair<byte[], byte[]> computeParameters(PacketHeader header) {
        return new Pair<>(key, nonce);
    }
}
