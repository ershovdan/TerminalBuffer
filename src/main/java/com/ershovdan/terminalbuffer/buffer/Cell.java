package com.ershovdan.terminalbuffer.buffer;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    private char character;
    private String foreground;
    private String background;
    private List<String> style;


    public Cell(char character) {
        this.character = character;
        this.style = new ArrayList<>();
        this.foreground = "default";
        this.background = "default";
    }

    public String getForeground() {
        return foreground;
    }

    public String getBackground() {
        return background;
    }

    public List<String> getStyle() {
        return style;
    }

    public char getCharacter() {
        return character;
    }

    public Cell setCharacter(char character) {
        this.character = character;
        return this;
    }

    public Cell setStyle(List<String> style) {
        this.style = style;
        return this;
    }

    public Cell setForeground(String foreground) {
        this.foreground = foreground;
        return this;
    }

    public Cell setBackground(String background) {
        this.background = background;
        return this;
    }
}
