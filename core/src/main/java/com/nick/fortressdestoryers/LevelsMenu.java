package com.nick.fortressdestoryers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class LevelsMenu implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private Stage stage;
    private Texture levelsMenuBackground;
    private Texture texture;
    private BitmapFont font;
    public Sound clickSound;


    public LevelsMenu(Main game, SpriteBatch batch, OrthographicCamera camera, Sound clicksSound){
        this.game = game;
        this.batch = batch;
        this.camera = camera;
        this.clickSound = clicksSound;
    }
    @Override
    public void show() {
        stage = new Stage(new FitViewport(1366f,768f),batch);
        Gdx.input.setInputProcessor(stage);
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        texture = new Texture(pixmap);
        font = new BitmapFont();
        font.getData().setScale(3f);
        levelsMenuBackground = new Texture(Gdx.files.internal("levelsbackground.jpg"));

        TextButton.TextButtonStyle returnStyle = new TextButton.TextButtonStyle();
        returnStyle.font = font;
        returnStyle.fontColor = Color.BLACK;
        returnStyle.up = new TextureRegionDrawable(new TextureRegion(texture)).tint(new Color(0.8f, 0.2f, 0.2f, 1f));
        returnStyle.down = new TextureRegionDrawable(new TextureRegion(texture)).tint(new Color(0.4f, 0.1f, 0.1f, 1f));

        TextButton returnButton = new TextButton("Return to Main Menu", returnStyle);
        returnButton.addListener(new ClickListener() {
            public void clicked(InputEvent event,float x, float y) {
                clickSound.play(1f);
                game.setScreen( new MainMenu(game,game.camera,game.batch));
            }
        });

        returnButton.setPosition(50f,20f);
        stage.addActor(returnButton);

        BitmapFont headerFont = new BitmapFont();
        headerFont.getData().setScale(5f);

        Label.LabelStyle labelStyle = new Label.LabelStyle(headerFont, Color.BLACK);
        labelStyle.background = new TextureRegionDrawable(new TextureRegion(texture)).tint(new Color(1, 0, 1, 0.7f));
        Label levelsTitle = new Label("Select Level", labelStyle);
        levelsTitle.setAlignment(Align.center);
        levelsTitle.setSize(400f, 100f);
        levelsTitle.setPosition(50f,650f);

        stage.addActor(levelsTitle);

    }

    @Override
    public void render(float v) {
        ScreenUtils.clear(0, 0, 0, 1);
        game.viewport.apply(true);
        game.batch.begin();
        batch.setProjectionMatrix(camera.combined);
        game.batch.draw(levelsMenuBackground,0,0,16f,9f);
        game.batch.end();

        stage.getViewport().apply(true);
        stage.act(v);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
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
        stage.dispose();
        levelsMenuBackground.dispose();
        font.dispose();
        texture.dispose();

    }

}
