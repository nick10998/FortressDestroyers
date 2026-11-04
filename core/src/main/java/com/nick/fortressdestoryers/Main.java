package com.nick.fortressdestoryers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FillViewport;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
   public FillViewport viewport;
   public OrthographicCamera camera;
   public SpriteBatch batch;
   public static final float menuWidth = 16f;
   public static final float menuHeight= 9f;
   private Music backgroundMusic;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        viewport = new FillViewport(menuWidth,menuHeight, camera);
        batch = new SpriteBatch();
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("skyrimmusic.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.7f);
        if (backgroundMusic != null && !backgroundMusic.isPlaying()){
            backgroundMusic.play();
        }
        try {
            new Database(DatabaseConnection.connect());
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.setScreen(new MainMenu(this,camera,batch));

    }

    @Override
    public void render() {
        viewport.apply();
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (backgroundMusic != null) {
            backgroundMusic.dispose();
        }
    }
}
