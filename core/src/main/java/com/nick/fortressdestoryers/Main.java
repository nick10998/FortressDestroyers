package com.nick.fortressdestoryers;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FillViewport;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
   public FillViewport viewport;
   public OrthographicCamera camera;
   public SpriteBatch batch;
   public static final float worldWidth = 16f;
   public static final float worldHeight= 9f;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        viewport = new FillViewport(worldWidth,worldHeight, camera);
        batch = new SpriteBatch();
        this.setScreen(new MainMenu(this,camera, viewport,batch));

    }

    @Override
    public void render() {
        viewport.apply();
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
