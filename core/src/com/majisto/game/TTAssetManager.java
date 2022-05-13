package com.majisto.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * tripleTriadGDX Created by Majisto on 5/6/2022.
 */
public class TTAssetManager {
    public final AssetManager manager = new AssetManager();
    public final String playingSong = "shuffleOrBoogie.mp3";
    public final String skin = "skins/star-soldier/skin/star-soldier-ui.json";
    public final String skin2 = "skins/clean-crispy/skin/clean-crispy-ui.json";

    public void queueAddMusic() {
        manager.load(playingSong, Music.class);
    }
    public void queueAddSkins(){
        SkinLoader.SkinParameter params = new SkinLoader.SkinParameter("skins/star-soldier/skin/star-soldier-ui.atlas");
        manager.load(skin, Skin.class, params);
        SkinLoader.SkinParameter params2 = new SkinLoader.SkinParameter("skins/clean-crispy/skin/clean-crispy-ui.atlas");
        manager.load(skin2, Skin.class, params2);
    }
}
