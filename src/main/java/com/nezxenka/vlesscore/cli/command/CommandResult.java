package com.nezxenka.vlesscore.cli.command;

public class CommandResult {

    private final boolean continueRunning;
    private final String message;

    private CommandResult(boolean continueRunning, String message) {
        this.continueRunning = continueRunning;
        this.message = message;
    }

    public static CommandResult ok() {
        return new CommandResult(true, null);
    }

    public static CommandResult ok(String msg) {
        return new CommandResult(true, msg);
    }

    public static CommandResult stop() {
        return new CommandResult(false, null);
    }

    public boolean shouldContinue() {
        return continueRunning;
    }

    public String getMessage() {
        return message;
    }
}
