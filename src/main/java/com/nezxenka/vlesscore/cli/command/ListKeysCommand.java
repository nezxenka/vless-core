package com.nezxenka.vlesscore.cli.command;

import com.nezxenka.vlesscore.cli.ConsoleWriter;
import com.nezxenka.vlesscore.database.model.Token;
import com.nezxenka.vlesscore.service.TokenManagementService;
import java.util.List;

public class ListKeysCommand implements Command {

    private final TokenManagementService tokenService;
    private final ConsoleWriter console;

    public ListKeysCommand(
        TokenManagementService tokenService,
        ConsoleWriter console
    ) {
        this.tokenService = tokenService;
        this.console = console;
    }

    @Override
    public boolean execute(String[] args) {
        try {
            List<Token> tokens = tokenService.listTokens();
            if (tokens.isEmpty()) {
                console.yellow("No tokens created yet.");
                return true;
            }

            console.println("");
            console.bold("Tokens (" + tokens.size() + "):");
            console.dim(
                "────────────────────────────────────────────────────────"
            );

            for (Token t : tokens) {
                String status = t.getStatus().name();
                String tokenShort =
                    t.getToken().length() > 20
                        ? t.getToken().substring(0, 20) + "..."
                        : t.getToken();

                console.println(
                    "• " +
                        console.cyan() +
                        tokenShort +
                        console.reset() +
                        " | " +
                        status +
                        " | until " +
                        t.getExpiresFormatted() +
                        " | conn=" +
                        t.getConnections()
                );
            }

            console.println("");
        } catch (Exception e) {
            console.red("Error: " + e.getMessage());
        }
        return true;
    }
}
