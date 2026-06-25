package com.nezxenka.vlesscore.cli.command;

import com.nezxenka.vlesscore.cli.ConsoleWriter;
import com.nezxenka.vlesscore.config.AppConfig;
import com.nezxenka.vlesscore.crypto.UuidConverter;
import com.nezxenka.vlesscore.database.model.Token;
import com.nezxenka.vlesscore.service.TokenManagementService;

public class NewKeyCommand implements Command {

    private final TokenManagementService tokenService;
    private final AppConfig config;
    private final ConsoleWriter console;

    public NewKeyCommand(
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
        int days = config.getDefaultTokenDays();
        if (args != null && args.length >= 2) {
            try {
                days = Integer.parseInt(args[1]);
                if (days <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                console.red("Usage: /new [days]");
                return true;
            }
        }

        try {
            Token token = tokenService.createToken(days);
            String clientUuid = UuidConverter.tokenToUuid(token.getToken());
            tokenService.createMapping(clientUuid, token.getToken());
            String vlessLink = tokenService.buildVlessLink(clientUuid);
            String socks5Card = config.isSocks5Enabled()
                ? tokenService.buildSocks5Card(clientUuid, token.getToken())
                : "";

            console.println("");
            console.println(
                console.green() +
                    console.bold() +
                    "╔══════════════════════════════════════════════════════════════════╗" +
                    console.reset()
            );
            console.println(
                console.green() +
                    console.bold() +
                    "║                    ✓ NEW KEY CREATED                            ║" +
                    console.reset()
            );
            console.println(
                console.green() +
                    console.bold() +
                    "╠══════════════════════════════════════════════════════════════════╣" +
                    console.reset()
            );

            console.println(
                console.green() +
                    "║ " +
                    console.cyan() +
                    "Token: " +
                    console.reset() +
                    token.getToken()
            );
            console.println(
                console.green() +
                    "║ " +
                    console.cyan() +
                    "UUID:  " +
                    console.reset() +
                    clientUuid
            );
            console.println(
                console.green() +
                    "║ " +
                    console.cyan() +
                    "Expiry: " +
                    console.reset() +
                    days +
                    " day(s) (until " +
                    token.getExpiresFormatted() +
                    ")"
            );
            console.println(console.green() + "║" + console.reset());

            console.println(
                console.green() +
                    console.bold() +
                    "╠══════════════════════════════════════════════════════════════════╣" +
                    console.reset()
            );
            console.println(
                console.green() +
                    "║ " +
                    console.bold() +
                    "VLESS (VPN) - paste into v2rayN/v2rayNG:" +
                    console.reset()
            );
            console.println(
                console.green() +
                    "║ " +
                    console.cyan() +
                    vlessLink +
                    console.reset()
            );
            console.println(console.green() + "║" + console.reset());

            if (config.isSocks5Enabled()) {
                console.println(
                    console.green() +
                        "║ " +
                        console.bold() +
                        "SOCKS5 (Proxy):" +
                        console.reset()
                );
                console.println(
                    console.green() +
                        "║ " +
                        console.cyan() +
                        socks5Card.replace("\n", "\n║ " + console.cyan()) +
                        console.reset()
                );
                console.println(console.green() + "║" + console.reset());
            } else {
                console.println(
                    console.green() +
                        console.dim() +
                        "║ SOCKS5 disabled (socks5-enabled: false)" +
                        console.reset()
                );
            }

            console.println(
                console.green() +
                    console.bold() +
                    "╚══════════════════════════════════════════════════════════════════╝" +
                    console.reset()
            );
            console.println("");
        } catch (Exception e) {
            console.red("Error creating token: " + e.getMessage());
        }

        return true;
    }
}
