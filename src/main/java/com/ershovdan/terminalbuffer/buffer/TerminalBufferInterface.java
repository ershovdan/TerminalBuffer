package com.ershovdan.terminalbuffer.buffer;

import java.util.List;

/**
 * Core terminal text buffer API.
 * <p>
 * Implementations maintain a fixed-size screen (width x height) and a scrollback
 * area that stores lines scrolled off the top of the screen. Coordinates are
 * zero-based and screen coordinates are relative to the visible area, while
 * scrollback coordinates are relative to the history region.
 * <p>
 * The buffer maintains a cursor position and "current attributes" (foreground,
 * background, and styles) that apply to subsequent edits.
 */
public interface TerminalBufferInterface {
    /**
     * Sets the current background color attribute to be used for future edits.
     *
     * @param currentBg background color name or {@code "none"} for default
     */
    void setCurrentBg(String currentBg);

    /**
     * Sets the current foreground color attribute to be used for future edits.
     *
     * @param currentFg foreground color name or {@code "none"} for default
     */
    void setCurrentFg(String currentFg);

    /**
     * Sets the current style list (e.g. bold/italic/underline) used for future edits.
     *
     * @param currentStyle list of style identifiers
     */
    void setCurrentStyle(List<String> currentStyle);

    /**
     * Returns the cursor column (x) within the visible screen.
     *
     * @return cursor x (0-based)
     */
    int getCursorPosX();

    /**
     * Returns the cursor row (y) within the visible screen.
     *
     * @return cursor y (0-based)
     */
    int getCursorPosY();

    /**
     * Returns the cell at the given screen coordinates.
     *
     * @param x screen column (0-based)
     * @param y screen row (0-based)
     * @return cell located on the visible screen
     */
    Cell getScreenCell(int x, int y);

    /**
     * Returns the cell at the given scrollback coordinates.
     *
     * @param x scrollback column (0-based)
     * @param y scrollback row (0-based)
     * @return cell located in the scrollback region
     */
    Cell getScrollbackCell(int x, int y);

    /**
     * Returns the cell at the current cursor position.
     *
     * @return active cell
     */
    Cell getActiveCell();

    /**
     * Writes a single character at the cursor position, overwriting existing content.
     * Advances the cursor by one cell (with wrapping behavior as implemented).
     *
     * @param ch character to write
     * @return the cell that was written
     */
    Cell setChar(char ch);

    /**
     * Deletes the character to the left of the cursor, moving the cursor left.
     *
     * @return the cell that was cleared
     */
    Cell backspaceOperation();

    /**
     * Moves the cursor to the beginning of the next line, scrolling if at bottom.
     */
    void returnOperation();

    /**
     * Checks for automatic line wrap when the cursor goes past the last column.
     */
    void autoReturnCheck();

    /**
     * Moves the cursor right by {@code n} cells, clamped to screen bounds.
     *
     * @param n number of cells to move
     */
    void shiftCursorRight(int n);

    /**
     * Moves the cursor left by {@code n} cells, clamped to screen bounds.
     *
     * @param n number of cells to move
     */
    void shiftCursorLeft(int n);

    /**
     * Moves the cursor down by {@code n} cells, clamped to screen bounds.
     *
     * @param n number of cells to move
     */
    void shiftCursorDown(int n);

    /**
     * Moves the cursor up by {@code n} cells, clamped to screen bounds.
     *
     * @param n number of cells to move
     */
    void shiftCursorUp(int n);

    /**
     * Sets the cursor position on the visible screen. Coordinates outside the screen
     * are clamped to bounds.
     *
     * @param x target column (0-based)
     * @param y target row (0-based)
     */
    void setCursorPosition(int x, int y);

    /**
     * Clears the visible screen region (does not affect scrollback).
     */
    void clearScreen();

    /**
     * Clears both the visible screen and scrollback regions.
     */
    void clearAll();

    /**
     * Fills the current line with the given character using current attributes.
     *
     * @param ch character to fill with (use {@code '\0'} for empty cells)
     */
    void fillLine(char ch);

    /**
     * Writes text at the cursor, overwriting existing content and advancing the cursor.
     *
     * @param text text to write
     */
    void writeText(String text);

    /**
     * Inserts text at the cursor, shifting existing content to the right and wrapping
     * to following lines as needed.
     *
     * @param text text to insert
     */
    void insertText(String text);

    /**
     * Inserts an empty line at the bottom of the screen, scrolling content upward.
     */
    void insertEmptyLineAtBottom();

    /**
     * Returns a full screen line as a string.
     *
     * @param y screen row (0-based)
     * @return line contents as a string
     */
    String getScreenLineAsString(int y);

    /**
     * Returns a full scrollback line as a string.
     *
     * @param y scrollback row (0-based)
     * @return line contents as a string
     */
    String getScrollbackLineAsString(int y);

    /**
     * Returns the entire visible screen content as a single string with line breaks.
     *
     * @return screen content
     */
    String getScreenAsString();

    /**
     * Returns the entire scrollback + screen content as a single string with line breaks.
     *
     * @return combined content
     */
    String getAllAsString();
}
