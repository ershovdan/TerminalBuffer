# Terminal Buffer (Java)

---

## Overview

This project implements a terminal screen buffer in Java.
It simulates how terminal emulators store characters, cursor position, and styling before rendering text to the screen.

The buffer stores characters in a 2 dimensional grid of cells, where each cell contains:

* a character
* foreground color
* background color
* text style

Application also supports:

* cursor movement
* writing characters
* line operations
* clearing the screen
* style handling

### Classes

#### TerminalBuffer

The core component that manages:

* the screen buffer
* cursor position
* scrollback
* writing characters
* screen manipulation operations


#### Cell

Represents a single character cell in the terminal grid.

Each cell stores:

* character
* foreground color
* background color
* text style

#### Terminal

Acts as a higher level interface to the buffer and handles terminal behavior.

#### StyleHandler

Handles parsing and applying style codes.

---

## Build & run
* Java 21 is used by default
* Maven framework is used
* Compile application with `mvn clean compile`
* Run with `mvn exec:java`

---

# How the Buffer Works

The buffer is implemented as a 2D array of cells.

Example:

```
+---+---+---+---+---+
| H | e | l | l | o |
+---+---+---+---+---+
|               |
+---+---+---+---+---+
```

Each time a character is written:

1. The character is stored in the current cursor cell
2. The cursor moves right
3. If the cursor reaches the end of a line, it may wrap or stay within bounds.

---

# Cursor Operations

The cursor represents the current writing position.

Supported operations:


`shiftCursorRight(n)`
`shiftCursorLeft(n)`
`shiftCursorUp(n)`
`shiftCursorDown(n)`
`setCursorPosition(x, y)`


The cursor position is always locked inside the buffer bounds.

---

# Screen Operations

The buffer supports several screen manipulation functions.

### Write Character

`setChar(char c)`

Writes a character to the active cell and moves the cursor.


### Return Operation

`returnOperation()`

Moves the cursor to:

```
x = 0
y = next line
```

If the cursor is on the last row, scrolling may occur.

### Backspace

`backspaceOperation()`


Moves the cursor left and clears the character.


### Fill Line

`fillLine(char c)`

Fills the entire current row with the specified character.


### Clear Screen

`clearScreen()`

Clears all visible cells but keeps the cursor position.


### Clear All

`clearAll()`

Resets:

* entire buffer
* cursor position
* styles

---

# Styling

Each cell can contain styling information.

Supported style properties include:

* foreground color
* background color
* text style

Example:

`terminalBuffer.setCurrentFg("green");`
`terminalBuffer.setCurrentBg("black");`
`terminalBuffer.setCurrentStyle(List.of("b"));`

All characters written afterward inherit the current style.

---

# Running Tests

Run tests with Maven:

`mvn test`

This will execute all unit tests in the project.

---

# Continuous Integration

The project uses GitHub Actions to automatically run tests.

Workflow file:

`.github/workflows/maven-tests.yml`

Each time code is pushed or a pull request is created:

1. GitHub starts a Linux runner
2. Java is installed
3. Maven builds the project
4. Unit tests are executed

