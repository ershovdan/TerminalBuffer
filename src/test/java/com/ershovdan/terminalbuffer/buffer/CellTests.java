package com.ershovdan.terminalbuffer.buffer;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CellTests {

    @Test
    void testInitialization() {
        Cell cell = new Cell('a');

        assertEquals('a', cell.getCharacter());
        assertEquals("none", cell.getForeground());
        assertEquals("none", cell.getBackground());
        assertTrue(cell.getStyle().isEmpty());
    }

    @Test
    void testSetCharacter() {
        Cell cell = new Cell('a');
        cell.setCharacter('A');

        assertEquals('A', cell.getCharacter());
    }

    @Test
    void testSetForeground() {
        Cell cell = new Cell('a');
        cell.setForeground("red");
        assertEquals("red", cell.getForeground());
    }

    @Test
    void testSetBackground() {
        Cell cell = new Cell('a');
        cell.setBackground("green");
        assertEquals("green", cell.getBackground());
    }

    @Test
    void testSetStyle() {
        Cell cell = new Cell('a');

        List<String> style = new ArrayList<>();
        style.add("i");
        style.add("b");

        cell.setStyle(style);

        assertTrue(style.contains("i"));
        assertTrue(style.contains("b"));
    }

}