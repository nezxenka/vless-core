package com.nezxenka.vlesscore.core.protocol;

public enum AddressType {
    IPV4((byte) 1, 4),
    DOMAIN((byte) 2, -1),
    IPV6((byte) 3, 16);

    private final byte value;
    private final int fixedLength;

    AddressType(byte value, int fixedLength) {
        this.value = value;
        this.fixedLength = fixedLength;
    }

    public byte getValue() {
        return value;
    }

    public int getFixedLength() {
        return fixedLength;
    }

    public static AddressType fromByte(byte b) {
        return switch (b) {
            case 1 -> IPV4;
            case 2 -> DOMAIN;
            case 3 -> IPV6;
            default -> throw new IllegalArgumentException(
                "Unknown address type: " + b
            );
        };
    }
}
