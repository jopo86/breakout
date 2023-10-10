package com.jopo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.awt.Dimension;
import java.util.ArrayList;

public class GameScreen implements Screen, InputProcessor {

    private final Breakout game;

    private final Dimension BRICK_DIMENSIONS = new Dimension(70, 30);
    private final Dimension BALL_DIMENSIONS = new Dimension(20, 20);
    private final Dimension PADDLE_DIMENSIONS = new Dimension(115, 22);
    private final int BRICK_GAP = 5;
    private final Color[] COLORS = { Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.PURPLE, Color.MAGENTA };

    private Stage stage;
    private Image ball;
    private ArrayList<ArrayList<Image>> bricks;
    private Image paddle;

    private int score = 0;
    private int vx = 0;
    private int vy = -300;
    private float v = 200;
    private float pv = 0;
    private float lastPaddleX = 0;

    private boolean tempBool = false;
    private String tempString = "";
    private Actor tempActor = new Actor();

    public GameScreen(final Breakout game) {
        this.game = game;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(this);

        ball = new Image(game.ballTexture);
        ball.setSize(BALL_DIMENSIONS.width, BALL_DIMENSIONS.height);
        ball.setPosition(Gdx.graphics.getWidth() / 2f - ball.getWidth() / 2f, 150f);
        ball.setName("ball");

        paddle = new Image(game.paddleTexture);
        paddle.setSize(PADDLE_DIMENSIONS.width, PADDLE_DIMENSIONS.height);
        paddle.setPosition(Gdx.graphics.getWidth() / 2f - paddle.getWidth() / 2f, 50f);
        paddle.setName("paddle");

        stage.addActor(ball);
        stage.addActor(paddle);
        populateBricks();

        Gdx.input.setCursorCatched(true);
    }

    private void update(float delta) {
        // win test
        tempBool = true;
        for (Actor actor : stage.getActors()) if (actor.getName().equals("brick")) tempBool = false;
        if (tempBool) {
            try { Thread.sleep(1000); } catch(InterruptedException e){ throw new RuntimeException(e.getMessage()); }
            game.setScreen(new WinScreen(game));
            Gdx.input.setCursorCatched(false);
        }

        pv = (getPaddleCenterX() - lastPaddleX) / delta;

        v = (float)Math.sqrt(vx*vx + vy*vy);
        ball.moveBy(vx * delta, vy * delta);

        // keep paddle in bounds
        if (paddle.getX() < 0) paddle.setX(0);
        if (paddle.getX() + paddle.getWidth() > 1280) paddle.setX(1280 - paddle.getWidth());

        // actor collision tests
        // top
        tempActor = stage.hit(getBallCenterX(), getBallTop(), false);
        if (vy > 0) {
            if (tempActor != null) {
                tempString = tempActor.getName();
                if (!tempString.equals("ball")) {
                    vy = -Math.abs(vy);
                    if (tempActor.getName().equals("brick")) {
                        score++;
                        tempActor.remove();
                        scaleVelocity(1.015f);
                    }
                    tempActor = null;
                    game.bounceSound.play();
                }
            }
        }

        // bottom (identical to top, but cant really combine)
        if (vy < 0) {
            tempActor = stage.hit(getBallCenterX(), getBallBottom(), false);
            if (tempActor != null) {
                tempString = tempActor.getName();
                if (!tempString.equals("ball")) {
                    vy = Math.abs(vy);
                    if (tempActor.getName().equals("brick")) {
                        score++;
                        tempActor.remove();
                        scaleVelocity(1.015f);
                    }
                    else if (tempActor.getName().equals("paddle")) { // decide angle based on offset from middle of paddle
                        int offset = (int)(getBallCenterX() - getPaddleCenterX());
                        vx += (offset * 3) + (int)pv;
                        if (Math.abs(vx) >= v - 30f) vx = (vx > 0 ? (int)v - 30 : (int)-v + 30);
                        vy = (int)Math.sqrt(v*v - vx*vx);

                    }
                    tempActor = null;
                    game.bounceSound.play();
                }
            }
        }

        // left
        if (vx < 0) {
            tempActor = stage.hit(getBallLeft(), getBallCenterY(), false);
            if (tempActor != null) {
                tempString = tempActor.getName();
                if (!tempString.equals("ball")) {
                    vx = Math.abs(vx);
                    if (tempActor.getName().equals("brick")) {
                        score++;
                        tempActor.remove();
                        scaleVelocity(1.015f);
                    }
                    tempActor = null;
                    game.bounceSound.play();
                }
            }
        }

        // right (identical to left, but can't really combine)
        tempActor = stage.hit(getBallRight(), getBallCenterY(), false);
        if (vx > 0) {
            if (tempActor != null) {
                tempString = tempActor.getName();
                if (!tempString.equals("ball")) {
                    vx = -Math.abs(vx);
                    if (tempActor.getName().equals("brick")) {
                        score++;
                        tempActor.remove();
                        scaleVelocity(1.015f);
                    }
                    tempActor = null;
                    game.bounceSound.play();
                }
            }
        }

        // wall collision tests
        if (ball.getY() + ball.getHeight() >= 720 && vy > 0) { // ceiling collision
            vy = -Math.abs(vy);
            game.bounceSound.play();
        }
        if (ball.getY() + ball.getHeight() <= 0 && vy < 0) { // floor collision = death
            try { Thread.sleep(1000); } catch(InterruptedException e){ throw new RuntimeException(e.getMessage()); }
            game.setScreen(new GameOverScreen(game));
            Gdx.input.setCursorCatched(false);
            game.bounceSound.play();
        }
        if ((ball.getX() <= 0 && vx < 0)) { // left wall collision
            vx = Math.abs(vx);
            game.bounceSound.play();
        }
        if ((ball.getX() + ball.getWidth() >= 1280 && vx > 0)) { // right wall collision
            vx = -Math.abs(vx);
            game.bounceSound.play();
        }

        lastPaddleX = getPaddleCenterX();

//        System.out.println("VX: " + vx + " | VY: " + vy + " | V: " + v + " | PV: " + pv);

    }

