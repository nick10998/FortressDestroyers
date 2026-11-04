package com.nick.fortressdestoryers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameScreen extends ScreenAdapter {
    private final Main game;
    private final int levelNumber;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private Stage uiStage;
    private BitmapFont font;
    private Texture texture;
    private Sound clickSound;
    private Label scoreLabel;
    private int score = 0;
    //Libgdx physics works best when screen pixels are converted to meters
    //each meter in my game will be 32 pixels
    public static final float pixelsPerMeter = 32f;
    public static final float myWorldWidth = 1366f/pixelsPerMeter;
    public static final float myWorldHeight = 768f/pixelsPerMeter;
    //floor variables
    private Body floorBody;
    private TextureRegion floorRegion;
    private float floorHeight;
    private Texture floorTexture;
    private float currentWorldWidth;

    private Texture backgroundTexture;


    public GameScreen(Main game, int levelNumber, Sound clickSound){
        this.game = game;
        this.levelNumber = levelNumber;
        this.clickSound = clickSound;
    }

    public void show(){
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(myWorldWidth,myWorldHeight,camera);
        //creates a world with gravity of 10 m/s
        world = new World(new Vector2(0, -10f), true);
        debugRenderer = new Box2DDebugRenderer();
        //background textures
        backgroundTexture = new Texture(Gdx.files.internal("grasslandbackground.jpg"));
        floorTexture = new Texture(Gdx.files.internal("dirtfloor.jpg"));
        floorTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        floorRegion = new TextureRegion(floorTexture);
        floorHeight = 2f;
        createFloor();

        uiStage = new Stage(new FitViewport(1366, 768));
        Gdx.input.setInputProcessor(uiStage);

        font = new BitmapFont();
        font.getData().setScale(2f);
        BitmapFont topLeftFont = new BitmapFont();
        topLeftFont.getData().setScale(3f);
        Label.LabelStyle topLeftLabelStyle = new Label.LabelStyle(topLeftFont,Color.BLACK);
        Pixmap pixmap = new Pixmap(1,1,Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        texture = new Texture(pixmap);
        pixmap.dispose();

        topLeftLabelStyle.background = new TextureRegionDrawable(new TextureRegion(texture)).tint(new Color(1, 0, 1, 0.7f));
        Label levelLabel = new Label("Level " + levelNumber, topLeftLabelStyle);
        levelLabel.setAlignment(Align.center);
        scoreLabel = new Label("Score: " + score, topLeftLabelStyle);
        scoreLabel.setAlignment(Align.center);
        TextButton.TextButtonStyle returnButtonStyle = new TextButton.TextButtonStyle();
        //return to level menu button
        returnButtonStyle.font = font;
        returnButtonStyle.fontColor = Color.BLACK;
        returnButtonStyle.up = new TextureRegionDrawable(new TextureRegion(texture)).tint(new Color(0.8f, 0.2f, 0.2f, 1f));
        returnButtonStyle.down = new TextureRegionDrawable(new TextureRegion(texture)).tint(new Color(0.4f, 0.1f, 0.1f, 1f));
        TextButton returnButton = new TextButton("Exit Level",returnButtonStyle);
        returnButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play(1f);
                game.setScreen(new LevelsMenu(game,game.batch,game.camera,clickSound));
            }
        });

        //format for level and score boxes
        Table leftInfoTable = new Table();
        leftInfoTable.add(levelLabel).left().padRight(50f);
        leftInfoTable.add(scoreLabel).left();
        Table topTable = new Table();
        topTable.setFillParent(true);
        topTable.top();
        topTable.add(leftInfoTable).left().pad(50f).expandX();
        topTable.add(returnButton).right().pad(20f).width(180f).height(45f);

        uiStage.addActor(topTable);
    }

    private void createFloor() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        bodyDef.position.set(myWorldWidth/2f,1f);
        //floor created in the world
        floorBody = world.createBody(bodyDef);
        //collision for the floor
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(200f,1f);
        floorBody.createFixture(shape,0.0f);
        shape.dispose();
    }

    public void render(float v){
        //completely clear screen
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        //60 world iterations per second
        world.step(1 / 60f, 6, 2);
        game.batch.begin();
        //draw background
        float backgroundX = camera.position.x-(viewport.getWorldWidth()/2f);
        float backgroundY = camera.position.y- (viewport.getWorldHeight()/2f);
        game.batch.draw(backgroundTexture,backgroundX,backgroundY,viewport.getWorldWidth(),viewport.getWorldHeight());
        //tiling for the floor
        currentWorldWidth = viewport.getWorldWidth();
        int tileCount = Math.max(1, (int) currentWorldWidth);
        floorRegion.setRegion(0,0,floorTexture.getWidth()*tileCount,floorTexture.getHeight());
        //draw floor
        float floorX = camera.position.x-(currentWorldWidth/2f);
        float floorY = floorBody.getPosition().y-(floorHeight/2f);
        game.batch.draw(floorRegion, floorX,floorY,currentWorldWidth,floorHeight);
        game.batch.end();
        //shows collision hitboxes
        debugRenderer.render(world, camera.combined);
        uiStage.act(v);
        uiStage.draw();
    }

    public void resize(int width, int height) {
        int screenW = Gdx.graphics.getWidth();
        int screenH = Gdx.graphics.getHeight();
        viewport.update(screenW,screenH, true);
        uiStage.getViewport().update(screenW,screenH, true);
    }

    public void dispose() {
        backgroundTexture.dispose();
        floorTexture.dispose();
        world.dispose();
        debugRenderer.dispose();
        uiStage.dispose();
    }
}
