package com.nezxenka.vlesscore.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class NetworkUtils {

    private NetworkUtils() {}

    public static boolean isValidHost(String host) {
        try {
            InetAddress.getByName(host);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    public static boolean isValidPort(int port) {
        return port > 0 && port <= 65535;
    }

    public static String resolveHostname(String host) {
        try {
            return InetAddress.getByName(host).getHostAddress();
        } catch (UnknownHostException e) {
            return host;
        }
    }
}
