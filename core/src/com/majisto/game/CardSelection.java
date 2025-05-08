package com.majisto.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.majisto.game.Screens.GameScreen;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * tripleTriadGDX Created by Majisto on 5/18/2022.
 */
@Data @Builder
public class CardSelection {

    protected GameScreen game;
    protected AutoFocusScrollPane scrollPane;
    protected List<Label> cardSelectionList;
    protected Table container;
    protected Table cardSelectionTable;
    protected final Stage stage;
    protected final Skin skin;
    protected final HashMap<String, Sprite> sprites;
    protected List<String> selectedCards;
    @Builder.Default
    int chosenCards = 0;

    public void buildCardSelectionList() {
        int chosenCards = 0;
        container = new Table();
        cardSelectionTable = new Table();
        selectedCards = new ArrayList<>();
        scrollPane = new AutoFocusScrollPane(cardSelectionTable);
        container.add(scrollPane).width(stage.getWidth() / 2).height(200f);
        container.setPosition(300f, 500f);
        container.row();

        cardSelectionList = new ArrayList<>();
        for (String name: sprites.keySet()) {
            if (name.contains("CPU")) {
                continue;
            }
            Label label = new Label(name, skin);
            label.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Label clickedLabel = (Label) event.getListenerActor();
                    selectedCards.add(clickedLabel.getText().toString());
                    System.out.println(selectedCards);
                }
            });
            cardSelectionList.add(label);
            cardSelectionTable.add(label);
            cardSelectionTable.row();
        }
        stage.addActor(container);
//        Card card = game.masterCardList.get(i);i
//        playerHand.cards.put(card.getId(), card);
//        playerHand.nameCard.put(card.getName(),card);
    }
}
