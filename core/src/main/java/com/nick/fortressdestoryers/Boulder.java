package com.nick.fortressdestoryers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class Boulder implements Disposable {
    private final Body body;
    private final Texture texture;
    private final float radius;

    //powerup relevant
    private boolean isLightning = false;
    private boolean hasStruck = false;

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
        fixtureDef.filter.maskBits = (short) (Collisions.ENVIRONMENT| Collisions.BOULDER | Collisions.ENEMY | Collisions.FLOOR);
        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    public void launch(Vector2 impulse) {
        body.applyLinearImpulse(impulse, body.getWorldCenter(), true);
    }

    public void setLightning(boolean isLightning) {
        this.isLightning = isLightning;
    }

    public boolean shouldTriggerLightning(){
        if (isLightning && !hasStruck) {
            hasStruck = true;
            return true;
        }
        return false;
    }

    private static class TrailParticle {
        float x, y, vx, vy, size, life, maxLife;
    }
    private final Array<TrailParticle> particles = new Array<>();
    private Texture particleTexture;
    private Color trailColor = null;
    private float spawnTimer = 0f;

    public void setTrailColor(Color color) {
        this.trailColor = color;
        if (this.particleTexture == null) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            this.particleTexture = new Texture(pixmap);
            pixmap.dispose();
        }
    }

    public void render(SpriteBatch batch) {
        Vector2 position = body.getPosition();
        float diameter = radius * 2f;
        batch.draw(texture,position.x - radius,position.y - radius,radius, radius,diameter, diameter,1f, 1f,
            (float) Math.toDegrees(body.getAngle()),0,0,texture.getWidth(),texture.getHeight(),false,false);

        //particle trail when powered up
        if (trailColor != null) {
            float delta = Gdx.graphics.getDeltaTime();
            spawnTimer += delta;

            if (spawnTimer >= 0.02f && body.getLinearVelocity().len() > 1.0f) {
                spawnTimer = 0f;
                Vector2 pos = body.getPosition();
                for (int i = 0; i < 2; i++) {
                    TrailParticle p = new TrailParticle();
                    p.x = pos.x + MathUtils.random(-0.3f, 0.3f);
                    p.y = pos.y + MathUtils.random(-0.3f, 0.3f);
                    p.vx = MathUtils.random(-1.5f, 1.5f);
                    p.vy = MathUtils.random(-1.5f, 1.5f);
                    p.size = MathUtils.random(0.2f, 0.4f);
                    p.maxLife = MathUtils.random(0.25f, 0.45f);
                    p.life = p.maxLife;
                    particles.add(p);
                }
            }
            Color previousColor = batch.getColor().cpy();

            for (int i = particles.size - 1; i >= 0; i--) {
                TrailParticle p = particles.get(i);
                p.life -= delta;
                if (p.life <= 0f) {
                    particles.removeIndex(i);
                    continue;
                }
                p.x += p.vx * delta;
                p.y += p.vy * delta;
                float alpha = p.life / p.maxLife;
                float currentSize = p.size * alpha;
                batch.setColor(trailColor.r, trailColor.g, trailColor.b, alpha);
                batch.draw(
                    particleTexture,
                    p.x - (currentSize / 2f),
                    p.y - (currentSize / 2f),
                    currentSize,
                    currentSize
                );
            }
            batch.setColor(previousColor);
        }
    }

    public Body getBody() {
        return body;
    }

    @Override
    public void dispose() {
        if (texture != null) texture.dispose();
        if (particleTexture != null) particleTexture.dispose();
    }
}
