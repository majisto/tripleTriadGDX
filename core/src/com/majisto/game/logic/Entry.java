package com.majisto.game.logic;

import lombok.Builder;
import lombok.Data;

/**
 * tripleTriad Created by Majisto on 4/28/2022.
 */
@Data @Builder
public class Entry {
    public Card card;
    public Position position;
    public Players owner;

}
