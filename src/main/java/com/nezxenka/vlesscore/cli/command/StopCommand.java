package com.nezxenka.vlesscore.cli.command;

import com.nezxenka.vlesscore.cli.ConsoleWriter;

public class StopCommand implements Command {

    private final ConsoleWriter console;

    public StopCommand(ConsoleWriter console) {
        this.console = console;
    }

    @Override
    public boolean execute(String[] args) {
        console.println(console.red() + "Shutting down..." + console.reset());
        System.exit(0);
        return false;
    }
}
