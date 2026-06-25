package com.nezxenka.vlesscore.cli.command;

import com.nezxenka.vlesscore.cli.ConsoleWriter;
import com.nezxenka.vlesscore.config.AppConfig;
import com.nezxenka.vlesscore.database.model.Token;
import com.nezxenka.vlesscore.service.TokenManagementService;

public class ExtendKeyCommand implements Command {

    private final TokenManagementService tokenService;
    private final AppConfig config;
    private final ConsoleWriter console;

    public ExtendKeyCommand(
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
        if (args == null || args.length < 3) {
            console.red("Usage: /extend <token> <days>");
            return true;
        }

        String tokenStr = args[1];
        int days;
        try {
            days = Integer.parseInt(args[2]);
            if (days <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            console.red("days must be a positive number.");
            return true;
        }

        try {
            Token token = tokenService.extendToken(tokenStr, days);
            if (token == null) {
                console.red("Token not found: " + tokenStr);
            } else {
                console.green("✓ Token extended by " + days + " day(s).");
                console.green(
                    "  New expiry: " +
                        token.getExpiresFormatted() +
                        " (remaining " +
                        token.getRemainingDays() +
                        " day(s))"
                );
            }
        } catch (Exception e) {
            console.red("Error: " + e.getMessage());
        }

        return true;
    }
}
