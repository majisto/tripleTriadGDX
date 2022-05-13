package com.majisto.game.logic;

import lombok.Data;

/**
 * tripleTriad Created by Majisto on 5/2/2022.
 */
@Data
public class AiEntry{

    public PositionToOtherCard positionToHumanCard; //0 -- Above, 1 -- Right, 2 -- Below, 3 -- Left
    public Card card;
    public Position position;

    public AiEntry(int row, int column, PositionToOtherCard pos, Card card) {
        this.position = new Position(row, column);
        this.positionToHumanCard = pos;
        this.card = card;
    }

    public AiEntry(int row, int column, PositionToOtherCard pos) {
        this.position = new Position(row, column);
        this.positionToHumanCard = pos;
    }
}
