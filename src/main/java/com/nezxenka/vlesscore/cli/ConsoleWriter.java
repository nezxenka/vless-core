package com.nezxenka.vlesscore.cli;

public class ConsoleWriter {

    private static final String RESET = "\033[0m";
    private static final String GREEN = "\033[32m";
    private static final String YELLOW = "\033[33m";
    private static final String CYAN = "\033[36m";
    private static final String RED = "\033[31m";
    private static final String BOLD = "\033[1m";
    private static final String DIM = "\033[2m";

    public void println(String msg) {
        System.out.println(msg);
    }

    public void printPrompt() {
        System.out.print("> ");
        System.out.flush();
    }

    public void green(String msg) {
        println(GREEN + msg + RESET);
    }

    public void yellow(String msg) {
        println(YELLOW + msg + RESET);
    }

    public void cyan(String msg) {
        println(CYAN + msg + RESET);
    }

    public void red(String msg) {
        println(RED + msg + RESET);
    }

    public void bold(String msg) {
        println(BOLD + msg + RESET);
    }

    public void dim(String msg) {
        println(DIM + msg + RESET);
    }

    public String green() {
        return GREEN;
    }

    public String yellow() {
        return YELLOW;
    }

    public String cyan() {
        return CYAN;
    }

    public String red() {
        return RED;
    }

    public String bold() {
        return BOLD;
    }

    public String dim() {
        return DIM;
    }

    public String reset() {
        return RESET;
    }
}
