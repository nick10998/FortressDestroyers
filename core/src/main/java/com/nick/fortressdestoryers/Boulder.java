package com.nick.fortressdestoryers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;

public class Boulder implements Disposable {
    private final Body body;
    private final Texture texture;
    private final float radius;

    public Boulder(World world, float startPosX, float startPosY, float radius) {
        this.radius = radius;
        this.texture = new Texture(Gdx.files.internal("boulder.png"));

        //makes boulder movable and sets at startPos x and y
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(startPosX,startPosY);
        body = world.createBody(bodyDef);
        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        //properties:
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        //mass
        fixtureDef.density = 2.0f;
        //friction
        fixtureDef.friction = 2.0f;
        //how much it bounces
        fixtureDef.restitution = 0.3f;
        fixtureDef.filter.categoryBits =Collisions.BOULDER;
        fixtureDef.filter.maskBits = (short) (Collisions.ENVIRONMENT| Collisions.BOULDER | Collisions.ENEMY);
        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    public void launch(Vector2 impulse) {
        body.applyLinearImpulse(impulse, body.getWorldCenter(), true);
    }

    public void render(SpriteBatch batch) {
        Vector2 pos = body.getPosition();
        float diameter = radius * 2f;
        batch.draw(texture,pos.x - radius,pos.y - radius,radius, radius,diameter, diameter,1f, 1f,
            (float) Math.toDegrees(body.getAngle()),0,0,texture.getWidth(),texture.getHeight(),false,false);
    }

    public Body getBody() {
        return body;
    }

    @Override
    public void dispose() {
        if (texture != null) texture.dispose();
    }
}
