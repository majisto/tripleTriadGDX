package com.majisto.game.logic;

import lombok.Data;

/**
 * tripleTriad Created by Majisto on 4/28/2022.
 */
@Data
public class Board {
    public Entry[][] board = new Entry[3][3];
    public int size;
}
