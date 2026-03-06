package com.ut5.dropgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
public class GameScreen implements Screen {
    private final Main game;

    private SpriteBatch batch;
    private Texture spaceBackground, spaceshipTexture, debrisTexture, coinTexture;
    private Texture astronautLostTexture, astronautIconTexture, heartTexture;
    private Texture asteroidTexture, ufoTexture, satelliteTexture;

    private FitViewport viewport;

    private Sprite spaceship;
    private Vector2 touchPos;
    private Rectangle spaceshipRect;

    private Array<FallingObject> fallingObjects;
    private Array<CoinObject> coins;
    private Array<FallingObject> astronauts;

    private int lives = 3;
    private int astronautsSaved = 0;
    private int score = 0;
    private boolean astronautOnScreen = false;

    private float spawnTimer = 0f;
    private float gameTime = 0f; // CHANGE 2: used for progressive difficulty

    private BitmapFont font;

    private Music gameSong;
    private Sound lootSound;
    private Sound collisionSound;

    public GameScreen(Main game) {
        this.game = game;
        batch = new SpriteBatch();

        viewport = new FitViewport(16, 9);

        spaceBackground = new Texture("space_background.png");
        spaceshipTexture = new Texture("spaceship.png");
        debrisTexture = new Texture("debris.png");
        coinTexture = new Texture("coin.png");
        astronautLostTexture = new Texture("astronaut_lost.png");
        astronautIconTexture = new Texture("astronaut.png");
        heartTexture = new Texture("heart.png");
        asteroidTexture = new Texture("asteroid.png");
        ufoTexture = new Texture("ufo.png");
        satelliteTexture = new Texture("satellite.png");

        gameSong = Gdx.audio.newMusic(Gdx.files.internal("gameSong.mp3"));
        gameSong.setLooping(true);
        gameSong.setVolume(1f);
        gameSong.play();

        collisionSound = Gdx.audio.newSound(Gdx.files.internal("collision.mp3"));
        lootSound = Gdx.audio.newSound(Gdx.files.internal("loot.mp3"));

        spaceship = new Sprite(spaceshipTexture);
        spaceship.setSize(1.5f, 1.5f);
        spaceship.setPosition(viewport.getWorldWidth()/2 - 0.75f, 1f);

        spaceshipRect = new Rectangle(
            spaceship.getX(),
            spaceship.getY(),
            spaceship.getWidth(),
            spaceship.getHeight()
        );

        touchPos = new Vector2();

        fallingObjects = new Array<>();
        coins = new Array<>();
        astronauts = new Array<>();

        font = new BitmapFont();
        font.getData().setScale(0.08f);
        font.setColor(Color.PINK);
    }

