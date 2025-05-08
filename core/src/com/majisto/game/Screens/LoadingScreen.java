package com.majisto.game.Screens;

import com.badlogic.gdx.Screen;
import com.majisto.game.TripleTriad;

/**
 * tripleTriadGDX Created by Majisto on 5/4/2022.
 */
public class LoadingScreen implements Screen {

    private TripleTriad game;

    public LoadingScreen(TripleTriad game) {
        this.game = game;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        game.changeScreen(TripleTriad.MENU);
    }

    @Override
    public void resize(int width, int height) {

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

    }
}
