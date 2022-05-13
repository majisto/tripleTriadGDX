package com.majisto.game.logic;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * tripleTriad Created by Majisto on 4/30/2022.
 */
@Getter
@Setter
public class Position {

    public int row;
    public int column;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return getRow() == position.getRow() && getColumn() == position.getColumn();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRow(), getColumn());
    }

    @Override
    public String toString() {
        return "Position{" +
                "row=" + row +
                ", column=" + column +
                '}';
    }
}
