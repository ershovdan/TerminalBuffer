package com.ershovdan.terminalbuffer.buffer;

import java.util.ArrayList;
import java.util.List;

public class TerminalBuffer implements TerminalBufferInterface {
    private final int width;
    private final int height;
    private final int scroll;

    private int posY; // abs y
    private int posX; // abs x
    private int cursorX = 0; // relative x
    private int cursorY = 0; // relative y

    private Cell[][] data;

    private String currentBg = "none";
    private String currentFg = "none";
    private List<String> currentStyle = new ArrayList<>();

    public TerminalBuffer(int width, int height, int scrollLines) {
        this.width = width;
        this.height = height;
        this.scroll = scrollLines;

        posX = 0;
        posY = scroll;

        data = new Cell[width][height + scroll];
        for (int y = 0; y < height + scroll; y++) {
            for (int x = 0; x < width; x++) {
                data[x][y] = new Cell('\0');
            }
        }
    }

    @Override
    public void setCurrentBg(String currentBg) {
        this.currentBg = currentBg;
    }

    @Override
    public void setCurrentFg(String currentFg) {
        this.currentFg = currentFg;
    }

    @Override
    public void setCurrentStyle(List<String> currentStyle) {
        this.currentStyle = currentStyle;
    }

    private void addRow() {
        for (int y = 0; y < height + scroll - 1; y++) {
            for (int x = 0; x < width; x++) {
                data[x][y] = data[x][y + 1];
            }
        }

        for (int x = 0; x < width; x++) {
            data[x][height + scroll - 1] = new Cell('\0');
        }
    }

    @Override
    public int getCursorPosX() {
        return cursorX;
    }

    @Override
    public int getCursorPosY() {
        return cursorY;
    }

    @Override
    public Cell getScreenCell(int screenX, int screenY) {
        return data[screenX][screenY + scroll];
    }

    @Override
    public Cell getScrollbackCell(int scrollX, int scrollY) {
        return data[scrollX][scrollY];
    }

    @Override
    public Cell getActiveCell() {
        return data[posX][posY];
    }

    @Override
    public Cell setChar(char ch) { // returns cell to be printed
        Cell cellToReturn = getActiveCell().setCharacter(ch).setForeground(currentFg).
                setBackground(currentBg).setStyle(currentStyle);
        shiftCursorRight(1);
        return cellToReturn;
    }

    @Override
    public Cell backspaceOperation() { // returns deleted cell
        Cell cellToReturn = getActiveCell();
        shiftCursorLeft(1);
        getActiveCell().setCharacter('\0');
        return cellToReturn;
    }

    @Override
    public void returnOperation() {
        posX = 0;
        shiftCursor(0, 1);

        handleLimits();

        if (cursorY >= height - 1) {
            addRow();
        }
    }

    @Override
    public void autoReturnCheck() {
        if (posX >= width) {
            if (posY < height + scroll - 1) {
                returnOperation();
            } else {
                posX = width - 1;
            }
        }
    }

    @Override
    public void shiftCursorRight(int n) {
        shiftCursor(n, 0);
    }

    @Override
    public void shiftCursorLeft(int n) {
        shiftCursor(-n, 0);
    }

    @Override
    public void shiftCursorDown(int n) {
        shiftCursor(0, n);
    }

    @Override
    public void shiftCursorUp(int n) {
        shiftCursor(0, -n);
    }

    @Override
    public void setCursorPosition(int x, int y) {
        shiftCursor(x - cursorX, y - cursorY);
    }

    @Override
    public void clearScreen() {
        for (int y = scroll; y < height + scroll; y++) {
            for (int x = 0; x < width; x++) {
                data[x][y] = new Cell('\0');
            }
        }
    }

    @Override
    public void clearAll() {
        for (int y = 0; y < height + scroll; y++) {
            for (int x = 0; x < width; x++) {
                data[x][y] = new Cell('\0');
            }
        }
    }

    @Override
    public void fillLine(char ch) {
        for (int x = 0; x < width; x++) {
            data[x][posY].setCharacter(ch);
        }
    }

    @Override
    public void insertText(String text) { // if string is bigger than whole screen, beginning would be cut
        for (char c : text.toCharArray()) {
            setChar(c);
        }
    }

    @Override
    public String getScreenLineAsString(int screenY) {
        if (screenY >= height + scroll || screenY < scroll) return null;

        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < width; x++) {
            sb.append(data[x][screenY + scroll]);
        }
        return sb.toString();
    }

    @Override
    public String getScrollbackLineAsString(int scrollY) {
        if (scrollY >= scroll || scrollY < 0) return null;

        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < width; x++) {
            sb.append(data[x][scrollY + scroll]);
        }
        return sb.toString();
    }

    @Override
    public String getScreenAsString() {
        StringBuilder sb = new StringBuilder();
        for (int y = scroll; y < height + scroll; y++) {
            for (int x = 0; x < width; x++) {
                sb.append(data[x][y]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public String getAllAsString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < height + scroll; y++) {
            for (int x = 0; x < width; x++) {
                sb.append(data[x][y]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private void handleLimits() {
        if (posY >= height + scroll) posY = height + scroll - 1;
        if (posY < scroll) posY = scroll;

        if (posX < 0)  posX = 0;
        if (posX >= width) posX = width - 1;
    }

    private void calculateCursorPosition() {
        cursorX = posX;
        cursorY = posY - scroll;
    }

    private void handleBackspaceAtBeginning() {
        if (posX < 0) {
            if (posY > scroll) {
                posX = width - 1;
                posY--;
            } else {
                posX = 0;
            }
        }
    }

    private void shiftCursor(int shiftX, int shiftY) {
        posX += shiftX;
        posY += shiftY;

        if (Math.abs(shiftX) == 1) {
            autoReturnCheck();
            handleBackspaceAtBeginning();
        }

        handleLimits();

        calculateCursorPosition();
    }
}
