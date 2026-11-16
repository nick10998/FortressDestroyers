package com.nick.fortressdestoryers;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
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
    private Sound enemyDefeatSound;
    private Label scoreLabel;
    private int score = 0;
    private Image boulderIcon;
    private int bouldersLeft = 7;
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
    private Catapult catapult;
    ShapeRenderer shapeRenderer;
    //boulder objects
    private final Array<Boulder> activeBoulders = new Array<>();
    //enemy objects
    private final Array<Enemy> activeEnemies = new Array<>();
    //structure objects
    private final Array<Structure> activeStructures = new Array<>();


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

        //necessary for the catapult
        shapeRenderer = new ShapeRenderer();
        catapult = new Catapult(world,6f,4f);

        enemyDefeatSound = Gdx.audio.newSound(Gdx.files.internal("enemyDefeatSound(trim).mp3"));

        //for (int i = 1; i<5; i++) {
            activeEnemies.add(new Enemy(world, (35f /*+ (5*i)*/), 2f, 2f, 4f));
       // }

        //structures
        //house formation
        //activeStructures.add(new Structure(world, 30f,2.4f, 1f, 6f));
        //activeStructures.add(new Structure(world, 40f,2.4f, 1f, 6f));
        //activeStructures.add(new Structure(world, 35f,9f, 15f, 1f));
        //big wall
        activeStructures.add(new Structure(world,30f,2.4f,1f,10f));

        world.setContactListener(new ContactListener() {
            //this detects collisions
            @Override
            public void beginContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                Object dataA = fixtureA.getUserData();
                Object dataB = fixtureB.getUserData();
                // type 1: boulder hits enemy
                if (dataA instanceof Boulder && dataB instanceof Enemy) {
                    ((Enemy) dataB).markForDestruction();
                } else if (dataB instanceof Boulder && dataA instanceof Enemy) {
                    ((Enemy) dataA).markForDestruction();
                }
                //type 2: boulder hits structure
                if (dataA instanceof Boulder && dataB instanceof Structure) {
                    ((Structure)dataB).takeDamage();
                } else if (dataB instanceof Boulder && dataA instanceof Structure) {
                    ((Structure)dataA).takeDamage();
                }
                //type 3: fast moving structure hits enemy
                float dangerSpeed = 3.0f;
                if (dataA instanceof Structure && dataB instanceof Enemy) {
                    float structureSpeed = ((Structure)dataA).getBody().getLinearVelocity().len();
                    if (structureSpeed >= dangerSpeed) {
                        ((Enemy)dataB).markForDestruction();
                    }
                }
                else if (dataB instanceof Structure && dataA instanceof Enemy) {
                    float structureSpeed = ((Structure)dataB).getBody().getLinearVelocity().len();
                    if (structureSpeed >= dangerSpeed) {
                        ((Enemy)dataA).markForDestruction();
                    }
                }
            }
            @Override
            public void endContact(Contact contact) {
            }
            @Override
            public void preSolve(Contact contact, Manifold manifold) {
            }
            @Override
            public void postSolve(Contact contact, ContactImpulse contactImpulse) {
            }
        });

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

        //labels and tables for UI elements
        topLeftLabelStyle.background = new TextureRegionDrawable(new TextureRegion(texture)).tint(new Color(1, 0, 1, 0.7f));
        Label levelLabel = new Label("Level " + levelNumber, topLeftLabelStyle);
        levelLabel.setAlignment(Align.center);
        scoreLabel = new Label("Score: " + score, topLeftLabelStyle);
        scoreLabel.setAlignment(Align.center);
        Texture boulderIconTexture = new Texture(Gdx.files.internal("boulder.png"));
        boulderIcon = new Image(boulderIconTexture);
        Label boulderLabel = new Label("x"+bouldersLeft, topLeftLabelStyle);
        Table boulderTable = new Table();
        boulderTable.add(boulderIcon).size(36f, 36f).padRight(5f);
        boulderTable.add(boulderLabel);

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
        topTable.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.childrenOnly);
        topTable.add(leftInfoTable).left().pad(50f).expandX();
        topTable.add(returnButton).right().pad(20f).width(180f).height(45f);
        topTable.row();
        topTable.add(boulderTable).left().pad(50f);

        // multiplexer allows both my back button and drag+hold controls to be processed even with uiStage
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiStage);
        multiplexer.addProcessor(new InputAdapter() {
            private final Vector2 worldTouch = new Vector2();
            private void updateTouchingCoordinates(float screenX,float screenY){
                viewport.unproject(worldTouch.set(screenX,screenY));
            }
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (bouldersLeft <= 0) return false;
                updateTouchingCoordinates(screenX, screenY);
                catapult.onTouchPress(worldTouch);
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                updateTouchingCoordinates(screenX, screenY);
                catapult.onTouchDragging(worldTouch);
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (bouldersLeft <= 0) return false;
                updateTouchingCoordinates(screenX, screenY);
                Boulder newBoulder = catapult.onTouchRelease(worldTouch);
                if (newBoulder != null) {
                    activeBoulders.add(newBoulder);
                    bouldersLeft--;
                    boulderLabel.setText("x" + bouldersLeft);
                }
                return true;
            }
        });

        Gdx.input.setInputProcessor(multiplexer);
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

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.0f;
        fixtureDef.filter.categoryBits = Collisions.ENVIRONMENT;
        fixtureDef.filter.maskBits = (short)(Collisions.CATAPULT | Collisions.BOULDER | Collisions.ENEMY | Collisions.ENVIRONMENT);
        floorBody.createFixture(fixtureDef);
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
        for (int i = activeEnemies.size - 1; i >= 0; i--) {
            Enemy enemy = activeEnemies.get(i);
            if (enemy.isMarkedForDestruction()) {
                world.destroyBody(enemy.getBody());
                enemy.dispose();
                activeEnemies.removeIndex(i);
                enemyDefeatSound.play(1f);
                score += 100;
                scoreLabel.setText("Score: " + score);
            }
        }
        for (int i = activeStructures.size - 1; i >= 0; i--) {
            Structure structure = activeStructures.get(i);
            if (structure.isMarkedForDestruction()) {
                world.destroyBody(structure.getBody());
                structure.dispose();
                activeStructures.removeIndex(i);
            }
        }

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
        catapult.render(game.batch);
        //boulders
        for (Boulder boulder : activeBoulders) {
            boulder.render(game.batch);
        }
        for (Enemy enemy : activeEnemies) {
            enemy.render(game.batch);
        }
        for (Structure structure : activeStructures) {
            structure.render(game.batch);
        }
        game.batch.end();
        //shows aim line
        catapult.trajectoryLine(shapeRenderer, camera);
        //shows collision hitboxes
        //debugRenderer.render(world, camera.combined);
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
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (enemyDefeatSound != null) enemyDefeatSound.dispose();
        if (catapult != null) catapult.dispose();
        for (Boulder boulder : activeBoulders) {
            boulder.dispose();
        }
        for (Structure structure: activeStructures) {
            structure.dispose();
        }

    }
}
