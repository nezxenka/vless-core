package com.nezxenka.vlesscore.core.protocol;

import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;

public final class VlessAddressDecoder {

    private VlessAddressDecoder() {}

    public static String readAddress(ByteBuf buf, AddressType type) {
        return switch (type) {
            case IPV4 -> {
                if (buf.readableBytes() < 4) yield null;
                int a = buf.readByte() & 0xFF;
                int b = buf.readByte() & 0xFF;
                int c = buf.readByte() & 0xFF;
                int d = buf.readByte() & 0xFF;
                yield a + "." + b + "." + c + "." + d;
            }
            case DOMAIN -> {
                if (buf.readableBytes() < 1) yield null;
                int domainLen = buf.readByte() & 0xFF;
                if (buf.readableBytes() < domainLen) yield null;
                byte[] domainBytes = new byte[domainLen];
                buf.readBytes(domainBytes);
                yield new String(domainBytes, StandardCharsets.US_ASCII);
            }
            case IPV6 -> {
                if (buf.readableBytes() < 16) yield null;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < 8; i++) {
                    if (i > 0) sb.append(':');
                    sb.append(String.format("%04x", buf.readUnsignedShort()));
                }
                yield sb.toString();
            }
        };
    }
}
