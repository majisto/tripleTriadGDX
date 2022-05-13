package com.majisto.game.logic;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import java.util.Objects;

/**
 * tripleTriad Created by Majisto on 4/28/2022.
 */
@Data
public class Card {

    @CsvBindByName
    public String name;

    @CsvBindByName
    public int upper;

    @CsvBindByName
    public int right;

    @CsvBindByName
    public int lower;

    @CsvBindByName
    public int left;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return getId() == card.getId();
    }

    @Override
    public String toString() {
        return String.format("Name: %s %d,%d,%d,%d ID: %s", name, upper, right, lower, left, id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }


    @CsvBindByName
    public Integer id;

}