# Terminal Buffer (Java)

---

## Overview

This project implements a terminal screen buffer in Java with a visible screen
and a scrollback history. It simulates how terminal emulators store characters,
cursor position, and styling before rendering text to the screen.

The buffer stores characters in a 2 dimensional grid of cells, where each cell contains:

* a character
* foreground color
* background color
* text style

---

## Operations

Cursor:

* `shiftCursorRight(n)`
* `shiftCursorLeft(n)`
* `shiftCursorUp(n)`
* `shiftCursorDown(n)`
* `setCursorPosition(x, y)`

Editing:

* `setChar(char c)`
* `writeText(text)`
* `insertText(text)`
* `returnOperation()`
* `backspaceOperation()`
* `fillLine(char c)`
* `insertEmptyLineAtBottom()`
* `clearScreen()`
* `clearAll()`

Attributes:

* `setCurrentFg(color)`
* `setCurrentBg(color)`
* `setCurrentStyle(list)`

Write vs insert:

* `writeText(text)` overwrites existing content and advances the cursor.
* `insertText(text)` shifts existing content to the right and wraps to following lines as needed.

Example:

`terminalBuffer.setCurrentFg("green");`
`terminalBuffer.setCurrentBg("black");`
`terminalBuffer.setCurrentStyle(List.of("b"));`

All characters written afterward inherit the current style.

---

# How the Buffer Works

The buffer is implemented as a fixed-size 2D array of `Cell`:
`width x (height + scrollback)`.
The top part is scrollback (history), the bottom part is the visible screen.

Each time a character is written (`setChar` / `writeText`):

1. The character is stored in the current cursor cell
2. The cursor moves right
3. If the cursor moves past the last column, it wraps to the next line
4. If the cursor is on the last screen row, the buffer scrolls upward and the
   top screen line becomes part of scrollback.

Insert mode (`insertText`) differs: it shifts existing content to the right
from the cursor position and propagates overflow into following lines.

Design trade-off: a fixed-size array keeps access O(1) and the implementation
simple, at the cost of more copying when scrolling.

---

# Core Classes

* `TerminalBufferInterface` — public API for the buffer.
* `TerminalBuffer` — buffer implementation.
* `Cell` — single character cell with attributes.

---

# Build & Run

* Java 21 is used by default
* Maven framework is used
* Compile application with `mvn clean compile`
* Run terminal demo with `mvn exec:java` (demo only)

---

# Tests

Run tests with Maven:

`mvn test`

This will execute all unit tests, including boundary and edge cases.

---

# Terminal Visualizer

Supporting (demo/visualizer):

* `Terminal` — interactive demo that renders the buffer.
* `StyleHandler` — ANSI styling helper for the demo.
* `Main` — demo entry point.

Notes:

* This visualizer is **not** required by the task, but helps validate behavior.
* It requires a full-featured terminal emulator and **does not work on Windows**.
* Colors and styles are basic ANSI sequences.

Supported commands (press `Ctrl+Q` to enter command mode):

* `bg <color>`
* `fg <color>`
* `st <style1> <style2> ...`
* `shift up|down|left|right <n>`
* `insert-text <text>`
* `clr`
* `clrall`
* `fill <char>`

---

# CI

The project uses GitHub Actions to automatically run tests.
GitHub Actions is included in the repository.

Workflow file:

`.github/workflows/maven-tests.yml`

Each time code is pushed or a pull request is created:

1. GitHub Actions runs on Linux
2. Maven builds the project
3. Unit tests are executed
