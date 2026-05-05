package com.ershovdan.terminalbuffer.buffer;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class Cell {
    private char character;
    private CellAttributes attributes;
    private final Consumer<Cell> onChange;


    public Cell(char character) {
        this.character = character;
        this.attributes = CellAttributes.DEFAULT;
        this.onChange = cell -> {};
    }

    Cell(char character, CellAttributes attributes) {
        this(character, attributes, cell -> {});
    }

    Cell(char character, CellAttributes attributes, Consumer<Cell> onChange) {
        this.character = character;
        this.attributes = attributes;
        this.onChange = onChange;
    }

    public String getForeground() {
        return attributes.getForeground();
    }

    public String getBackground() {
        return attributes.getBackground();
    }

    public List<String> getStyle() {
        return attributes.getStyle();
    }

    public char getCharacter() {
        return character;
    }

    public Cell setCharacter(char character) {
        this.character = character;
        onChange.accept(this);
        return this;
    }

    public Cell setStyle(List<String> style) {
        this.attributes = new CellAttributes(getForeground(), getBackground(), style);
        onChange.accept(this);
        return this;
    }

    public Cell setForeground(String foreground) {
        this.attributes = new CellAttributes(foreground, getBackground(), getStyle());
        onChange.accept(this);
        return this;
    }

    public Cell setBackground(String background) {
        this.attributes = new CellAttributes(getForeground(), background, getStyle());
        onChange.accept(this);
        return this;
    }

    CellAttributes getAttributes() {
        return attributes.isDefault() ? CellAttributes.DEFAULT : attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell cell)) return false;
        return character == cell.character && Objects.equals(attributes, cell.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(character, attributes);
    }
}
