package com.nezxenka.vlesscore.cli.command;

import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {

    private final Map<String, Command> commands = new HashMap<>();

    public void register(String name, Command command) {
        commands.put(name.toLowerCase(), command);
    }

    public Command get(String name) {
        return commands.get(name.toLowerCase());
    }

    public int size() {
        return commands.size();
    }
}
