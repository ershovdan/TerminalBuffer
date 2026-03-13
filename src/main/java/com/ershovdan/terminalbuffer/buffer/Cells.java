package com.ershovdan.terminalbuffer.buffer;

import java.util.ArrayList;
import java.util.List;

public class Cells {
    private Cell[][] data;

    private int posX;
    public int posY;

    private int cursorX = 0;
    private int cursorY = 0;

    private final int width;
    private final int height;
    private final int scroll;

    private int hiddenScrollSize;

    private String currentBg = "none";
    private String currentFg = "none";
    private List<String> currentStyle = new ArrayList<>();

    Cells(int width, int height, int scrollLines) {
        this.width = width;
        this.height = height;
        this.scroll = scrollLines;

        posX = 0;
        posY = scroll;

        hiddenScrollSize = scroll;

        data = new Cell[width][height + scroll];
        for (int y = 0; y < height + scroll; y++) {
            for (int x = 0; x < width; x++) {
                data[x][y] = new Cell('\0');
            }
        }
    }

    public void setCurrentBg(String currentBg) {
        this.currentBg = currentBg;
    }

    public void setCurrentFg(String currentFg) {
        this.currentFg = currentFg;
    }

    public void setCurrentStyle(List<String> currentStyle) {
        this.currentStyle = currentStyle;
    }

    public String getCurrentBg() {
        return currentBg;
    }

    public String getCurrentFg() {
        return currentFg;
    }

    public List<String> getCurrentStyle() {
        return currentStyle;
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

    public int getCursorPosX() {
        return cursorX;
    }

    public int getCursorPosY() {
        return cursorY;
    }

    public Cell getCell(int x, int y) {
        return data[x][y + hiddenScrollSize];
    }

    public boolean isActiveCellEditable() {
        return posY >= scroll;
    }

    public Cell getActiveCell() {
        return data[posX][posY];
    }

    public Cell insertChar(char ch) { // returns cell to be printed
        if (!isActiveCellEditable()) return null;
        getActiveCell().setCharacter(ch);
        getActiveCell().setForeground(currentFg);
        getActiveCell().setBackground(currentBg);
        getActiveCell().setStyle(currentStyle);
        Cell cellToReturn = getActiveCell();
        shiftCursorRight(1);
        return cellToReturn;
    }

    public Cell backspaceOperation() { // returns deleted cell
        if (!isActiveCellEditable()) return null;
        Cell cellToReturn = getActiveCell();
        shiftCursorLeft(1);
        getActiveCell().setCharacter('\0');
        return cellToReturn;
    }

    private void handleScrollShift() {
        if (posY >= height + scroll) posY = height + scroll - 1;
        if (posY < 0) posY = 0;
    }

    public void returnOperation() {
        posX = 0;
        shiftCursor(0, 1);

        handleScrollShift();

        if (cursorY >= height - 1) {
            addRow();
        }
    }

    public void autoReturnCheck() {
        if (posX >= width) {
            returnOperation();
        }
    }

    public void shiftCursorRight(int n) {
        shiftCursor(n, 0);
    }

    public void shiftCursorLeft(int n) {
        shiftCursor(-n, 0);
    }

    public void shiftCursorDown(int n) {
        shiftCursor(0, n);
    }

    public void shiftCursorUp(int n) {
        shiftCursor(0, -n);
    }

    public void setCursorPosition(int x, int y) {
        shiftCursor(x - cursorX, y - cursorY);
    }

    public void clear() {
        for (int y = scroll; y < height + scroll; y++) {
            for (int x = 0; x < width; x++) {
                data[x][y] = new Cell('\0');
            }
        }
    }

    public void clearAll() {
        for (int y = 0; y < height + scroll; y++) {
            for (int x = 0; x < width; x++) {
                data[x][y] = new Cell('\0');
            }
        }
    }

    public void fillLine(char ch) {
        if (!isActiveCellEditable()) return;
        for (int x = 0; x < width; x++) {
            data[x][posY].setCharacter(ch);
        }
    }

    public void insertText(String text) {
        if (!isActiveCellEditable()) return;
        for (char c : text.toCharArray()) {
            insertChar(c);
        }
    }

    public String getLineAsString(int y) {
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < width; x++) {
            sb.append(data[x][y + hiddenScrollSize]);
        }
        return sb.toString();
    }

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

    private void shiftCursor(int shiftX, int shiftY) {
        if (Math.abs(shiftX) > 1 && (posX + shiftX < 0 || posX + shiftX >= width)) {
            if (posX + shiftX < 0) {
                posX = 0;
            } else {
                posX += width - 1;
            }
        } else {
            posX += shiftX;
        }

        autoReturnCheck();

        if (posX < 0) {
            if (posY > 0) {
                posX = width - 1;
            } else {
                posX = 0;
            }
            posY--;
        }

        posY += shiftY;
        handleScrollShift();

        cursorX = posX;
        cursorY = posY - scroll;
        if (cursorY < 0) {
            cursorY = 0;
        }

        if (posY - cursorY < scroll) {
            hiddenScrollSize = posY - cursorY;
        } else {
            hiddenScrollSize = scroll;
        }

    }
}
