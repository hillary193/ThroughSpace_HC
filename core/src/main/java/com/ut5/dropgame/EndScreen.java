package com.ut5.dropgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class EndScreen implements Screen {
    private final Main game;
    private SpriteBatch batch;
    private Texture background, menuButtonTexture;
    private BitmapFont font;
    private FitViewport viewport;
    private Rectangle menuButtonRect;

    private boolean isWin;
    private int astronautsSaved;
    private int score;

    public EndScreen(Main game, boolean isWin, int astronautsSaved, int score){
        this.game = game;
        this.isWin = isWin;
        this.astronautsSaved = astronautsSaved;
        this.score = score;

        batch = new SpriteBatch();
        viewport = new FitViewport(16,9);

        // Background based on win/lose
        background = new Texture(isWin ? "winScreen.png" : "loseScreen.png");

        // Menu button
        menuButtonTexture = new Texture("menu_button.png");
        menuButtonRect = new Rectangle((viewport.getWorldWidth()-4)/2,2,4,1.2f);

        // Font
        font = new BitmapFont();
        font.getData().setScale(0.08f);  // readable in world units
        font.setColor(Color.PINK);
    }
    @Override
    public void render(float delta){
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        ScreenUtils.clear(Color.BLACK);

        batch.begin();

        // Draw background
        batch.draw(background, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        font.getData().setScale(0.08f); // adjust as needed
        font.setColor(Color.PINK);

        // Fixed padding for the right side (to avoid overflow)
        float padding = 1.5f; // adjust for desired distance from right edge
        float x = viewport.getWorldWidth() - padding;

        if(isWin){
            // Winning screen: just the score number
            String scoreText = String.valueOf(score);
            GlyphLayout layout = new GlyphLayout(font, scoreText);
            // Positioning the score slightly lower than center
            float y = viewport.getWorldHeight() / 2f + layout.height / 2f; // current position
            y -= 0.1f;  // Lower the score slightly (adjust this value to your liking)
            font.draw(batch, layout, x - layout.width, y); // offset by width of the text
        } else {
            // Losing screen
            String astronautsText = String.valueOf(4 - astronautsSaved);
            String scoreText = String.valueOf(score);

            GlyphLayout layoutAstronauts = new GlyphLayout(font, astronautsText);
            GlyphLayout layoutScore = new GlyphLayout(font, scoreText);

            // Start vertical center
            float centerY = viewport.getWorldHeight() / 2f;

            // Adjust the vertical gap by calculating the height of the text
            float yAstronauts = centerY + layoutAstronauts.height / 2f; // Slightly above the center
            font.draw(batch, layoutAstronauts, x - layoutAstronauts.width, yAstronauts);

            // Draw score below astronauts, use a fixed gap based on text height
            float yScore = yAstronauts - layoutAstronauts.height - 0.1f; // Adjust vertical distance
            font.draw(batch, layoutScore, x - layoutScore.width, yScore); // offset by width of the text
        }

        // Draw menu button
        batch.draw(menuButtonTexture, menuButtonRect.x, menuButtonRect.y, menuButtonRect.width, menuButtonRect.height);

        batch.end();

        handleInput();
    }
    private void handleInput(){
        if(Gdx.input.justTouched()){
            Vector2 touch = new Vector2(Gdx.input.getX(),Gdx.input.getY());
            viewport.unproject(touch);
            if(menuButtonRect.contains(touch.x,touch.y)){
                game.setScreen(new MenuScreen(game));
            }
        }
    }

    @Override public void resize(int width,int height){ viewport.update(width,height,true); }
    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override
    public void dispose(){
        batch.dispose();
        background.dispose();
        menuButtonTexture.dispose();
        font.dispose();
    }
}
