package com.nick.fortressdestoryers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;

public class Catapult implements Disposable {
    private Body body;
    private World world;
    private static final float catapultWidth = 6.0f;
    private static final float catapultHeight = 8.0f;
    private final Vector2 boulderRestPosition;
    //drag/launch variables
    private final Vector2 dragPosition = new Vector2();
    private boolean isTouched = false;
    private static final float maxDrag = 4.0f;
    private static final float forceMultiplier = 40.0f;
    private final Sound tensionSound;
    private final Sound launchSound;
    private final Texture catapultTexture;
    private long tensionSoundID = -1;

    public enum Powerups {NONE, LIGHTNING}
    private Powerups currentPowerup = Powerups.NONE;

    public Catapult(World world, float startPosX, float startPosY){
        this.world = world;
        this.boulderRestPosition = new Vector2(startPosX,startPosY);
        this.dragPosition.set(boulderRestPosition);
        this.tensionSound = Gdx.audio.newSound(Gdx.files.internal("ropetension.mp3"));
        this.launchSound = Gdx.audio.newSound(Gdx.files.internal("catapultlaunch.mp3"));
        this.catapultTexture = new Texture(Gdx.files.internal("catapult.png"));
        createCatapult(world,startPosX,startPosY);
    }

    private void createCatapult(World world,float x, float y){
        BodyDef bodyDef = new BodyDef();
        //this makes catapult immovable
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x,y);
        body = world.createBody(bodyDef);


        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1.5f,2.0f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.0f;
        fixtureDef.filter.categoryBits = Collisions.CATAPULT;
        fixtureDef.filter.maskBits = Collisions.ENVIRONMENT;
        body.createFixture(fixtureDef);
        shape.dispose();
    }

    //picks up boulder
    public void onTouchPress(Vector2 worldTouch) {
        if(worldTouch.dst(boulderRestPosition)<=2.0f) {
            isTouched = true;
            updateDragPosition(worldTouch);
            //plays a rope noise when beginning to pull back
            if (tensionSound != null && tensionSoundID == -1) {
                tensionSoundID = tensionSound.loop(0.8f); // Volume at 50%
            }
        }
    }

    //dragging boulder
    public void onTouchDragging(Vector2 worldTouch) {
        if(isTouched) {
            updateDragPosition(worldTouch);
        }
    }

    //releases boulder from dragging
    public Boulder onTouchRelease(Vector2 worldTouch){
        Boulder launchedBoulder = null;
        if (isTouched) {
            isTouched = false;
            launchedBoulder = lauchBoulder();
            dragPosition.set(boulderRestPosition);
            //stops rope noise
            if (tensionSound != null && tensionSoundID != -1) {
                tensionSound.stop(tensionSoundID);
                tensionSoundID = -1;
            }
            if (launchSound != null) {
                launchSound.play(1.0f);
            }
        }
        return launchedBoulder;
    }

    private void updateDragPosition(Vector2 touch){
        dragPosition.set(touch);
        if(dragPosition.dst(boulderRestPosition)>maxDrag) {
            dragPosition.sub(boulderRestPosition).nor().scl(maxDrag).add(boulderRestPosition);
        }
    }

    private Boulder lauchBoulder(){
        Vector2 force = new Vector2(boulderRestPosition).sub(dragPosition).scl(forceMultiplier);
        Boulder boulder = new Boulder(world, boulderRestPosition.x, boulderRestPosition.y, 0.7f);
        boulder.launch(force);
        return boulder;
    }

    public void trajectoryLine(ShapeRenderer shapeRenderer, OrthographicCamera camera) {
        if(!isTouched) return;
        // Calculate aiming direction (opposite of pullback direction)
        Vector2 launchDirection = new Vector2(boulderRestPosition).sub(dragPosition);
        Vector2 aimPoint = new Vector2(boulderRestPosition).add(launchDirection);

        Gdx.gl.glLineWidth(3f);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 1, 1, 1);
        shapeRenderer.line(boulderRestPosition.x, boulderRestPosition.y, aimPoint.x, aimPoint.y);
        shapeRenderer.end();
        Gdx.gl.glLineWidth(1f);
    }

    public void setPowerUp(Powerups powerup) {
        this.currentPowerup = powerup;
    }

    public Powerups getCurrentPowerUp() {
        return currentPowerup;
    }

    public boolean hasPowerUp() {
        return currentPowerup != Powerups.NONE;
    }

    public void consumePowerUp() {
        this.currentPowerup = Powerups.NONE;
    }

    public Vector2 getLaunchAnchor() {
        return boulderRestPosition;
    }

    public void render(SpriteBatch batch) {
        Vector2 pos = body.getPosition();
        // Draw texture
        batch.draw(catapultTexture,pos.x-(catapultWidth/2f),pos.y-(catapultHeight/2f), catapultWidth,catapultHeight);
    }

    @Override
    public void dispose() {
        if (tensionSound != null) {
            tensionSound.stop();
            tensionSound.dispose();
        }
        if (launchSound != null) launchSound.dispose();
    }
}

