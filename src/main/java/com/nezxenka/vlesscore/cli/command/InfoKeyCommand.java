package com.nezxenka.vlesscore.cli.command;

import com.nezxenka.vlesscore.cli.ConsoleWriter;
import com.nezxenka.vlesscore.config.AppConfig;
import com.nezxenka.vlesscore.crypto.UuidConverter;
import com.nezxenka.vlesscore.database.model.Token;
import com.nezxenka.vlesscore.service.TokenManagementService;

public class InfoKeyCommand implements Command {

    private final TokenManagementService tokenService;
    private final AppConfig config;
    private final ConsoleWriter console;

    public InfoKeyCommand(
        TokenManagementService tokenService,
        AppConfig config,
        ConsoleWriter console
    ) {
        this.tokenService = tokenService;
        this.config = config;
        this.console = console;
    }

    @Override
    public boolean execute(String[] args) {
        if (args == null || args.length < 2) {
            console.red("Usage: /info <token>");
            return true;
        }

        String tokenStr = args[1];

        try {
            Token t = tokenService.findByToken(tokenStr);
            if (t == null) {
                console.red("Token not found.");
                return true;
            }

            String clientUuid = UuidConverter.tokenToUuid(t.getToken());
            String vlessLink = tokenService.buildVlessLink(clientUuid);
            String socks5Card = config.isSocks5Enabled()
                ? tokenService.buildSocks5Card(clientUuid, t.getToken())
                : "";

            console.println("");
            console.bold("Token info:");
            console.println(
                "  Token:   " + console.cyan() + t.getToken() + console.reset()
            );
            console.println(
                "  UUID:    " + console.cyan() + clientUuid + console.reset()
            );
            console.println(
                "  Status:  " +
                    t.getStatus().name() +
                    (t.isExpired() ? " (expired)" : "")
            );
            console.println(
                "  Expires: " +
                    t.getExpiresFormatted() +
                    " (" +
                    t.getRemainingDays() +
                    " day(s))"
            );
            console.println("  Conn:    " + t.getConnections());
            console.println(
                "  Traffic: ↑" +
                    formatBytes(t.getBytesUp()) +
                    " ↓" +
                    formatBytes(t.getBytesDown())
            );

            console.println("");
            console.bold("VLESS link:");
            console.println(
                "  " + console.cyan() + vlessLink + console.reset()
            );

            if (config.isSocks5Enabled()) {
                console.println("");
                console.bold("SOCKS5:");
                console.println(
                    "  " +
                        console.cyan() +
                        socks5Card.replace("\n", "\n  " + console.cyan()) +
                        console.reset()
                );
            }

            console.println("");
        } catch (Exception e) {
            console.red("Error: " + e.getMessage());
        }

        return true;
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format(
            "%.1f KB",
            bytes / 1024.0
        );
        if (bytes < 1024 * 1024 * 1024) return String.format(
            "%.1f MB",
            bytes / (1024.0 * 1024)
        );
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }
}
