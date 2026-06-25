package com.nezxenka.vlesscore.crypto;

import java.util.UUID;

public final class UuidConverter {

    private UuidConverter() {}

    public static String tokenToUuid(String token) {
        return UUID.nameUUIDFromBytes(token.getBytes()).toString();
    }
}
