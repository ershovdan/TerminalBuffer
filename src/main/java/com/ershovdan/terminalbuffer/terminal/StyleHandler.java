package com.ershovdan.terminalbuffer.terminal;

import com.ershovdan.terminalbuffer.buffer.Cell;

public class StyleHandler {
    private static final String RESET = "\u001B[0m";

    private static final String BG_BLACK = "\u001B[40m";
    private static final String BG_RED = "\u001B[41m";
    private static final String BG_GREEN = "\u001B[42m";
    private static final String BG_YELLOW = "\u001B[43m";
    private static final String BG_BLUE = "\u001B[44m";
    private static final String BG_PURPLE = "\u001B[45m";
    private static final String BG_CYAN = "\u001B[46m";
    private static final String BG_WHITE = "\u001B[47m";

    private static final String BLACK = "\u001B[30m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";

    private static final String BOLD = "\u001B[1m";
    private static final String ITALIC = "\u001B[3m";
    private static final String UNDERLINE = "\u001B[4m";

    public String getStyledString(Cell cell) {
        StringBuilder sb = new StringBuilder();

        switch (cell.getBackground()) {
            case "black" -> sb.append(BG_BLACK);
            case "red" -> sb.append(BG_RED);
            case "green" -> sb.append(BG_GREEN);
            case "yellow" -> sb.append(BG_YELLOW);
            case "blue" -> sb.append(BG_BLUE);
            case "purple" -> sb.append(BG_PURPLE);
            case "cyan" -> sb.append(BG_CYAN);
            case "white" -> sb.append(BG_WHITE);
            default ->  sb.append("");
        }

        switch (cell.getForeground()) {
            case "black" -> sb.append(BLACK);
            case "red" -> sb.append(RED);
            case "green" -> sb.append(GREEN);
            case "yellow" -> sb.append(YELLOW);
            case "blue" -> sb.append(BLUE);
            case "purple" -> sb.append(PURPLE);
            case "cyan" -> sb.append(CYAN);
            case "white" -> sb.append(WHITE);
            default ->  sb.append("");
        }

        if (!cell.getStyle().isEmpty()) {
            for (String st : cell.getStyle()) {
                switch (st) {
                    case "b" -> sb.append(BOLD);
                    case "i" -> sb.append(ITALIC);
                    case "u" -> sb.append(UNDERLINE);
                    default -> sb.append("");
                }
            }
        }

        sb.append(cell.getCharacter());
        sb.append(RESET);
        return sb.toString();
    }
}
