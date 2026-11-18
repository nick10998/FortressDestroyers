package com.nick.fortressdestoryers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;

public class Structure implements Disposable {
    private final Body body;
    private final Texture texture;
    private final float width;
    private final float height;
    private int health = 2;
    private boolean markedForDestruction = false;
    private final Sound damageSound;
    private final Sound destroyedSound;

    public Structure(World world, float startPosX, float startPosY, float width, float height) {
        this.width = width;
        this.height = height;
        this.texture = new Texture(Gdx.files.internal("woodTexture.jpeg"));
        this.damageSound = Gdx.audio.newSound(Gdx.files.internal("wooddamagelight.mp3"));
        this.destroyedSound = Gdx.audio.newSound(Gdx.files.internal("woodbreakdestroyed.mp3"));

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(startPosX, startPosY);
        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2f, height/2f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 8.0f;
        fixtureDef.friction = 0.8f;
        fixtureDef.restitution = 0.0f;
        fixtureDef.filter.categoryBits = Collisions.ENVIRONMENT;
        fixtureDef.filter.maskBits = (short)(Collisions.ENVIRONMENT | Collisions.ENEMY | Collisions.BOULDER | Collisions.FLOOR);

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        shape.dispose();
    }

    public void render(SpriteBatch batch) {
        Color previousColor = batch.getColor().cpy();
        if (health == 1) {
            batch.setColor(0.3f, 0.3f, 0.3f, 1f);
        }
        Vector2 position = body.getPosition();
        batch.draw(texture,position.x-(width/2f),position.y-(height/2f),width/2f,height/2f, width,height,
            1f, 1f,(float)Math.toDegrees(body.getAngle()),0,0,texture.getWidth(),texture.getHeight(),false,false);
        batch.setColor(previousColor);
    }

    public void takeDamage() {
        health--;
        if (health <= 0) {
            markedForDestruction = true;
            destroyedSound.play(1f);
        }
        else damageSound.play(1f);
    }

    public void markForDestruction() {
        if (!markedForDestruction) {
            this.health = 0;
            this.markedForDestruction = true;
            destroyedSound.play();
        }
    }

    public Body getBody() {
        return body;
    }

    public int getHealth() {
        return health;
    }

    public boolean isMarkedForDestruction() {
        return markedForDestruction;
    }

    @Override
    public void dispose(){
        texture.dispose();
    }
}
