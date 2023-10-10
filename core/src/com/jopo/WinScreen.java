package com.jopo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class WinScreen implements Screen {

    private Stage stage;
    private Table uiTable;
    private Dialog titleDialog;
    private TextButton startGameButton;
    private TextButton quitGameButton;

    public WinScreen(final Breakout game) {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        uiTable = new Table();
        uiTable.setFillParent(true);
        uiTable.align(Align.center | Align.top);
        titleDialog = new Dialog("YOU WIN!", game.uiSkin, "victory");

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
        uiTable.add(quitGameButton);

        stage.addActor(uiTable);

        game.victorySound.play();
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
