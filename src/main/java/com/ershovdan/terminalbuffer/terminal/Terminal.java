package com.ershovdan.terminalbuffer.terminal;

import com.ershovdan.terminalbuffer.buffer.Cell;
import com.ershovdan.terminalbuffer.buffer.TerminalBuffer;
import com.ershovdan.terminalbuffer.buffer.TerminalBufferInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Terminal {
    private final int width;
    private final int height;

    private TerminalBufferInterface terminalBuffer;

    private StyleHandler styleHandler;

    private final Scanner scanner = new Scanner(System.in);

    public Terminal(int width, int height, int maxScrollback) throws IOException, InterruptedException {
        this.width = width;
        this.height = height;

        terminalBuffer = new TerminalBuffer(width, height, maxScrollback);
        styleHandler = new StyleHandler();

        switchToSttyRaw();

        draw();
        processInput();
    }

    private void switchToSttySane() throws IOException, InterruptedException {
        new ProcessBuilder("/bin/sh", "-c", "stty sane").inheritIO().start().waitFor();
    }

    private void switchToSttyRaw() throws IOException, InterruptedException {
        new ProcessBuilder("/bin/sh", "-c", "stty raw -echo").inheritIO().start().waitFor();
    }

    private void processCommand() throws IOException, InterruptedException {
        switchToSttySane();

        moveCursor(0, height + 4);

        List<String> blocks = new ArrayList<>(Arrays.asList(scanner.nextLine().split(" ")));

        moveCursor(0, height + 4);
        clearLine();

        switchToSttyRaw();

        moveCursorToActiveCell();

        try {
            switch (blocks.getFirst()) {
                case "bg" -> terminalBuffer.setCurrentBg(blocks.get(1));
                case "fg" -> terminalBuffer.setCurrentFg(blocks.get(1));
                case "st" -> {
                    blocks.removeFirst();
                    terminalBuffer.setCurrentStyle(blocks);
                }
                case "shift" -> {
                    switch (blocks.get(1)) {
                        case "up" -> terminalBuffer.shiftCursorUp(Integer.parseInt(blocks.get(2)));
                        case "down" -> terminalBuffer.shiftCursorDown(Integer.parseInt(blocks.get(2)));
                        case "left" -> terminalBuffer.shiftCursorLeft(Integer.parseInt(blocks.get(2)));
                        case "right" -> terminalBuffer.shiftCursorRight(Integer.parseInt(blocks.get(2)));
                    }
                }
                case "insert-text" -> {
                    blocks.removeFirst();
                    String text = String.join(" ", blocks);
                    terminalBuffer.insertText(text);
                }
                case "clr" -> terminalBuffer.clearScreen();
                case "clrall" -> terminalBuffer.clearAll();
                case "fill" -> terminalBuffer.fillLine(blocks.get(1).charAt(0));
            }
        } catch (Exception e) {}

        moveCursorToActiveCell();
    }

    private void moveCursorToActiveCell() {
        moveCursor(1 + terminalBuffer.getCursorPosX(), 3 + terminalBuffer.getCursorPosY());
    }

    private void processInput() throws IOException, InterruptedException {
        String input = "";

        while (!input.equals("!e")) {
            int ch = System.in.read();
            if (ch == 27) {
                if (System.in.available() > 0) { // arrows sequence
                    int next = System.in.read();
                    if (next == 91) { // arrows
                        int arrow = System.in.read();
                        switch (arrow) {
                            case 'A' -> {
                                terminalBuffer.shiftCursorUp(1);
                                moveCursorToActiveCell();
                            }
                            case 'B' -> {
                                terminalBuffer.shiftCursorDown(1);
                                moveCursorToActiveCell();
                            }
                            case 'C' -> {
                                terminalBuffer.shiftCursorRight(1);
                                moveCursorToActiveCell();
                            }
                            case 'D' -> {
                                terminalBuffer.shiftCursorLeft(1);
                                moveCursorToActiveCell();
                            }
                        }
                    }
                } else { // escape
                    switchToSttySane();
                    scanner.close();
                    break;
                }
            } else if (ch == 17) { // control+q
                processCommand();
            } else if (ch == 8 || ch == 127) { // backspace
                insertStyledSymbol(terminalBuffer.backspaceOperation());
                moveCursorToActiveCell();
            } else if (ch == 10 || ch == 13) { // return
                terminalBuffer.returnOperation();
            } else {
                insertStyledSymbol(terminalBuffer.setChar((char) ch));
                moveCursorToActiveCell();
            }

            drawCoordinates();
            redraw();
        }
    }

    private void drawHorLine(int w) {
        System.out.print("\r");
        for (int x = 0; x < w; x++) System.out.print("=");
        System.out.println();
    }

    private void drawCoordinates() {
        moveCursor(0, 0);
        clearLine();
        System.out.print("column: " + terminalBuffer.getCursorPosX() + ", row: " + terminalBuffer.getCursorPosY());
        moveCursorToActiveCell();
    }

    private void moveCursor(int x, int y) {
        System.out.print("\u001B[" + y + ";" + x + "H");
    }

    private void clearLine() {
        System.out.print("\033[2K");
    }

    private void insertStyledSymbol(Cell cell) {
        if (cell != null) {
            System.out.print(styleHandler.getStyledString(cell));
        }
    }

    private void redraw() {
        for (int y = 0; y < height; y++) {
            moveCursor(1, y + 3);
            clearLine();
            for (int x = 0; x < width; x++) {
                moveCursor(x + 1, y + 3);
                insertStyledSymbol(terminalBuffer.getScreenCell(x, y));
            }
        }

        moveCursorToActiveCell();
    }

    public void draw() {
        System.out.print("\033[H\033[2J");
        System.out.println();

        drawHorLine(width);

        for (int y = 0; y < height; y++) System.out.println();

        drawHorLine(width);
        drawCoordinates();

        moveCursorToActiveCell();
    }

}
