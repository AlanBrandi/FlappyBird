package com.mygdx.flappybird;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SplashScreen implements Screen, ApplicationListener {
    private GL20 gl;

    private Game game;
    private SpriteBatch spriteBatch;
    private Texture splashImage;
    private float timeElapsed = 0f;
    private static final float SPLASH_SCREEN_TIME = 3f; // tempo em segundos

    public SplashScreen(Game game) {
        this.game = game;
    }

    public SplashScreen() {

    }

    @Override
    public void show() {
        gl = Gdx.graphics.getGL20();
        spriteBatch = new SpriteBatch();
        splashImage = new Texture("Splash_Screen.png");
    }

    @Override
    public void render(float delta) {
        gl.glClearColor(0, 0, 0, 1);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();
        spriteBatch.draw(splashImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();

        timeElapsed += Gdx.graphics.getDeltaTime();
        if (timeElapsed > SPLASH_SCREEN_TIME) {
            dispose();
            game.setScreen(new FlappyBird().getScreen());
        }

    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {

    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        splashImage.dispose();
    }

    @Override
    public void create() {
        // deixe vazio
    }
}