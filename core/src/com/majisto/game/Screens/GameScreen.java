package com.majisto.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.majisto.game.CardSelection;
import com.majisto.game.TTButton;
import com.majisto.game.TripleTriad;
import com.majisto.game.logic.*;

import java.util.*;
import java.util.List;

/**
 * tripleTriadGDX Created by Majisto on 5/4/2022.
 */
public class GameScreen implements Screen {
    public final TripleTriad game;
    private final Music boogie;
    private final Music victoryMusic;
    private final Stage stage;

    public Hand playerHand = new Hand();
    private Hand computerHand = new Hand();
    private final Skin skin;
    private final Skin crispySkin;
    final HashMap<String, Sprite> sprites = new HashMap<>();
    final HashMap<String, HashMap<String, Sprite>> spriteMap;
    Card selectedCard;
    Button selectedButton;
    CheckBox selectedCheckbox;
    private ArrayList<CheckBox> checkBoxes;
    private Table boardTable;
    private Table computerHandTable;
    Map<Position, List<TTButton>> neighborMap; //Actors adjacent to index.
    Map<Position, TTButton> positionActorMap;
    private Integer playerScore = 5;
    private Integer computerScore = 5;
    private Label playerScoreLabel;
    private Label computerScoreLabel;
    private Label infoLabel;
    private VerticalGroup cpuVerticalGroup;
    private VerticalGroup humanVerticalGroup;
    private Label endOfGameMessage;
    private TextButton newGameButton;
    private CardSelection cardSelection;

    public GameScreen(TripleTriad game) {

        spriteMap = new HashMap<>();
        TextureAtlas originalAtlas = new TextureAtlas("ttOriginal.txt");
        Array<TextureAtlas.AtlasRegion> regions = originalAtlas.getRegions();
        Array.ArrayIterator<TextureAtlas.AtlasRegion> atlasRegions = new Array.ArrayIterator<>(regions);

        for (TextureAtlas.AtlasRegion region : atlasRegions) {
            Sprite sprite = originalAtlas.createSprite(region.name);
            sprites.put(region.name, sprite);
        }

        this.game = game;
        stage = new Stage(new ScreenViewport());
        boogie = game.assMan.manager.get("shuffleOrBoogie.mp3");
        victoryMusic = game.assMan.manager.get("victoryMusic.mp3");
        skin = game.assMan.manager.get("skins/star-soldier/skin/star-soldier-ui.json");
        crispySkin = game.assMan.manager.get("skins/clean-crispy/skin/clean-crispy-ui.json");
        boogie.setLooping(true);
    }

    @Override
    public void show() {
        stage.clear();
        boogie.play();
        positionActorMap = new HashMap<>();
        neighborMap = new LinkedHashMap<>();
        checkBoxes = new ArrayList<>();
        playerHand = new Hand();
        computerHand = new Hand();
        cardSelection = CardSelection.builder().stage(stage).skin(skin).sprites(sprites).build();

        buildUIButtonsAndBoard();
        Gdx.input.setInputProcessor(stage);
    }

    private void buildUIButtonsAndBoard () {
//        cardSelection.buildCardSelectionList();
        buildBoard();
        buildPlayerHandButtons();
        buildComputerHandImages();
        buildScoreLabels();
    }

