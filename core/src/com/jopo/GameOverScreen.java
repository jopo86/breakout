package com.jopo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameOverScreen implements Screen {

    private Stage stage;
    private Table uiTable;
    private Dialog titleDialog;
    private Dialog scoreDialog;
    private Dialog highScoreDialog;
    private TextButton startGameButton;
    private TextButton quitGameButton;

    public GameOverScreen(final Breakout game, int score) {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        uiTable = new Table();
        uiTable.setFillParent(true);
        uiTable.align(Align.center | Align.top);
        titleDialog = new Dialog("GAME OVER", game.uiSkin, "game-over");
        scoreDialog = new Dialog("SCORE: " + score, game.uiSkin, "default");
        highScoreDialog = new Dialog((score == game.highScore ? "NEW " : "") + "HIGH SCORE: " + game.highScore, game.uiSkin, "default");

        startGameButton = new TextButton("NEW GAME", game.uiSkin);
        startGameButton.pad(10f);
        startGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
                dispose();
            }
        });

        quitGameButton = new TextButton("QUIT GAME", game.uiSkin);
        quitGameButton.pad(10f);
        quitGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dispose();
                Gdx.app.exit();
            }
        });

        uiTable.padTop(250f).add(titleDialog).padBottom(70f).row();
        uiTable.add(startGameButton).padBottom(30f).row();
        uiTable.add(quitGameButton).padBottom(170f).row();
        uiTable.add(scoreDialog).padBottom(30f).row();
        uiTable.add(highScoreDialog);
        highScoreDialog.setZIndex(0);
        scoreDialog.setZIndex(0);

        stage.addActor(uiTable);

        Gdx.input.setCursorCatched(false);

        game.gameOverSound.play();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, .04f, .1f, 1f);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

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
        stage.dispose();
    }
}
