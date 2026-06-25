package com.nezxenka.vlesscore.core.protocol;

public final class VlessUuidUtil {

    private VlessUuidUtil() {}

    public static String uuidToString(byte[] uuid) {
        if (uuid == null || uuid.length != VlessConstants.UUID_BYTES) {
            return "invalid-uuid";
        }
        StringBuilder sb = new StringBuilder(36);
        for (int i = 0; i < 16; i++) {
            sb.append(String.format("%02x", uuid[i]));
            if (i == 3 || i == 5 || i == 7 || i == 9) sb.append('-');
        }
        return sb.toString();
    }
}