    private void buildScoreLabels() {
        humanVerticalGroup = new VerticalGroup();
        Label humanLabel = new Label("HUMAN", skin);
        humanLabel.setColor(Color.BLUE);
        humanLabel.setFontScale(2f);
        playerScoreLabel = new Label(String.valueOf(playerScore), skin);
        playerScoreLabel.setFontScale(7f, 7f);
        playerScoreLabel.setColor(Color.BLUE);
        humanVerticalGroup.setPosition(stage.getViewport().getScreenWidth() * 0.7f, stage.getHeight() * 0.2f);
        humanVerticalGroup.addActor(humanLabel);
        humanVerticalGroup.addActor(playerScoreLabel);
        stage.addActor(humanVerticalGroup);


        cpuVerticalGroup = new VerticalGroup();
        Label computerLabel = new Label("CPU", skin);
        computerLabel.setColor(Color.RED);
        computerLabel.setFontScale(2f);
        computerScoreLabel = new Label(String.valueOf(computerScore), skin);
        computerScoreLabel.setFontScale(7f, 7f);
        computerScoreLabel.setColor(Color.RED);
        cpuVerticalGroup.setPosition(stage.getViewport().getScreenWidth() * 0.25f, stage.getHeight() * 0.2f);
        cpuVerticalGroup.addActor(computerLabel);
        cpuVerticalGroup.addActor(computerScoreLabel);
        stage.addActor(cpuVerticalGroup);

        infoLabel = new Label("", skin);
        infoLabel.setFontScale(1.5f);
        infoLabel.setPosition(stage.getWidth() * 0.32f, stage.getHeight() * 0.1f);
        stage.addActor(infoLabel);

        newGameButton = new TextButton("New Game?", skin);
        newGameButton.setPosition(stage.getWidth() * 0.2f, stage.getHeight() * 0.8f);
        newGameButton.setVisible(false);
        newGameButton.setTransform(true);
        newGameButton.setScale(4f);
        newGameButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startNewGame();
            }
        });
        stage.addActor(newGameButton);
    }

    private void startNewGame () {
        newGameButton.setVisible(false);
        endOfGameMessage.setVisible(false);
        computerScore = 5;
        playerScore = 5;
        updateScores();
        if (victoryMusic.isPlaying()) victoryMusic.stop();
        show();
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
                    infoLabel.setText("Selected: " + selectedCard.getName());
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
            if (defenderButton.getCard() == null || defenderButton.getOwner().equals(player)) {
                continue;
            }
            Entry defenderEntry = Entry.builder().card(defenderButton.getCard()).position(defenderButton.getPosition()).build();
            boolean b = calculateCapture(attackerEntry, defenderEntry);
            if (b) {
                //Capture card.
                if (player == Players.HUMAN) {
                    playerScore++;
                    computerScore--;
                    updateDefenderButton(defenderButton, Players.HUMAN);
                }
                else if (player == Players.COMPUTER) {
                    playerScore--;
                    computerScore++;
                    updateDefenderButton(defenderButton, Players.COMPUTER);
                }
            }
        }
        updateScores();
    }

    private void updateDefenderButton(TTButton defenderButton, Players player) {
        Sprite sprite = sprites.get((player == Players.HUMAN ? defenderButton.getCard().getName() : (defenderButton.getCard().getName() + "CPU")));
        defenderButton.setOwner(player == Players.HUMAN ? Players.HUMAN : Players.COMPUTER);
        SpriteDrawable spriteDrawable = new SpriteDrawable(sprite);
        defenderButton.getStyle().imageUp = spriteDrawable;
        defenderButton.getStyle().imageDown = spriteDrawable;
        defenderButton.getStyle().imageChecked = spriteDrawable;
        defenderButton.getStyle().imageCheckedDown = spriteDrawable;
    }

    private void updateScores () {
        playerScoreLabel.setText(String.valueOf(playerScore));
        computerScoreLabel.setText(String.valueOf(computerScore));
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
//            Gdx.app.log("TEST", buttonImage.toString());
            buttonImage.addListener(new ClickListener(){
                @Override
                public void clicked (InputEvent event, float x, float y) {
                    TTButton imageButton = (TTButton) event.getListenerActor();
                    if (selectedCard == null || imageButton.getCard() != null) return;
                    infoLabel.setText("");
                    Action action = Actions.sequence(Actions.moveTo(imageButton.getX(), imageButton.getY(), 0.3f),
                            Actions.run(() -> {
                                playerHand.getCards().remove(selectedCard.getId());
                                selectedButton.remove();
                                imageButton.setOwner(Players.HUMAN);
                                imageButton.setCard(selectedCard);
                                selectedCheckbox.remove();
                                clearCheckboxes();
                                Sprite sprite = sprites.get(selectedCard.getName());
                                SpriteDrawable spriteDrawable = new SpriteDrawable(sprite);
                                imageButton.getStyle().imageUp = new SpriteDrawable(spriteDrawable);
                                imageButton.getStyle().imageDown = new SpriteDrawable(spriteDrawable);
                                captureOpponents(imageButton.getPosition(), selectedCard, Players.HUMAN);
                                if (computerHand.getCards().size() <= 1) {
                                    //End of game.
                                    determineVictory();
                                    return;
                                }
                                selectedCard = null;
                            }));
                    selectedButton.addAction(action);
                    com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task(){
                        @Override
                        public void run() {
                            process_ai(imageButton);
                        }
                    }, 1.5f);
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

    private void determineVictory () {
        int humanScore = playerScore;
        int cpuScore = computerScore;
        if (humanScore < cpuScore){
            endOfGameMessage = new Label("YOU LOSE!", skin);
            endOfGameMessage.setColor(Color.RED);
            endOfGameMessage.setFontScale(10f);
            endOfGameMessage.setPosition(endOfGameMessage.getWidth(), stage.getHeight() * 0.5f);
        } else if (cpuScore < humanScore) {
            endOfGameMessage = new Label("YOU WIN!", skin);
            endOfGameMessage.setColor(Color.BLUE);
            endOfGameMessage.setFontScale(10f);
            endOfGameMessage.setPosition(stage.getWidth() * 0.25f, stage.getHeight() * 0.5f);
            boogie.stop();
            victoryMusic.play();
        } else {
            //Draw
            endOfGameMessage = new Label("DRAW!", skin);
            endOfGameMessage.setColor(Color.RED);
            endOfGameMessage.setFontScale(10f);
            endOfGameMessage.setPosition(stage.getWidth() * 0.25f, stage.getHeight() * 0.5f);
        }
        stage.addActor(endOfGameMessage);
        newGameButton.setVisible(true);
    }

    private void process_ai (TTButton button) {
        if (newGameButton.isVisible() || playerHand.getCards().size() == 5) {
            return;
        }
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
            if (strongestPlay == null) return; //should never happen ideally.
        }
        TTButton aiButton = positionActorMap.get(positionToPlay);
        aiButton.setCard(strongestPlay);
        Actor handButton = computerHandTable.findActor(strongestPlay.getName());
        Card finalStrongestPlay = strongestPlay;
        Position finalPositionToPlay = positionToPlay;
        Action action = Actions.sequence(Actions.moveTo(aiButton.getX(), aiButton.getY(), 0.3f),
                Actions.run(() -> {
                    Sprite sprite = sprites.get(finalStrongestPlay.getName()+"CPU");
                    aiButton.getStyle().imageDown = new SpriteDrawable(sprite);
                    aiButton.getStyle().imageUp = new SpriteDrawable(sprite);
                    aiButton.setOwner(Players.COMPUTER);
                    computerHandTable.removeActor(computerHandTable.findActor(finalStrongestPlay.getName()));
                    computerHand.getCards().remove(finalStrongestPlay.getId());
                    captureOpponents(finalPositionToPlay, finalStrongestPlay, Players.COMPUTER);
                }));
        handButton.addAction(action);
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
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        humanVerticalGroup.setPosition(stage.getViewport().getScreenWidth() * 0.7f, stage.getHeight() * 0.2f);
        cpuVerticalGroup.setPosition(stage.getViewport().getScreenWidth() * 0.25f, stage.getHeight() * 0.2f);
        infoLabel.setPosition(stage.getViewport().getScreenWidth() * 0.32f, stage.getViewport().getScreenHeight() * .1f);
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
        victoryMusic.dispose();
        skin.dispose();
    }
}
