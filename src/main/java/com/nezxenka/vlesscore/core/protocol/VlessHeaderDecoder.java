package com.nezxenka.vlesscore.core.protocol;

import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VlessHeaderDecoder {

    private static final Logger log = LoggerFactory.getLogger(
        VlessHeaderDecoder.class
    );

    private VlessHeaderDecoder() {}

    public static VlessConnection decode(ByteBuf buf) {
        if (buf.readableBytes() < VlessConstants.MIN_HEADER_SIZE) {
            return null;
        }

        buf.markReaderIndex();

        try {
            VlessConnection conn = new VlessConnection();

            byte version = buf.readByte();
            if (version != VlessConstants.VERSION) {
                log.warn("Unsupported VLESS version: {}", version);
                buf.resetReaderIndex();
                return null;
            }
            conn.setVersion(version);

            byte[] uuid = new byte[VlessConstants.UUID_BYTES];
            buf.readBytes(uuid);
            conn.setUuid(uuid);
            conn.setAuthToken(conn.getUuidString());

            int addonsLen = buf.readByte() & 0xFF;
            if (addonsLen > 0) {
                if (buf.readableBytes() < addonsLen) {
                    buf.resetReaderIndex();
                    return null;
                }
                byte[] addons = new byte[addonsLen];
                buf.readBytes(addons);
                conn.setAddonsData(addons);
            }

            byte command = buf.readByte();
            conn.setCommand(command);

            if (
                command != VlessConstants.CMD_TCP &&
                command != VlessConstants.CMD_UDP &&
                command != VlessConstants.CMD_MUX
            ) {
                log.warn("Unknown VLESS command: {}", command);
                buf.resetReaderIndex();
                return null;
            }

            int port = buf.readUnsignedShort();
            conn.setPort(port);

            byte addrTypeByte = buf.readByte();
            AddressType addrType;
            try {
                addrType = AddressType.fromByte(addrTypeByte);
            } catch (IllegalArgumentException e) {
                log.warn("Unknown address type: {}", addrTypeByte);
                buf.resetReaderIndex();
                return null;
            }
            conn.setAddressType(addrType);

            String address = VlessAddressDecoder.readAddress(buf, addrType);
            if (address == null) {
                buf.resetReaderIndex();
                return null;
            }
            conn.setAddress(address);

            if (buf.readableBytes() > 0) {
                byte[] payload = new byte[buf.readableBytes()];
                buf.readBytes(payload);
                conn.setPayload(payload);
            } else {
                conn.setPayload(new byte[0]);
            }

            return conn;
        } catch (Exception e) {
            log.error("VLESS decode error", e);
            buf.resetReaderIndex();
            return null;
        }
    }
}
