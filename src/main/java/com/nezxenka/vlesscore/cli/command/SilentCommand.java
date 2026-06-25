package com.nezxenka.vlesscore.cli.command;

import com.nezxenka.vlesscore.cli.ConsoleWriter;
import com.nezxenka.vlesscore.server.vless.VlessServerHandler;

public class SilentCommand implements Command {

    private final ConsoleWriter console;

    public SilentCommand(ConsoleWriter console) {
        this.console = console;
    }

    @Override
    public boolean execute(String[] args) {
        VlessServerHandler.silentMode = !VlessServerHandler.silentMode;
        if (VlessServerHandler.silentMode) {
            console.yellow("Silent mode ENABLED - connection logs hidden.");
        } else {
            console.green("Silent mode DISABLED - connection logs visible.");
        }
        return true;
    }
}
