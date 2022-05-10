package com.github.manevolent.ts3j.protocol.packet;

import com.github.manevolent.ts3j.protocol.PacketKind;

public enum PacketBodyType {
    VOICE(0x0, PacketBody0Voice.class, PacketKind.SPEECH, null, true, false, false, false, false),
    VOICE_WHISPER(0x1, PacketBody1VoiceWhisper.class, PacketKind.SPEECH, null, false, false, false, false, false),
    PONG(0x5, PacketBody5Pong.class, PacketKind.KEEPALIVE, null, false, false, false, false, false),
    PING(0x4, PacketBody4Ping.class, PacketKind.KEEPALIVE, PONG, false, false, false, false, false),
    ACK(0x6, PacketBody6Ack.class, PacketKind.CONTROL, null, true, true, false, false, false),
    ACK_LOW(0x7, PacketBody7AckLow.class, PacketKind.CONTROL, null, true, true, false, false, false),
    INIT1(0x8, PacketBody8Init1.class, PacketKind.CONTROL, null, false, true, false, false, false),
    COMMAND_LOW(0x3, PacketBody3CommandLow.class, PacketKind.CONTROL, ACK_LOW, true, true, true, true, true),
    COMMAND(0x2, PacketBody2Command.class, PacketKind.CONTROL, ACK, true, true, true, true, true);

    private final int index;
    private final Class<? extends PacketBody> packetClass;

    private final PacketKind kind;
    private final PacketBodyType acknolwedgedBy;
    private final boolean encrypted, resend, splittable, compressible, mustEncrypt;

    PacketBodyType(int index, Class<? extends PacketBody> packetClass,
                   PacketKind kind, PacketBodyType acknolwedgedBy,
                   boolean encrypted,
                   boolean resend,
                   boolean splittable,
                   boolean compressible,
                   boolean mustEncrypt) {
        this.index = index;
        this.packetClass = packetClass;
        this.kind = kind;
        this.acknolwedgedBy = acknolwedgedBy;
        this.resend = resend;
        this.encrypted = encrypted;
        this.splittable = splittable;
        this.compressible = compressible;
        this.mustEncrypt = mustEncrypt;
    }

    public int getIndex() {
        return index;
    }

    public static PacketBodyType fromId(int index) {
        for (PacketBodyType value : values())
            if (value.getIndex() == index) return value;

        throw new IllegalArgumentException("invalid index: " + index);
    }

    public Class<? extends PacketBody> getPacketClass() {
        return packetClass;
    }

    public boolean canEncrypt() {
        return encrypted;
    }

    public PacketBodyType getAcknolwedgedBy() {
        return acknolwedgedBy;
    }

    public boolean canResend() {
        return resend;
    }

    public boolean isSplittable() {
        return splittable;
    }

    public boolean isCompressible() {
        return compressible;
    }

    public boolean mustEncrypt() {
        return mustEncrypt;
    }

    public PacketKind getKind() {
        return kind;
    }
}
