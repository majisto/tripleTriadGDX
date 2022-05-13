package com.majisto.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.majisto.game.logic.*;

import java.util.List;
import java.util.*;

/**
 * tripleTriadGDX Created by Majisto on 5/4/2022.
 */
public class GameScreen implements Screen {
    private final TripleTriad game;
    private final Music boogie;
    private final Stage stage;

    private Hand playerHand = new Hand();
    private Hand computerHand = new Hand();
    Board board;
    private Skin skin;
    private Skin crispySkin;
    final HashMap<String, Sprite> sprites = new HashMap<>();
    TextureAtlas originalAtlas;
    Card selectedCard = new Card();
    Button selectedButton;
    CheckBox selectedCheckbox;
    private ArrayList<CheckBox> checkBoxes = new ArrayList<>();
    private Table boardTable;
    private Table computerHandTable;
    Map<Position, List<TTButton>> neighborMap = new LinkedHashMap<>(); //Actors adjacent to index.
    Map<Position, TTButton> positionActorMap = new HashMap<>();
    private String playerScore = "5";
    private String computerScore = "5";
    private Label playerScoreLabel;
    private Label computerScoreLabel;

    public GameScreen(TripleTriad game) {

        originalAtlas = new TextureAtlas("ttOriginal.txt");
        Array<TextureAtlas.AtlasRegion> regions = originalAtlas.getRegions();
        Array.ArrayIterator<TextureAtlas.AtlasRegion> atlasRegions = new Array.ArrayIterator<>(regions);

        for (TextureAtlas.AtlasRegion region : atlasRegions) {
            Sprite sprite = originalAtlas.createSprite(region.name);
            sprites.put(region.name, sprite);
        }

        this.game = game;
        board = new Board();
        stage = new Stage(new ScreenViewport());
        boogie = game.assMan.manager.get("shuffleOrBoogie.mp3");
        boogie.setLooping(true);
        boogie.play();
    }

    @Override
    public void show() {
        stage.clear();

        skin = game.assMan.manager.get("skins/star-soldier/skin/star-soldier-ui.json");
        crispySkin = game.assMan.manager.get("skins/clean-crispy/skin/clean-crispy-ui.json");

        buildUIButtonsAndBoard();
        Gdx.input.setInputProcessor(stage);
    }

    private void buildUIButtonsAndBoard () {
        buildBoard();
        buildPlayerHandButtons();
        buildComputerHandImages();
        buidScoreLabels();
    }

    private void buidScoreLabels() {
        playerScoreLabel = new Label(playerScore, skin);
        playerScoreLabel.setFontScale(7f, 7f);
        playerScoreLabel.setColor(Color.BLUE);
        playerScoreLabel.setPosition(stage.getViewport().getScreenWidth() * 0.7f, stage.getHeight() * 0.08f);
        stage.addActor(playerScoreLabel);
        computerScoreLabel = new Label(computerScore, skin);
        computerScoreLabel.setFontScale(7f, 7f);
        computerScoreLabel.setColor(Color.RED);
        computerScoreLabel.setPosition(stage.getViewport().getScreenWidth() * 0.2f, stage.getHeight() * 0.08f);
        stage.addActor(computerScoreLabel);
    }

