package com.nezxenka.vlesscore.cli;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleReader {

    private static final Logger log = LoggerFactory.getLogger(
        ConsoleReader.class
    );
    private final CommandDispatcher dispatcher;

    public ConsoleReader(CommandDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void start() {
        try (
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in)
            )
        ) {
            String line;
            System.out.print("> ");
            System.out.flush();

            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    boolean continueRunning = dispatcher.process(line);
                    if (!continueRunning) break;
                }
                System.out.print("> ");
                System.out.flush();
            }
        } catch (Exception e) {
            log.error("Console read error", e);
        }
    }
}
