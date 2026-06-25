package com.nezxenka.vlesscore.cli;

import com.nezxenka.vlesscore.cli.command.*;
import com.nezxenka.vlesscore.config.AppConfig;
import com.nezxenka.vlesscore.monitoring.HealthReporter;
import com.nezxenka.vlesscore.monitoring.SystemMetrics;
import com.nezxenka.vlesscore.server.vless.VlessServerHandler;
import com.nezxenka.vlesscore.service.StatisticsService;
import com.nezxenka.vlesscore.service.TokenManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandDispatcher {

    private static final Logger log = LoggerFactory.getLogger(
        CommandDispatcher.class
    );

    private final TokenManagementService tokenService;
    private final AppConfig config;
    private final ConsoleWriter console;
    private final StatisticsService statsService;
    private final SystemMetrics systemMetrics;
    private final HealthReporter healthReporter;
    private final CommandRegistry registry;

    public CommandDispatcher(
        TokenManagementService tokenService,
        AppConfig config,
        ConsoleWriter console,
        StatisticsService statsService,
        SystemMetrics systemMetrics,
        HealthReporter healthReporter
    ) {
        this.tokenService = tokenService;
        this.config = config;
        this.console = console;
        this.statsService = statsService;
        this.systemMetrics = systemMetrics;
        this.healthReporter = healthReporter;
        this.registry = new CommandRegistry();

        registerCommands();
    }

    private void registerCommands() {
        registry.register(
            "/new",
            new NewKeyCommand(tokenService, config, console)
        );
        registry.register(
            "/extend",
            new ExtendKeyCommand(tokenService, config, console)
        );
        registry.register("/list", new ListKeysCommand(tokenService, console));
        registry.register(
            "/info",
            new InfoKeyCommand(tokenService, config, console)
        );
        registry.register("/block", new BlockKeyCommand(tokenService, console));
        registry.register("/stats", new StatsCommand(statsService, console));
        registry.register("/silent", new SilentCommand(console));
        registry.register("/help", new HelpCommand(console));
        registry.register("/stop", new StopCommand(console));
        registry.register("/exit", new StopCommand(console));
        registry.register("/quit", new StopCommand(console));
    }

    public boolean process(String input) {
        if (input == null || input.isBlank()) return true;

        String trimmed = input.trim();
        String[] parts = trimmed.split("\\s+");
        String commandName = parts[0].toLowerCase();

        Command command = registry.get(commandName);
        if (command == null) {
            console.red("Unknown command: " + commandName + ". Type /help");
            return true;
        }

        try {
            return command.execute(parts);
        } catch (Exception e) {
            console.red("Error: " + e.getMessage());
            log.error("Command execution error", e);
            return true;
        }
    }

    public void printHelp() {
        Command helpCmd = registry.get("/help");
        if (helpCmd != null) {
            helpCmd.execute(null);
        }
    }
}
