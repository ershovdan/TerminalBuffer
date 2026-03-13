package com.ershovdan.terminalbuffer.buffer;

import java.util.List;

public interface TerminalBufferInterface {
    void setCurrentBg(String currentBg);

    void setCurrentFg(String currentFg);

    void setCurrentStyle(List<String> currentStyle);

    int getCursorPosX();

    int getCursorPosY();

    Cell getScreenCell(int x, int y);

    Cell getScrollbackCell(int x, int y);

    Cell getActiveCell();

    Cell setChar(char ch);

    Cell backspaceOperation();

    void returnOperation();

    void autoReturnCheck();

    void shiftCursorRight(int n);

    void shiftCursorLeft(int n);

    void shiftCursorDown(int n);

    void shiftCursorUp(int n);

    void setCursorPosition(int x, int y);

    void clearScreen();

    void clearAll();

    void fillLine(char ch);

    void insertText(String text);

    String getScreenLineAsString(int y);

    String getScrollbackLineAsString(int y);

    String getScreenAsString();

    String getAllAsString();
}
