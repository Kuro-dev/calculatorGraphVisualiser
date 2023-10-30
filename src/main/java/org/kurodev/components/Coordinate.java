package org.kurodev.components;

import java.util.Objects;

public final class Coordinate {
    private final double x;
    private final double y;
    private final String label;

    public Coordinate(double x, double y) {
        this(x, y, "");
    }

    public Coordinate(double x, double y, String label) {
        this.x = x;
        this.y = y;
        this.label = label;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public String label() {
        return label;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Coordinate) obj;
        return this.x == that.x &&
                this.y == that.y &&
                Objects.equals(this.label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, label);
    }

    @Override
    public String toString() {
        return "Coordinate[" +
                "x=" + x + ", " +
                "y=" + y + ", " +
                "label=" + label + ']';
    }

}
