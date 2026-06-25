package com.nezxenka.vlesscore.cli.command;

import com.nezxenka.vlesscore.cli.ConsoleWriter;
import com.nezxenka.vlesscore.service.StatisticsService;

public class StatsCommand implements Command {

    private final StatisticsService statsService;
    private final ConsoleWriter console;

    public StatsCommand(StatisticsService statsService, ConsoleWriter console) {
        this.statsService = statsService;
        this.console = console;
    }

    @Override
    public boolean execute(String[] args) {
        try {
            int total = statsService.getTotalKeys();
            int active = statsService.getActiveKeys();
            int blocked = statsService.getBlockedKeys();
            int expired = statsService.getExpiredKeys();

            console.println("");
            console.bold("Key Statistics:");
            console.println("  🟢 Active:    " + active);
            console.println("  🔴 Blocked:   " + blocked);
            console.println("  ⚪ Expired:    " + expired);
            console.println("  📦 Total:     " + total);
            console.println("");
        } catch (Exception e) {
            console.red("Error: " + e.getMessage());
        }
        return true;
    }
}
