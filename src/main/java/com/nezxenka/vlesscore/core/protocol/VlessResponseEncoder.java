package com.nezxenka.vlesscore.core.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public final class VlessResponseEncoder {

    private VlessResponseEncoder() {}

    public static ByteBuf encodeResponseHeader() {
        return Unpooled.wrappedBuffer(VlessConstants.RESPONSE_HEADER);
    }

    public static ByteBuf encodeResponse(byte[] data) {
        ByteBuf buf = Unpooled.buffer(2 + data.length);
        buf.writeBytes(VlessConstants.RESPONSE_HEADER);
        buf.writeBytes(data);
        return buf;
    }
}
