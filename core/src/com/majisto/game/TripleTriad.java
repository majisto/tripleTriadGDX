package com.majisto.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.majisto.game.Screens.CardSelectionScreen;
import com.majisto.game.Screens.GameScreen;
import com.majisto.game.Screens.LoadingScreen;
import com.majisto.game.Screens.MainMenuScreen;
import com.majisto.game.logic.Card;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.Getter;

import java.util.List;

public class TripleTriad extends Game {
	public List<Card> masterCardList;

	private MainMenuScreen menuScreen;
	private GameScreen mainScreen;
	private PreferencesScreen preferencesScreen;
	private CardSelectionScreen cardSelectionScreen;
	@Getter
	private AppPreferences preferences;
	private final LoadingScreen loadingScreen = new LoadingScreen(this);
	public TTAssetManager assMan = new TTAssetManager();

	public final static int MENU = 0;
	public final static int PREFERENCES = 1;
	public final static int APPLICATION = 2;
	public static final int CARD_SELECTION = 3;

	@Override
	public void create () {
		preferences = new AppPreferences();
		setScreen(loadingScreen);
		masterCardList = new CsvToBeanBuilder<Card>(Gdx.files.internal("cards.csv").reader())
				.withType(Card.class)
				.build()
				.parse();
		assMan.queueAddMusic();
		assMan.manager.finishLoading();
		assMan.queueAddSkins();
		assMan.manager.finishLoading();
	}

	public void changeScreen(int screen) {
		switch (screen) {
			case MENU:
				if(menuScreen == null) menuScreen = new MainMenuScreen(this);
				setScreen(menuScreen);
				break;
			case PREFERENCES:
				if(preferencesScreen == null) preferencesScreen = new PreferencesScreen(this);
				setScreen(preferencesScreen);
				break;
			case APPLICATION:
				if(mainScreen == null) mainScreen = new GameScreen(this);
				setScreen(mainScreen);
				break;
			case CARD_SELECTION:
				if(cardSelectionScreen == null) cardSelectionScreen = new CardSelectionScreen(this);
				setScreen(cardSelectionScreen);
				break;
		}
	}

	@Override
	public void dispose() {
		assMan.manager.dispose();
		super.dispose();
	}
}
