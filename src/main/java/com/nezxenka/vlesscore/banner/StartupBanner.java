package com.nezxenka.vlesscore.banner;

public final class StartupBanner {

    private static final String BANNER = """

        ╔══════════════════════════════════════════╗
        ║     __   __ _     _____ ____ ____        ║
        ║     \\ \\ / /| |   | ____/ ___/ ___|      ║
        ║      \\ V / | |   |  _| \\___ \\___ \\      ║
        ║       | |  | |___| |___ ___) |__) |     ║
        ║       |_|  |_____|_____|____/____/      ║
        ║                                          ║
        ║         VLESS Core Engine v%s         ║
        ║         + API + SOCKS5                  ║
        ╚══════════════════════════════════════════╝
        """;

    private StartupBanner() {}

    public static void print(String version) {
        System.out.println(BANNER.formatted(version));
    }
}
