package com.nezxenka.vlesscore.core.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class VlessPacket {

    private final int port;
    private final AddressType addressType;
    private final String address;
    private final ByteBuf payload;

    public VlessPacket(
        int port,
        AddressType addressType,
        String address,
        ByteBuf payload
    ) {
        this.port = port;
        this.addressType = addressType;
        this.address = address;
        this.payload = payload;
    }

    public static VlessPacket fromConnection(VlessConnection conn) {
        ByteBuf payload =
            conn.getPayload() != null && conn.getPayload().length > 0
                ? Unpooled.wrappedBuffer(conn.getPayload())
                : Unpooled.EMPTY_BUFFER;
        return new VlessPacket(
            conn.getPort(),
            conn.getAddressType(),
            conn.getAddress(),
            payload
        );
    }

    public int getPort() {
        return port;
    }

    public AddressType getAddressType() {
        return addressType;
    }

    public String getAddress() {
        return address;
    }

    public ByteBuf getPayload() {
        return payload;
    }

    public String getDestination() {
        return address + ":" + port;
    }

    public void release() {
        if (payload != null && payload.refCnt() > 0) {
            payload.release();
        }
    }
}
