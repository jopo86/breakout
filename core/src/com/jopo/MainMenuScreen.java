package com.jopo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MainMenuScreen implements Screen {

    private Stage stage;
    private Table uiTable;
    private Dialog titleDialog;
    private Dialog highScoreDialog;
    private TextButton startGameButton;
    private TextButton quitGameButton;

    public MainMenuScreen(final Breakout game) throws IOException {
        stage = new Stage(new ScreenViewport());
        uiTable = new Table();
        uiTable.setFillParent(true);
        uiTable.align(Align.center | Align.top);
        titleDialog = new Dialog("BREAKOUT", game.uiSkin, "large");
        highScoreDialog = new Dialog("HIGH SCORE: " + game.highScore, game.uiSkin, "default");

        startGameButton = new TextButton("START GAME", game.uiSkin);
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

        uiTable.padTop(200f).add(titleDialog).padBottom(70f).row();
        uiTable.add(startGameButton).padBottom(30f).row();
        uiTable.add(quitGameButton).padBottom(200f).row();
        uiTable.add(highScoreDialog);
        highScoreDialog.setZIndex(0);
        stage.addActor(uiTable);
        Gdx.input.setInputProcessor(stage);
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
