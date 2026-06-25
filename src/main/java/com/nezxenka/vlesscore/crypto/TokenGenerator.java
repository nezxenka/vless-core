package com.nezxenka.vlesscore.crypto;

import java.security.SecureRandom;
import java.util.UUID;

public final class TokenGenerator {

    private static final String TOKEN_PREFIX = "vpn_";
    private static final int TOKEN_RANDOM_LENGTH = 32;
    private static final String TOKEN_CHARS =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private TokenGenerator() {}

    public static String generateToken() {
        StringBuilder sb = new StringBuilder(TOKEN_PREFIX);
        for (int i = 0; i < TOKEN_RANDOM_LENGTH; i++) {
            sb.append(
                TOKEN_CHARS.charAt(SECURE_RANDOM.nextInt(TOKEN_CHARS.length()))
            );
        }
        return sb.toString();
    }
}
