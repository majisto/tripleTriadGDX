package com.majisto.game.logic;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * tripleTriad Created by Majisto on 4/28/2022.
 */
@Data
public class Hand {
    public Map<Integer, Card> cards = new HashMap<>();
    public Map<String, Card> nameCard = new HashMap<>();

    public Card getRandomCard() {
        return cards.values().iterator().next();
    }
}
