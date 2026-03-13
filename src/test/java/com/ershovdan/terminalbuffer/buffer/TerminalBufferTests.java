package com.ershovdan.terminalbuffer.buffer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TerminalBufferTests {
    private TerminalBuffer terminalBuffer;

    @BeforeEach
    void setup() {
        terminalBuffer = new TerminalBuffer(5, 10, 4);
    }

    @Test
    void testInitialCursorPosition() {
        assertEquals(0, terminalBuffer.getCursorPosX());
        assertEquals(0, terminalBuffer.getCursorPosY());
    }

    @Test
    void testGetActiveCell() {
        terminalBuffer.shiftCursorRight(2);
        assertEquals(terminalBuffer.getScreenCell(2, 0), terminalBuffer.getActiveCell());
    }

    @Test
    void testSetChar() {
        terminalBuffer.setChar('a');

        terminalBuffer.shiftCursorLeft(1);
        Cell cell = terminalBuffer.getActiveCell();

        assertEquals('a', cell.getCharacter());
    }

    @Test
    void testSetCurrentBg() {
        terminalBuffer.setChar('a');
        terminalBuffer.setCurrentBg("red");
        terminalBuffer.setChar('b');

        terminalBuffer.shiftCursorLeft(1);
        assertEquals("red", terminalBuffer.getActiveCell().getBackground());

        terminalBuffer.shiftCursorLeft(1);
        assertEquals("none", terminalBuffer.getActiveCell().getBackground());
    }

    @Test
    void testSetCurrentFg() {
        terminalBuffer.setChar('a');
        terminalBuffer.setCurrentFg("green");
        terminalBuffer.setChar('b');

        terminalBuffer.shiftCursorLeft(1);
        assertEquals("green", terminalBuffer.getActiveCell().getForeground());

        terminalBuffer.shiftCursorLeft(1);
        assertEquals("none", terminalBuffer.getActiveCell().getForeground());
    }

    @Test
    void testSetCurrentStyle() {
        List<String> style = new ArrayList<>();

        terminalBuffer.setChar('a');

        style.add("i");
        terminalBuffer.setCurrentStyle(style);
        terminalBuffer.setChar('b');

        style.add("b");
        terminalBuffer.setCurrentStyle(style);
        terminalBuffer.setChar('c');

        terminalBuffer.shiftCursorLeft(1);
        assertTrue(terminalBuffer.getActiveCell().getStyle().contains("i") &&
                terminalBuffer.getActiveCell().getStyle().contains("b"));

        terminalBuffer.shiftCursorLeft(1);
        assertTrue(terminalBuffer.getActiveCell().getStyle().contains("i"));

        terminalBuffer.shiftCursorLeft(1);
        assertTrue(terminalBuffer.getActiveCell().getStyle().isEmpty());
    }

    @Test
    void testShiftCursorRight() {
        terminalBuffer.shiftCursorRight(2);
        assertEquals(2, terminalBuffer.getCursorPosX());

        terminalBuffer.shiftCursorRight(20);
        assertEquals(4, terminalBuffer.getCursorPosX());
    }

    @Test
    void testShiftCursorLeft() {
        terminalBuffer.shiftCursorLeft(5);
        assertEquals(0, terminalBuffer.getCursorPosX());

        terminalBuffer.shiftCursorRight(3);
        terminalBuffer.shiftCursorLeft(2);
        assertEquals(1, terminalBuffer.getCursorPosX());
    }

    @Test
    void testShiftCursorDown() {
        terminalBuffer.shiftCursorDown(2);
        assertEquals(2, terminalBuffer.getCursorPosY());

        terminalBuffer.shiftCursorDown(10);
        assertEquals(9, terminalBuffer.getCursorPosY());
    }

    @Test
    void testShiftCursorUp() {
        terminalBuffer.shiftCursorDown(2);
        terminalBuffer.shiftCursorUp(1);
        assertEquals(1, terminalBuffer.getCursorPosY());

        terminalBuffer.shiftCursorDown(3);
        terminalBuffer.shiftCursorUp(20);
        assertEquals(0, terminalBuffer.getCursorPosY());
    }

    @Test
    void testSetCursorPosition() {
        terminalBuffer.setCursorPosition(3,2);
        assertEquals(3, terminalBuffer.getCursorPosX());
        assertEquals(2, terminalBuffer.getCursorPosY());

        terminalBuffer.setCursorPosition(20,20);
        assertEquals(4, terminalBuffer.getCursorPosX());
        assertEquals(9, terminalBuffer.getCursorPosY());

        terminalBuffer.setCursorPosition(-5,-5);
        assertEquals(0, terminalBuffer.getCursorPosX());
        assertEquals(0, terminalBuffer.getCursorPosY());
    }

    @Test
    void testReturnOperationMovesCursor() {
        terminalBuffer.shiftCursorRight(3);
        terminalBuffer.returnOperation();

        assertEquals(0, terminalBuffer.getCursorPosX());
        assertEquals(1, terminalBuffer.getCursorPosY());
    }

    @Test
    void testReturnOperationAtBottomLimit() {
        terminalBuffer.setCursorPosition(4, 9);
        terminalBuffer.returnOperation();

        assertEquals(0, terminalBuffer.getCursorPosX());
        assertEquals(9, terminalBuffer.getCursorPosY());
    }

    @Test
    void testBackspaceOperation() {
        terminalBuffer.setChar('a');
        terminalBuffer.backspaceOperation();

        Cell cell = terminalBuffer.getActiveCell();

        assertEquals('\0', cell.getCharacter());
    }

    @Test
    void testBackspaceOperationAtTopLeftLimit() {
        terminalBuffer.backspaceOperation();

        assertEquals(0, terminalBuffer.getCursorPosX());
    }

    @Test
    void testFillLine() {
        terminalBuffer.fillLine('a');

        for (int i = 0; i < 5; i++) {
            assertEquals('a', terminalBuffer.getScreenCell(i, 0).getCharacter());
        }
    }

    @Test
    void testClearScreen() {
        terminalBuffer.setChar('a');
        terminalBuffer.shiftCursorRight(1);
        terminalBuffer.setChar('b');

        terminalBuffer.clearScreen();

        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 5; x++) {
                assertEquals('\0', terminalBuffer.getScreenCell(x,y).getCharacter());
            }
        }
    }

    @Test
    void testClearAll() {
        terminalBuffer.setChar('a');
        terminalBuffer.shiftCursorRight(1);
        terminalBuffer.setChar('b');

        terminalBuffer.setCursorPosition(1, 9);
        terminalBuffer.backspaceOperation();

        terminalBuffer.setChar('c');
        terminalBuffer.shiftCursorRight(1);
        terminalBuffer.setChar('d');

        terminalBuffer.backspaceOperation();
        terminalBuffer.setChar('e');

        terminalBuffer.clearAll();

        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 5; x++) {
                assertEquals('\0', terminalBuffer.getScreenCell(x, y).getCharacter());
            }
        }

        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 5; x++) {
                assertEquals('\0', terminalBuffer.getScrollbackCell(x, y).getCharacter());
            }
        }
    }

    @Test
    void testGetScreenLineAsString() {
        terminalBuffer.setChar('a');
        terminalBuffer.setChar('b');
        terminalBuffer.setChar('c');

        assertEquals("abc\0\0",  terminalBuffer.getScreenLineAsString(0));
    }

    @Test
    void testGetScrollbackLineAsString() {
        terminalBuffer.setChar('a');
        terminalBuffer.setChar('b');
        terminalBuffer.setChar('c');

        terminalBuffer.setCursorPosition(4, 9);
        terminalBuffer.returnOperation();

        assertEquals("abc\0\0",  terminalBuffer.getScrollbackLineAsString(3));
    }

    @Test
    void testGetScreenAsString() {
        terminalBuffer.setChar('a');
        terminalBuffer.setChar('b');
        terminalBuffer.setChar('c');
        terminalBuffer.returnOperation();
        terminalBuffer.setChar('d');

        assertEquals("""
                        abc\0\0
                        d\0\0\0\0
                        \0\0\0\0\0
                        \0\0\0\0\0
                        \0\0\0\0\0
                        \0\0\0\0\0
                        \0\0\0\0\0
                        \0\0\0\0\0
                        \0\0\0\0\0
                        \0\0\0\0\0
                        """,
                terminalBuffer.getScreenAsString());
    }

    @Test
    void testGetAllAsString() {
        terminalBuffer.setChar('a');
        terminalBuffer.setChar('b');
        terminalBuffer.setChar('c');
        terminalBuffer.setCursorPosition(4, 9);
        terminalBuffer.returnOperation();

        terminalBuffer.setChar('a');
        terminalBuffer.setChar('b');
        terminalBuffer.setChar('c');
        terminalBuffer.returnOperation();
        terminalBuffer.setChar('d');

        assertEquals("""
                        \0\0\0\0\0
                        \0\0\0\0\0
                        abc\0\0
                        \0\0\0\0\0
                        \0\0\0\0\0
                        \0\0\0\0\0
                        \0\0\0\0\0
                        \0\0\0\0\0
                        \0\0\0\0\0
                        \0\0\0\0\0
                        \0\0\0\0\0
                        \0\0\0\0\0
                        abc\0\0
                        d\0\0\0\0
                        """,
                terminalBuffer.getAllAsString());
    }

    @Test
    void testInsertText() {
        terminalBuffer.insertText("Cyprus is an island.");

        assertEquals("""
                        Cypru
                        s is\s
                        an is
                        land.
                        \0\0\0\0\0
                        \0\0\0\0\0
                        \0\0\0\0\0
                        \0\0\0\0\0
                        \0\0\0\0\0
                        \0\0\0\0\0
                        """,
                terminalBuffer.getScreenAsString());
    }

    @Test
    void testAutoReturn() {
        terminalBuffer.shiftCursorRight(4);
        terminalBuffer.shiftCursorRight(1);

        assertEquals(0, terminalBuffer.getCursorPosX());
        assertEquals(1, terminalBuffer.getCursorPosY());
    }
}
