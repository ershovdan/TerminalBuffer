package com.ershovdan.terminalbuffer.buffer;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    private char character;
    private String foreground;
    private String background;
    private List<String> style;


    Cell(char character) {
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

    public void setCharacter(char character) {
        this.character = character;
    }

    public void setStyle(List<String> style) {
        this.style = style;
    }

    public void setForeground(String foreground) {
        this.foreground = foreground;
    }

    public void setBackground(String background) {
        this.background = background;
    }
}