    private void buildPlayerHandButtons() {

        Collections.shuffle(game.masterCardList);
        for (int i = 0; i < 5; i++) {
            Card card = game.masterCardList.get(i);
            playerHand.cards.put(card.getId(), card);
            playerHand.nameCard.put(card.getName(),card);
        }

        Table table = new Table();
        table.setName("Player_Hand_Buttons");
        table.setColor(skin.getColor("white"));
        table.padLeft(0.0f);
        table.padTop(0.0f);
        table.padRight(23.0f);
        table.padBottom(0.0f);
        table.align(Align.right);
        table.setFillParent(true);

        for (Card card: playerHand.getCards().values()) {

            CheckBox checkBox = new CheckBox("", crispySkin);
            checkBoxes.add(checkBox);
            checkBox.setDisabled(true);
            table.add(checkBox);

            Sprite sprite = sprites.get(card.getName());
            SpriteDrawable spriteDrawable = new SpriteDrawable(sprite);
            Button textButton = new Button(spriteDrawable);
            textButton.setName(card.getName());
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    clearCheckboxes();
                    checkBox.toggle();
                    selectedButton = (Button) actor;
                    selectedCheckbox = checkBox;
                    selectedCard = playerHand.nameCard.get(actor.getName());
                }
            });
            table.add(textButton);
            table.row();
        }

        table.row();
        stage.addActor(table);
    }

    private void captureOpponents (Position position, Card card, Players player) {
        List<TTButton> actors = neighborMap.get(position);
        Entry attackerEntry = Entry.builder().card(card).position(position).build();
        for (TTButton defenderButton : actors) {
            if (defenderButton.getCard() == null) continue;;
            Entry defenderEntry = Entry.builder().card(defenderButton.getCard()).position(defenderButton.getPosition()).build();
            boolean b = calculateCapture(attackerEntry, defenderEntry);
            if (b) {
                //Capture card.
                if (player == Players.HUMAN) {
                    int score = Integer.parseInt(playerScore);
                    score += 1;
                    playerScore = String.valueOf(score);
                    playerScoreLabel.setText(playerScore);
                    int aiscore = Integer.parseInt(computerScore);
                    aiscore -= 1;
                    computerScore = String.valueOf(aiscore);
                    computerScoreLabel.setText(computerScore);
                    defenderButton.setOwner(Players.HUMAN);
                    Sprite sprite = sprites.get(defenderButton.getCard().getName());
                    SpriteDrawable spriteDrawable = new SpriteDrawable(sprite);
                    defenderButton.getStyle().imageUp = new SpriteDrawable(spriteDrawable);
                    defenderButton.getStyle().imageDown = new SpriteDrawable(spriteDrawable);
                }
                else if (player == Players.COMPUTER) {
                    int score = Integer.parseInt(playerScore);
                    score -= 1;
                    playerScore = String.valueOf(score);
                    playerScoreLabel.setText(playerScore);
                    int aiscore = Integer.parseInt(computerScore);
                    aiscore += 1;
                    computerScore = String.valueOf(aiscore);
                    computerScoreLabel.setText(computerScore);
                    defenderButton.setOwner(Players.COMPUTER);
                    Sprite sprite = sprites.get(defenderButton.getCard().getName()+"CPU");
                    SpriteDrawable spriteDrawable = new SpriteDrawable(sprite);
                    defenderButton.getStyle().imageUp = spriteDrawable;
                    defenderButton.getStyle().imageDown = spriteDrawable;
                    defenderButton.getStyle().imageChecked = spriteDrawable;
                    defenderButton.getStyle().imageCheckedDown = spriteDrawable;

                }
            }
        }

    }

    private boolean calculateCapture(Entry attacker, Entry defender) {
        Card aCard = attacker.getCard();
        Card dCard = defender.getCard();
        if (attacker.position.row == defender.position.row) { //Compare L/R values
            if (attacker.position.column - defender.position.column >= 0) {
                //Attacker is on right
                return aCard.left - dCard.right > 0;
            } else {
                return aCard.right - dCard.left > 0;
            }
        } else {
            if (attacker.position.row - defender.position.row >= 0) {
                return aCard.upper - dCard.lower > 0;
            } else {
                return aCard.lower - dCard.upper > 0;
            }
        }
    }

    private void clearCheckboxes() {
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setChecked(false);
        }
    }

    private void buildComputerHandImages () {

        Collections.shuffle(game.masterCardList);
        for (int i = 0; i < 5; i++) {
            Card card = game.masterCardList.get(i);
            computerHand.cards.put(card.getId(), card);
            computerHand.nameCard.put(card.getName(),card);
        }

        Table table = new Table();
        table.setName("Computer_Hand_Buttons");
        table.setColor(skin.getColor("white"));
        table.align(Align.left);
        table.setFillParent(true);

        for (Card card: computerHand.getNameCard().values()) {
            Image image = new Image(sprites.get(card.getName()+"CPU"));
            image.setName(card.getName());
            table.add(image);
            table.row();
        }

        computerHandTable = table;
        stage.addActor(table);
    }

    private void buildBoard() {
        Table table = new Table().align(Align.center);
        table.setName("Board");
        table.setFillParent(true);
        Sprite blank = sprites.get("blank.tex");
        TextureRegionDrawable blankDrawable = new TextureRegionDrawable(new TextureRegion(blank));

        TTButton buttonImage;

        for (int i = 0; i < 9; i++) {
            buttonImage = new TTButton(blankDrawable);
            buttonImage.setName(String.valueOf(i));
            Position position = null;
            switch (i) {
                case 0:
                    position = new Position(0, 0);
                    break;
                case 1:
                    position = new Position(0, 1);
                    break;
                case 2:
                    position = new Position(0, 2);
                    break;
                case 3:
                    position = new Position(1, 0);
                    break;
                case 4:
                    position = new Position(1,1);
                    break;
                case 5:
                    position = new Position(1,2);
                    break;
                case 6:
                    position = new Position(2,0);
                    break;
                case 7:
                    position = new Position(2,1);
                    break;
                case 8:
                    position = new Position(2,2);
                    break;
            }
            neighborMap.put(position, new ArrayList<>());
            positionActorMap.put(position, buttonImage);
            buttonImage.setPosition(position);
            buttonImage.addListener(new ClickListener(){
                @Override
                public void clicked (InputEvent event, float x, float y) {
                    TTButton imageButton = (TTButton) event.getListenerActor();
                    if (selectedCard == null || imageButton.getCard() != null) return;
                    imageButton.setCard(selectedCard);
                    Sprite sprite = sprites.get(selectedCard.getName());
                    selectedButton.remove();
                    selectedCheckbox.remove();
                    clearCheckboxes();
                    captureOpponents(imageButton.getPosition(), selectedCard, Players.HUMAN);
                    process_ai(imageButton);
                    selectedCard = null;
//                    sprite.setColor(Color.FIREBRICK);
                    SpriteDrawable spriteDrawable = new SpriteDrawable(sprite);
                    imageButton.getStyle().imageUp = new SpriteDrawable(spriteDrawable);
                    imageButton.getStyle().imageDown = new SpriteDrawable(spriteDrawable);
                }
            });
            table.add(buttonImage);
            if (i == 2 || i == 5) {
                table.row();
            }
        }
        boardTable = table;
        buildNeighborMap();
        stage.addActor(table);
    }

    private void process_ai (TTButton button) {
        Entry entry = Entry.builder().card(button.getCard()).position(button.getPosition()).owner(Players.COMPUTER).build();
        List<AiEntry> aiEntries = new ArrayList<>();
        Game.buildEntryChecklist(entry, aiEntries);
        Card strongestPlay = null;
        Position positionToPlay = null;
        for (AiEntry aiEntry : aiEntries) {
            if (positionActorMap.get(aiEntry.getPosition()).getCard() != null) {
                continue;
            }
            strongestPlay = Game.findStrongestPlay(aiEntry, entry, computerHand);
            positionToPlay = aiEntry.getPosition();
            if (strongestPlay != null) {
                break;
            }
        }
        if (strongestPlay == null) {
            //Pick random spot for card.
            for (TTButton ttButton : positionActorMap.values()) {
                if (ttButton.card == null) {
                    //found spot to put card.
                    positionToPlay = ttButton.position;
                    strongestPlay = computerHand.getRandomCard();
                }
            }
        }
        TTButton aiButton = positionActorMap.get(positionToPlay);
        aiButton.setCard(strongestPlay);
        Sprite sprite = sprites.get(strongestPlay.getName()+"CPU");
        aiButton.getStyle().imageDown = new SpriteDrawable(sprite);
        aiButton.getStyle().imageUp = new SpriteDrawable(sprite);
        computerHandTable.removeActor(computerHandTable.findActor(strongestPlay.getName()));
        computerHand.getCards().remove(strongestPlay.getId());
        captureOpponents(positionToPlay, strongestPlay, Players.COMPUTER);
    }

    private void buildNeighborMap() {
        int i = 0;
        for (List<TTButton> actorList : neighborMap.values()) {
            switch (i++) {
                case 0:
                    actorList.add(boardTable.findActor("1"));
                    actorList.add(boardTable.findActor("3"));
                    break;
                case 1:
                    actorList.add(boardTable.findActor("0"));
                    actorList.add(boardTable.findActor("2"));
                    actorList.add(boardTable.findActor("4"));
                    break;
                case 2:
                    actorList.add(boardTable.findActor("1"));
                    actorList.add(boardTable.findActor("5"));
                    break;
                case 3:
                    actorList.add(boardTable.findActor("0"));
                    actorList.add(boardTable.findActor("4"));
                    actorList.add(boardTable.findActor("6"));
                    break;
                case 4:
                    actorList.add(boardTable.findActor("1"));
                    actorList.add(boardTable.findActor("3"));
                    actorList.add(boardTable.findActor("5"));
                    actorList.add(boardTable.findActor("7"));
                    break;
                case 5:
                    actorList.add(boardTable.findActor("2"));
                    actorList.add(boardTable.findActor("4"));
                    actorList.add(boardTable.findActor("8"));
                    break;
                case 6:
                    actorList.add(boardTable.findActor("3"));
                    actorList.add(boardTable.findActor("7"));
                    break;
                case 7:
                    actorList.add(boardTable.findActor("4"));
                    actorList.add(boardTable.findActor("6"));
                    actorList.add(boardTable.findActor("8"));
                    break;
                case 8:
                    actorList.add(boardTable.findActor("5"));
                    actorList.add(boardTable.findActor("7"));
                    break;
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        playerScoreLabel.setPosition(stage.getViewport().getScreenWidth() * 0.7f, stage.getHeight() * 0.08f);
        computerScoreLabel.setPosition(stage.getViewport().getScreenWidth() * 0.2f, stage.getHeight() * 0.08f);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        boogie.dispose();
        stage.dispose();
    }
}