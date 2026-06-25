package com.nezxenka.vlesscore.cli.command;

import com.nezxenka.vlesscore.cli.ConsoleWriter;

public class HelpCommand implements Command {

    private final ConsoleWriter console;

    public HelpCommand(ConsoleWriter console) {
        this.console = console;
    }

    @Override
    public boolean execute(String[] args) {
        console.println("");
        console.bold(console.cyan() + "Available commands:" + console.reset());
        console.cyan("/new [days]             - create a key");
        console.cyan("/extend <token> <days>  - extend a key");
        console.cyan("/list                   - list all keys");
        console.cyan("/info <token>           - details + links");
        console.cyan("/block <token>          - block a key");
        console.cyan("/stats                  - key statistics");
        console.cyan("/silent                 - toggle connection logs");
        console.cyan("/help                   - this help");
        console.cyan("/stop                   - stop the server");
        console.println("");
        return true;
    }
}
