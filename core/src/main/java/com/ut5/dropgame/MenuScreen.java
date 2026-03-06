package com.ut5.dropgame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
public class MenuScreen implements Screen {

    private final Main game;
    private SpriteBatch batch;
    private Texture backgroundTexture, startButtonTexture;
    private Rectangle startButtonRect;
    private FitViewport viewport;


    public MenuScreen(Main game) {
        this.game = game;

        batch = new SpriteBatch();

        // Viewport matches 16:9 aspect ratio
        viewport = new FitViewport(16, 9);

        // Background
        backgroundTexture = new Texture("menuScreen.png");

        // Start button
        startButtonTexture = new Texture("start_button.png");
        startButtonRect = new Rectangle();
        startButtonRect.setSize(4f, 1f); // bigger button
        startButtonRect.setPosition((viewport.getWorldWidth() - startButtonRect.width)/2, 2f);

        // Font for title and instructions

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        // Background
        batch.draw(backgroundTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        // Title



        // Draw Start button
        batch.draw(startButtonTexture, startButtonRect.x, startButtonRect.y, startButtonRect.width, startButtonRect.height);

        batch.end();

        handleInput();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            Vector2 touch = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touch); // convert to world coordinates
            if (startButtonRect.contains(touch.x, touch.y)) {
                game.setScreen(new GameScreen(game));
            }
        }
    }

    @Override public void resize(int width, int height) { viewport.update(width, height, true); }
    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override
    public void dispose() {
        batch.dispose();
        backgroundTexture.dispose();
        startButtonTexture.dispose();

    }
}
