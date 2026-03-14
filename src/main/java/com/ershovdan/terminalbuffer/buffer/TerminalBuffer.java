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
                setBackground(currentBg).setStyle(new ArrayList<>(currentStyle));
        shiftCursorRight(1);
        return cellToReturn;
    }

    @Override
    public Cell backspaceOperation() { // returns deleted cell
        Cell cellToReturn = getActiveCell();
        shiftCursorLeft(1);
        getActiveCell().setCharacter('\0').setBackground("none")
                .setForeground("none").setStyle(new ArrayList<>());
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
            data[x][posY].setCharacter(ch).setForeground(currentFg).
                    setBackground(currentBg).setStyle(new ArrayList<>(currentStyle));
        }
    }

    @Override
    public void writeText(String text) { // if string is bigger than whole screen, beginning would be cut
        for (char c : text.toCharArray()) {
            setChar(c);
        }
    }

    @Override
    public String getScreenLineAsString(int screenY) {
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < width; x++) {
            sb.append(data[x][screenY + scroll].getCharacter());
        }
        return sb.toString();
    }

    @Override
    public String getScrollbackLineAsString(int scrollY) {
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < width; x++) {
            sb.append(data[x][scrollY].getCharacter());
        }
        return sb.toString();
    }

    @Override
    public String getScreenAsString() {
        StringBuilder sb = new StringBuilder();
        for (int y = scroll; y < height + scroll; y++) {
            for (int x = 0; x < width; x++) {
                sb.append(data[x][y].getCharacter());
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private Cell createCellWithCurrentAttributes(char ch) {
        return new Cell(ch).setForeground(currentFg)
                .setBackground(currentBg).setStyle(new ArrayList<>(currentStyle));
    }

    private boolean isEmptyCell(Cell cell) {
        return cell.getCharacter() == '\0' && "none".equals(cell.getForeground())
                && "none".equals(cell.getBackground()) && cell.getStyle().isEmpty();
    }

    private Cell shiftLineRight(int y, int fromX) {
        Cell oldLast = data[width - 1][y];
        for (int x = width - 1; x > fromX; x--) {
            data[x][y] = data[x - 1][y];
        }
        return oldLast;
    }

    private Cell insertCellAtLineStart(Cell cell, int y) {
        Cell oldLast = data[width - 1][y];
        for (int x = width - 1; x > 0; x--) {
            data[x][y] = data[x - 1][y];
        }
        data[0][y] = cell;
        return oldLast;
    }

    private boolean insertCharAtCursor(char ch) {
        Cell carry = shiftLineRight(posY, posX);
        data[posX][posY] = createCellWithCurrentAttributes(ch);

        boolean scrolled = false;
        int nextY = posY + 1;
        while (carry != null && !isEmptyCell(carry)) {
            if (nextY >= height + scroll) {
                addRow();
                scrolled = true;
                nextY = height + scroll - 1;
            }
            carry = insertCellAtLineStart(carry, nextY);
            nextY++;
        }

        return scrolled;
    }

    private void advanceCursorAfterInsert(boolean scrolledDuringInsert) {
        posX += 1;
        if (posX >= width) {
            posX = 0;
            posY += 1;
            if (posY >= height + scroll) {
                if (!scrolledDuringInsert) {
                    addRow();
                }
                posY = height + scroll - 1;
            }
        }

        handleLimits();
        calculateCursorPosition();
    }

    @Override
    public void insertText(String text) { // if string is bigger than whole screen beginning would be cut
        for (char c : text.toCharArray()) {
            boolean scrolled = insertCharAtCursor(c);
            advanceCursorAfterInsert(scrolled);
        }
    }

    @Override
    public void insertEmptyLineAtBottom() {
        addRow();
    }

    @Override
    public String getAllAsString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < height + scroll; y++) {
            for (int x = 0; x < width; x++) {
                sb.append(data[x][y].getCharacter());
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
