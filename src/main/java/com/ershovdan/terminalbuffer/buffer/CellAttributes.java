package com.ershovdan.terminalbuffer.buffer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

final class CellAttributes {
    static final CellAttributes DEFAULT = new CellAttributes("none", "none", List.of());

    private final String foreground;
    private final String background;
    private final List<String> style;

    CellAttributes(String foreground, String background, List<String> style) {
        this.foreground = foreground;
        this.background = background;
        this.style = Collections.unmodifiableList(new ArrayList<>(style));
    }

    String getForeground() {
        return foreground;
    }

    String getBackground() {
        return background;
    }

    List<String> getStyle() {
        return style;
    }

    boolean isDefault() {
        return this == DEFAULT || ("none".equals(foreground) && "none".equals(background) && style.isEmpty());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CellAttributes that)) return false;
        return Objects.equals(foreground, that.foreground)
                && Objects.equals(background, that.background)
                && Objects.equals(style, that.style);
    }

    @Override
    public int hashCode() {
        return Objects.hash(foreground, background, style);
    }
}
