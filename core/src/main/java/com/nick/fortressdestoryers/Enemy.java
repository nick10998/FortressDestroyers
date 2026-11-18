
package com.nick.fortressdestoryers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;

public class Enemy implements Disposable {
    private final Body body;
    private final Texture texture;
    private final float width;
    private final float height;
    private boolean markedForDestruction = false;

    public Enemy(World world, float startPosX, float startPosY, float width, float height) {
        this.width = width;
        this.height = height;
        this.texture = new Texture(Gdx.files.internal("evilknight.png"));

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(startPosX,startPosY);
        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2f,height/2f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef. density = 2.0f;
        fixtureDef.friction = 0.5f;
        fixtureDef.filter.categoryBits = Collisions.ENEMY;
        fixtureDef.filter.maskBits = (short)(Collisions.ENVIRONMENT | Collisions.BOULDER | Collisions.ENEMY | Collisions.FLOOR);
        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        shape.dispose();
    }

    public void render(SpriteBatch batch) {
        if (markedForDestruction) return;
        Vector2 position = body.getPosition();
        batch.draw(texture, position.x-(width/2f), position.y-(height/2f), width/2f,height/2f,width,height,
            1.6f,1.6f,(float)Math.toDegrees(body.getAngle()),0,0,texture.getWidth(),texture.getHeight(),false,false);
    }

    public void markForDestruction(){
        this.markedForDestruction = true;
    }

    public boolean isMarkedForDestruction() {
        return markedForDestruction;
    }

    public Body getBody() {
        return body;
    }


    @Override
    public void dispose() {
        if (texture != null) texture.dispose();
    }
}

