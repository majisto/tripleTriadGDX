package com.majisto.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.majisto.game.logic.Card;
import com.opencsv.bean.CsvToBeanBuilder;

import java.util.List;

public class TripleTriad extends Game {
	public List<Card> masterCardList;

	private MainMenuScreen menuScreen;
	private GameScreen mainScreen;
	private PreferencesScreen preferencesScreen;
	private AppPreferences preferences;
	private LoadingScreen loadingScreen = new LoadingScreen(this);
	public TTAssetManager assMan = new TTAssetManager();

	public final static int MENU = 0;
	public final static int PREFERENCES = 1;
	public final static int APPLICATION = 2;

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
		}
	}

	public AppPreferences getPreferences(){
		return this.preferences;
	}

	@Override
	public void dispose() {
		assMan.manager.dispose();
		super.dispose();
	}
}
