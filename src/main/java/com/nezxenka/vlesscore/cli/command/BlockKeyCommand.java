package com.nezxenka.vlesscore.cli.command;

import com.nezxenka.vlesscore.cli.ConsoleWriter;
import com.nezxenka.vlesscore.service.TokenManagementService;

public class BlockKeyCommand implements Command {

    private final TokenManagementService tokenService;
    private final ConsoleWriter console;

    public BlockKeyCommand(
        TokenManagementService tokenService,
        ConsoleWriter console
    ) {
        this.tokenService = tokenService;
        this.console = console;
    }

    @Override
    public boolean execute(String[] args) {
        if (args == null || args.length < 2) {
            console.red("Usage: /block <token>");
            return true;
        }

        String tokenStr = args[1];

        try {
            var token = tokenService.findByToken(tokenStr);
            if (token == null) {
                console.red("Token not found.");
                return true;
            }
            tokenService.blockToken(tokenStr);
            console.yellow("✓ Token blocked.");
        } catch (Exception e) {
            console.red("Error: " + e.getMessage());
        }

        return true;
    }
}
