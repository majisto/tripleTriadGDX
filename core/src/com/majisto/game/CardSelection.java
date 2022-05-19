package com.majisto.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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

    protected AutoFocusScrollPane scrollPane;
    protected List<Label> cardSelectionList;
    protected Table container;
    protected Table cardSelectionTable;
    protected final Stage stage;
    protected final Skin skin;
    protected final HashMap<String, Sprite> sprites;

    public void buildCardSelectionList() {
        container = new Table();
        cardSelectionTable = new Table();
        scrollPane = new AutoFocusScrollPane(cardSelectionTable);
        container.add(scrollPane).width(stage.getWidth() / 2).height(200f);
        container.setPosition(300f, 500f);
        container.row();

        cardSelectionList = new ArrayList<>();
        for (String name: sprites.keySet()) {
            Label label = new Label(name, skin);
            cardSelectionList.add(label);
            cardSelectionTable.add(label);
            cardSelectionTable.row();
        }
        stage.addActor(container);
    }
}