    private void populateBricks() {
        bricks = new ArrayList<>();
        for (int row = 0; row < (Gdx.graphics.getHeight() / 2f - 100) / (BRICK_DIMENSIONS.height + BRICK_GAP); row++) {
            bricks.add(new ArrayList<>());
            for (int col = 0; col < (Gdx.graphics.getWidth() - 200f) / (BRICK_DIMENSIONS.width + BRICK_GAP); col++) {
                bricks.get(row).add(new Image(game.brickTexture));
                bricks.get(row).get(col).setColor(getRowColor(row));
                bricks.get(row).get(col).setSize(BRICK_DIMENSIONS.width, BRICK_DIMENSIONS.height);
                bricks.get(row).get(col).setPosition(100f + col * (BRICK_DIMENSIONS.width + BRICK_GAP), Gdx.graphics.getHeight() - (100 + row * (BRICK_DIMENSIONS.height + BRICK_GAP)));
                bricks.get(row).get(col).setName("brick");
                stage.addActor(bricks.get(row).get(col));
            }
        }
    }

    private Color getRowColor(int row) {
        if (row >= COLORS.length) {
            return COLORS[row % COLORS.length];
        } else return COLORS[row];
    }

    private float getBallCenterX() {
        return ball.getX() + ball.getWidth() / 2f;
    }

    private float getBallLeft() {
        return ball.getX();
    }

    private float getBallRight() {
        return ball.getX() + ball.getWidth();
    }

    private float getBallCenterY() {
        return ball.getY() + ball.getHeight() / 2f;
    }

    private float getBallTop() {
        return ball.getY() + ball.getHeight();
    }

    private float getBallBottom() {
        return ball.getY();
    }

    private float getPaddleCenterX() {
        return paddle.getX() + paddle.getWidth() / 2f;
    }

    private void scaleVelocity(float scalar) {
        vx *= scalar;
        vy *= scalar;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        update(delta);
        ScreenUtils.clear(.1f, .105f, .15f, 1f);
        stage.getBatch().begin();
        game.pixellari64.setColor(1f, 1f, 1f, .5f);
        game.pixellari64.draw(stage.getBatch(), String.valueOf(score), 30f, 720f - 30f);
        stage.getBatch().end();
        stage.act(delta);
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
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) Gdx.input.setCursorCatched(!Gdx.input.isCursorCatched());
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        paddle.setX(screenX - paddle.getWidth() / 2f);
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
