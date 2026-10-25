package com.nick.fortressdestoryers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;


public class MainMenu implements Screen {
    private Texture background;
    private final Main game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private FillViewport viewport;
    private FitViewport pixelViewport;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private Stage stage;
    private Texture whiteTexture;
    private Sound clickSound;




    public MainMenu(final Main game, OrthographicCamera camera, FillViewport viewport, SpriteBatch batch) {
        this.game = game;
        this.camera= camera;
        this.viewport = viewport;
        this.batch = batch;
    }


    public void show() {
        pixelViewport = new FitViewport(1366f,768f);
        stage = new Stage(pixelViewport,batch);
        background = new Texture(Gdx.files.internal("background castle main.jpg"));
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(5f);



        Gdx.input.setInputProcessor(stage);
        //this makes pixels so I dont have to use skin.json
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whiteTexture = new Texture(pixmap);
        pixmap.dispose();

        //title
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.BLACK);
        Label gameTitle = new Label("Fortress Destroyers", labelStyle);
        gameTitle.setAlignment(Align.center);
        gameTitle.setWrap(true);
        //title box
        Table boxTitle = new Table();
        boxTitle.setBackground(new TextureRegionDrawable(new TextureRegion(whiteTexture)).tint(new Color(1, 0, 1, 0.7f)));
        boxTitle.add(gameTitle).width(500f).center().pad(25f);

        Table center = new Table();
        center.setFillParent(true);
        center.add(boxTitle).center().padBottom(150f);
        stage.addActor(center);

        //buttons
        Table buttonTable = new Table();
        clickSound = Gdx.audio.newSound(Gdx.files.internal("zapsplat_multimedia_button_press_plastic_click_001_36868.mp3"));

        TextButton.TextButtonStyle startStyle = new TextButton.TextButtonStyle();
        startStyle.font = font;
        startStyle.fontColor = Color.BLACK;
        startStyle.up = new TextureRegionDrawable(new TextureRegion(whiteTexture)).tint(new Color(0f, 0.8f, 0f, 1f));
        startStyle.down = new TextureRegionDrawable(new TextureRegion(whiteTexture)).tint(new Color(0f, 0.4f, 0f, 1f));
        TextButton startButton = new TextButton("Start", startStyle);
        center.row();
        buttonTable.add(startButton).width(300f).height(100f);

        startButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play(1.0f);
            }
        });

        //this makes space between both buttons
        buttonTable.add().width(200f);

        //stats
        TextButton.TextButtonStyle statsStyle = new TextButton.TextButtonStyle();
        statsStyle.font = font;
        statsStyle.fontColor = Color.BLACK;
        statsStyle.up = new TextureRegionDrawable(new TextureRegion(whiteTexture)).tint(new Color(0.8f, 0f, 0.8f, 1f));
        statsStyle.down = new TextureRegionDrawable(new TextureRegion(whiteTexture)).tint(new Color(0.4f, 0f, 0.4f, 1f));
        TextButton statisticsButton = new TextButton("Statistics", statsStyle);
        buttonTable.add(statisticsButton).width(300f).height(100f);

        statisticsButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play(1.0f);
            }
        });
        center.row();
        center.add(buttonTable);
    }



    @Override
    public void render(float v) {
        //light blue background
        ScreenUtils.clear(0, 1, 1, 1);
        //applies viewport
        game.viewport.apply(true);
        game.batch.begin();
        batch.setProjectionMatrix(camera.combined);
        //creates background image
        game.batch.draw(background,0, 0, 16f, 9f);
        game.batch.end();
        // allows stage to work in terms of pixels and not meters
        pixelViewport.apply(true);
        stage.getViewport().apply(true);
        stage.act(v);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
        pixelViewport.update(width, height, true);
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
        batch.dispose();
        background.dispose();
        shapeRenderer.dispose();
        font.dispose();
        stage.dispose();
        whiteTexture.dispose();
        clickSound.dispose();

    }
}
