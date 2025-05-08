package com.majisto.game;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.majisto.game.logic.Card;
import com.majisto.game.logic.Entry;
import com.majisto.game.logic.Players;
import com.majisto.game.logic.Position;

/**
 * tripleTriadGDX Created by Majisto on 5/11/2022.
 */
public class TTButton extends ImageButton {

    public Card card;
    public Position position;

    @Override
    public String toString() {
        return "TTButton{" +
                "card=" + card +
                ", position=" + position +
                ", entry=" + entry +
                ", owner=" + owner +
                '}';
    }

    public Entry entry;
    public Players getOwner() {
        return owner;
    }

    public void setOwner(Players owner) {
        this.owner = owner;
    }

    public Players owner;

    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public TTButton(Skin skin) {
        super(skin);
    }

    public TTButton(Skin skin, String styleName) {
        super(skin, styleName);
    }

    public TTButton(ImageButtonStyle style) {
        super(style);
    }

    public TTButton(Drawable imageUp) {
        super(imageUp);
    }

    public TTButton(Drawable imageUp, Drawable imageDown) {
        super(imageUp, imageDown);
    }

    public TTButton(Drawable imageUp, Drawable imageDown, Drawable imageChecked) {
        super(imageUp, imageDown, imageChecked);
    }
}
