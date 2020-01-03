package com.hexminds.sparrow.lexer;

public class Position implements Comparable<Position> {
    protected int line;
    protected int column;

    public Position() {
        line = 0;
        column = 0;
    }

    public Position(Position position) {
        line = position.line;
        column = position.column;
    }

    public Position(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
        return;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
        return;
    }

    public void incLine() {
        line++;
        return;
    }

    public void incLine(int step) {
        line += step;
        return;
    }

    public void decLine() {
        line--;
        return;
    }

    public void decLine(int step) {
        line -= step;
        return;
    }

    public void incColumn() {
        column++;
        return;
    }

    public void incColumn(int step) {
        column += step;
        return;
    }

    public void decColumn() {
        column--;
        return;
    }

    public void decColumn(int step) {
        column -= step;
        return;
    }

    public void set(int line, int column) {
        this.line = line;
        this.column = column;
        return;
    }

    public void set(Position position) {
        line = position.line;
        column = position.column;
        return;
    }

    public int compareTo(Position o) {
        if (line < o.line || line == o.line && column < o.column)
            return -1;
        else if (line == o.line && column == o.column)
            return 0;
        else
            return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Position))
            return false;

        if (obj == this)
            return true;

        if (((Position) obj).line == line && ((Position) obj).column == column)
            return true;

        return false;
    }

    @Override
    public int hashCode() {
        return line * 31 + column;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", line + 1, column + 1);
    }
}
