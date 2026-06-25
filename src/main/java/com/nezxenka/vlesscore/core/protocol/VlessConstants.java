package com.nezxenka.vlesscore.core.protocol;

public final class VlessConstants {

    public static final byte VERSION = 0;

    public static final byte CMD_TCP = 1;
    public static final byte CMD_UDP = 2;
    public static final byte CMD_MUX = 3;

    public static final int MIN_HEADER_SIZE = 22;
    public static final int UUID_BYTES = 16;

    public static final byte[] RESPONSE_HEADER = new byte[] { VERSION, 0 };

    private VlessConstants() {}
}
