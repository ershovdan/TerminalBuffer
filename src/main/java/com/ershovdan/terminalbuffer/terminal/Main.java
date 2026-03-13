package com.ershovdan.terminalbuffer.terminal;

import java.io.IOException;

public class Main {
    private final static int INIT_WIDTH = 10;
    private final static int INIT_HEIGHT = 10;
    private final static int MAX_SCROLLBACK = 5;

    public static void main(String[] args) throws IOException, InterruptedException {
        Terminal terminal = new Terminal(INIT_WIDTH, INIT_HEIGHT, MAX_SCROLLBACK);
    }

}