    @Override
    public void render(float delta) {
        handleInput();
        updateLogic(delta);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        ScreenUtils.clear(Color.BLACK);

        batch.begin();

        batch.draw(spaceBackground, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        spaceship.draw(batch);

        for (FallingObject obj : fallingObjects) obj.sprite.draw(batch);
        for (FallingObject a : astronauts) a.sprite.draw(batch);
        for (CoinObject c : coins) c.sprite.draw(batch);

        float iconSize = 0.7f;
        float margin = 0.3f;
        float iconY = viewport.getWorldHeight() - iconSize - 0.2f;

        for (int i = 0; i < lives; i++) {
            batch.draw(heartTexture, margin + i*(iconSize+0.1f), iconY, iconSize, iconSize);
        }

        for (int i = 0; i < astronautsSaved; i++) {
            batch.draw(astronautIconTexture,
                viewport.getWorldWidth() - iconSize - margin,
                iconY - i*(iconSize + 0.1f),
                iconSize,
                iconSize);
        }

        font.draw(batch, "S: " + score,
            viewport.getWorldWidth() - 5f,
            viewport.getWorldHeight() - 0.3f);

        batch.end();
    }

    private void handleInput() {
        float speed = 6f;
        float delta = Gdx.graphics.getDeltaTime();

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) spaceship.translateX(speed*delta);
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) spaceship.translateX(-speed*delta);
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) spaceship.translateY(speed*delta);
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) spaceship.translateY(-speed*delta);

        if(Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);
            spaceship.setCenter(touchPos.x, touchPos.y);
        }

        spaceship.setX(MathUtils.clamp(spaceship.getX(), 0, viewport.getWorldWidth()-spaceship.getWidth()));
        spaceship.setY(MathUtils.clamp(spaceship.getY(), 0, viewport.getWorldHeight()-spaceship.getHeight()));

        spaceshipRect.set(
            spaceship.getX(),
            spaceship.getY(),
            spaceship.getWidth(),
            spaceship.getHeight()
        );
    }

    private void updateLogic(float delta) {

        spawnTimer += delta;
        gameTime += delta; // CHANGE 2
        if(spawnTimer > 1f){
            if(MathUtils.random() < 0.012f) createDebris();
            if(MathUtils.random() < 0.009f) createAsteroid();
            if(MathUtils.random() < 0.005f) createUfo();
            if(MathUtils.random() < 0.006f) createSatellite();

            if(MathUtils.random() < 0.0035f) createAstronaut(); // slightly less frequent
            if(MathUtils.random() < 0.008f) createCoin();
        }

        float speed = 3f + gameTime * 0.1f; // CHANGE 2 difficulty scaling
        float padding = 0.2f; // CHANGE 1 smaller hitboxes

        for(int i = fallingObjects.size - 1; i >= 0; i--){
            FallingObject obj = fallingObjects.get(i);

            obj.sprite.translateY(-speed * delta);

            Rectangle objRect = new Rectangle(
                obj.sprite.getX() + padding,
                obj.sprite.getY() + padding,
                obj.sprite.getWidth() - padding*2,
                obj.sprite.getHeight() - padding*2
            );

            Rectangle shipRect = new Rectangle(
                spaceship.getX() + padding,
                spaceship.getY() + padding,
                spaceship.getWidth() - padding*2,
                spaceship.getHeight() - padding*2
            );

            if(shipRect.overlaps(objRect)){
                collisionSound.play();
                lives--;
                fallingObjects.removeIndex(i);
            }
            else if(obj.sprite.getY() < -obj.sprite.getHeight()){
                fallingObjects.removeIndex(i);
            }
        }

        for(int i = astronauts.size - 1; i >= 0; i--){
            FallingObject a = astronauts.get(i);
            a.sprite.translateY(-2.5f * delta);

            Rectangle aRect = new Rectangle(
                a.sprite.getX() + padding,
                a.sprite.getY() + padding,
                a.sprite.getWidth() - padding*2,
                a.sprite.getHeight() - padding*2
            );

            Rectangle shipRect = new Rectangle(
                spaceship.getX() + padding,
                spaceship.getY() + padding,
                spaceship.getWidth() - padding*2,
                spaceship.getHeight() - padding*2
            );

            if(shipRect.overlaps(aRect)){
                astronautsSaved = Math.min(astronautsSaved + 1, 4);
                score += 5;

                astronauts.removeIndex(i);
                astronautOnScreen = false;

                lootSound.play(0.3f); // CHANGE 4 clean sound
            }
            else if(a.sprite.getY() < -a.sprite.getHeight()){
                astronauts.removeIndex(i);
                astronautOnScreen = false;
            }
        }

        for(int i = coins.size - 1; i >= 0; i--){
            CoinObject c = coins.get(i);

            c.sprite.translateY(-2.5f * delta);

            Rectangle coinRect = new Rectangle(
                c.sprite.getX() + padding,
                c.sprite.getY() + padding,
                c.sprite.getWidth() - padding*2,
                c.sprite.getHeight() - padding*2
            );

            Rectangle shipRect = new Rectangle(
                spaceship.getX() + padding,
                spaceship.getY() + padding,
                spaceship.getWidth() - padding*2,
                spaceship.getHeight() - padding*2
            );

            if(shipRect.overlaps(coinRect)){
                score += 1;
                coins.removeIndex(i);

                lootSound.play(0.3f); // CHANGE 7 fixed double sound
            }
            else if(c.sprite.getY() < -c.sprite.getHeight()){
                coins.removeIndex(i);
            }
        }

        if(lives <= 0){
            game.setScreen(new EndScreen(game, false, astronautsSaved, score));
        }
        else if(astronautsSaved >= 4){
            game.setScreen(new EndScreen(game, true, astronautsSaved, score));
        }
    }

    // CHANGE 3: spawn protection from spawning above spaceship
    private float getSafeSpawnX(float width){
        float x;
        do{
            x = MathUtils.random(0f, viewport.getWorldWidth()-width);
        }while(Math.abs(x - spaceship.getX()) < 2f);
        return x;
    }

    private void createDebris(){ spawnObject(debrisTexture, "debris"); }
    private void createAsteroid(){ spawnObject(asteroidTexture, "asteroid"); }
    private void createUfo(){ spawnObject(ufoTexture, "ufo"); }
    private void createSatellite(){ spawnObject(satelliteTexture, "satellite"); }

    private void createAstronaut(){
        if(!astronautOnScreen){
            Sprite s = new Sprite(astronautLostTexture);
            s.setSize(1f,1f);
            s.setX(getSafeSpawnX(s.getWidth()));
            s.setY(viewport.getWorldHeight() + 1f);

            astronauts.add(new FallingObject(s, "astronaut"));
            astronautOnScreen = true;
        }
    }

    private void createCoin(){
        Sprite s = new Sprite(coinTexture);
        s.setSize(1f,1f);
        s.setX(getSafeSpawnX(s.getWidth()));
        s.setY(viewport.getWorldHeight() + 1f);

        coins.add(new CoinObject(s));
    }

    private void spawnObject(Texture tex, String type){
        Sprite s = new Sprite(tex);
        s.setSize(1.5f,1.5f);
        s.setX(getSafeSpawnX(s.getWidth()));
        s.setY(viewport.getWorldHeight() + 1f);

        fallingObjects.add(new FallingObject(s, type));
    }

    private class FallingObject{
        Sprite sprite;
        String type;
        public FallingObject(Sprite s, String type){ sprite=s; this.type = type; }
    }

    private class CoinObject{
        Sprite sprite;
        public CoinObject(Sprite s){ sprite=s; }
    }

    @Override public void resize(int width, int height){ viewport.update(width,height,true); }
    @Override public void show(){}
    @Override public void hide(){}
    @Override public void pause(){}
    @Override public void resume(){}

    @Override
    public void dispose(){
        batch.dispose();
        spaceBackground.dispose();
        spaceshipTexture.dispose();
        debrisTexture.dispose();
        coinTexture.dispose();
        astronautLostTexture.dispose();
        astronautIconTexture.dispose();
        heartTexture.dispose();
        asteroidTexture.dispose();
        ufoTexture.dispose();
        satelliteTexture.dispose();
        gameSong.dispose();
        collisionSound.dispose();
        font.dispose();
    }
}
