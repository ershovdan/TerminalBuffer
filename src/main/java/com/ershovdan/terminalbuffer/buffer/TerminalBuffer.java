package com.ershovdan.terminalbuffer.buffer;

import java.util.ArrayList;
import java.util.List;

public class TerminalBuffer implements TerminalBufferInterface {
    private final int width;
    private final int height;
    private final int scroll;
    private final int capacity;

    private int posY; // abs y
    private int posX; // abs x
    private int cursorX = 0; // relative x
    private int cursorY = 0; // relative y
    private int topIndex = 0;

    private char[][] characters;
    private CellAttributes[][] attributes;

    private String currentBg = "none";
    private String currentFg = "none";
    private List<String> currentStyle = new ArrayList<>();

    public TerminalBuffer(int width, int height, int scrollLines) {
        this.width = width;
        this.height = height;
        this.scroll = scrollLines;
        this.capacity = height + scroll;

        posX = 0;
        posY = scroll;

        characters = new char[capacity][width];
        attributes = new CellAttributes[capacity][width];
        for (int y = 0; y < capacity; y++) {
            clearPhysicalRow(y);
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
        int oldTopIndex = topIndex;
        topIndex = (topIndex + 1) % capacity;
        clearPhysicalRow(oldTopIndex);
    }

    private void clearPhysicalRow(int row) {
        for (int x = 0; x < width; x++) {
            characters[row][x] = '\0';
            attributes[row][x] = CellAttributes.DEFAULT;
        }
    }

    private int rowIndex(int logicalY) {
        return (topIndex + logicalY) % capacity;
    }

    private char getCharacter(int x, int y) {
        return characters[rowIndex(y)][x];
    }

    private CellAttributes getAttributes(int x, int y) {
        return attributes[rowIndex(y)][x];
    }

    private Cell getCell(int x, int y) {
        return new Cell(getCharacter(x, y), getAttributes(x, y), cell -> setCell(x, y, cell));
    }

    private void setCell(int x, int y, char character, CellAttributes cellAttributes) {
        int row = rowIndex(y);
        characters[row][x] = character;
        attributes[row][x] = cellAttributes.isDefault() ? CellAttributes.DEFAULT : cellAttributes;
    }

    private void setCell(int x, int y, Cell cell) {
        setCell(x, y, cell.getCharacter(), cell.getAttributes());
    }

    private void clearCell(int x, int y) {
        setCell(x, y, '\0', CellAttributes.DEFAULT);
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
        return getCell(screenX, screenY + scroll);
    }

    @Override
    public Cell getScrollbackCell(int scrollX, int scrollY) {
        return getCell(scrollX, scrollY);
    }

    @Override
    public Cell getActiveCell() {
        return getCell(posX, posY);
    }

    @Override
    public Cell setChar(char ch) { // returns cell to be printed
        Cell cellToReturn = createCellWithCurrentAttributes(ch);
        setCell(posX, posY, cellToReturn);
        shiftCursorRight(1);
        return cellToReturn;
    }

    @Override
    public Cell backspaceOperation() { // returns deleted cell
        Cell cellToReturn = getActiveCell();
        shiftCursorLeft(1);
        clearCell(posX, posY);
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
            if (posY < capacity - 1) {
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
        for (int y = scroll; y < capacity; y++) {
            for (int x = 0; x < width; x++) {
                clearCell(x, y);
            }
        }
    }

    @Override
    public void clearAll() {
        topIndex = 0;
        for (int y = 0; y < capacity; y++) {
            clearPhysicalRow(y);
        }
    }

    @Override
    public void fillLine(char ch) {
        CellAttributes currentAttributes = createCurrentAttributes();
        for (int x = 0; x < width; x++) {
            setCell(x, posY, ch, currentAttributes);
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
            sb.append(getCharacter(x, screenY + scroll));
        }
        return sb.toString();
    }

    @Override
    public String getScrollbackLineAsString(int scrollY) {
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < width; x++) {
            sb.append(getCharacter(x, scrollY));
        }
        return sb.toString();
    }

    @Override
    public String getScreenAsString() {
        StringBuilder sb = new StringBuilder();
        for (int y = scroll; y < capacity; y++) {
            for (int x = 0; x < width; x++) {
                sb.append(getCharacter(x, y));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private CellAttributes createCurrentAttributes() {
        if ("none".equals(currentFg) && "none".equals(currentBg) && currentStyle.isEmpty()) {
            return CellAttributes.DEFAULT;
        }
        return new CellAttributes(currentFg, currentBg, new ArrayList<>(currentStyle));
    }

    private Cell createCellWithCurrentAttributes(char ch) {
        return new Cell(ch, createCurrentAttributes());
    }

    private boolean isEmptyCell(Cell cell) {
        return cell.getCharacter() == '\0' && cell.getAttributes().isDefault();
    }

    private Cell shiftLineRight(int y, int fromX) {
        Cell oldLast = getCell(width - 1, y);
        int row = rowIndex(y);
        for (int x = width - 1; x > fromX; x--) {
            characters[row][x] = characters[row][x - 1];
            attributes[row][x] = attributes[row][x - 1];
        }
        return oldLast;
    }

    private Cell insertCellAtLineStart(Cell cell, int y) {
        Cell oldLast = getCell(width - 1, y);
        int row = rowIndex(y);
        for (int x = width - 1; x > 0; x--) {
            characters[row][x] = characters[row][x - 1];
            attributes[row][x] = attributes[row][x - 1];
        }
        setCell(0, y, cell);
        return oldLast;
    }

    private boolean insertCharAtCursor(char ch) {
        Cell carry = shiftLineRight(posY, posX);
        setCell(posX, posY, createCellWithCurrentAttributes(ch));

        boolean scrolled = false;
        int nextY = posY + 1;
        while (carry != null && !isEmptyCell(carry)) {
            if (nextY >= capacity) {
                addRow();
                scrolled = true;
                nextY = capacity - 1;
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
            if (posY >= capacity) {
                if (!scrolledDuringInsert) {
                    addRow();
                }
                posY = capacity - 1;
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
        for (int y = 0; y < capacity; y++) {
            for (int x = 0; x < width; x++) {
                sb.append(getCharacter(x, y));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private void handleLimits() {
        if (posY >= capacity) posY = capacity - 1;
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
