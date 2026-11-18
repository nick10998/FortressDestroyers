package com.nick.fortressdestoryers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;

public class LightningPowerup implements Disposable {
    private final Body body;
    private final Texture texture;
    private final float width = 4.0f;
    private final float height = 8.0f;
    private boolean markedForDestruction = false;
    private final Sound thunder;

    public LightningPowerup(World world, float x, float startY) {
        this.texture = new Texture(Gdx.files.internal("lightning2.png"));
        this.thunder = Gdx.audio.newSound(Gdx.files.internal("lightningstrike.mp3"));

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, startY);
        bodyDef.gravityScale = 0f;
        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2f, height / 2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = Collisions.BOULDER;
        fixtureDef.filter.maskBits = (short) (Collisions.ENVIRONMENT | Collisions.ENEMY | Collisions.FLOOR);

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        shape.dispose();

        //travel straight down
        body.setLinearVelocity(0f, -45f);
        thunder.play(1f);
    }

    public void markForDestruction() {
        markedForDestruction = true;
    }

    public boolean isMarkedForDestruction() {
        return markedForDestruction;
    }

    public Body getBody() {
        return body;
    }

    public void render(SpriteBatch batch) {
        if (markedForDestruction) return;
        Vector2 position = body.getPosition();
        batch.draw(texture,position.x - (width / 2f), position.y - (height / 2f), width, height);
    }

    @Override
    public void dispose() {
        if (texture != null) texture.dispose();
        if (thunder != null) thunder.dispose();
    }
}
