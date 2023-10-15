package com.jopo;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
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
    private SpriteBatch batch;
    private Table pauseTable;
    private Dialog gamePausedDialog;
    private Dialog escToResumeDialog;
    private Dialog qToQuitDialog;
    private ArrayList<Color> actorColors;
    private boolean isPaused = false;
    private float lastPaddleX;
    private int lastMouseX;

    private int score = 0;
    private int lives = 3;
    private int vx = 0;
    private int vy = 0;
    private float v = 0;
    private float pv = 0;

    private boolean tempBool = false;
    private String tempString = "";
    private Actor tempActor = new Actor();

    public GameScreen(final Breakout game) {
        this.game = game;
        vy = -Math.abs(game.startVelocity);
        lastMouseX = Gdx.input.getX();

        stage = new Stage(new ScreenViewport());

        ball = new Image(game.ballTexture);
        ball.setSize(BALL_DIMENSIONS.width, BALL_DIMENSIONS.height);
        ball.setPosition(Gdx.graphics.getWidth() / 2f - ball.getWidth() / 2f, 150f);
        ball.setName("ball");

        paddle = new Image(game.paddleTexture);
        paddle.setSize(PADDLE_DIMENSIONS.width, PADDLE_DIMENSIONS.height);
        paddle.setPosition(Gdx.graphics.getWidth() / 2f - paddle.getWidth() / 2f, 50f);
        paddle.setName("paddle");
        lastPaddleX = paddle.getX();

        batch = new SpriteBatch();

        stage.addActor(ball);
        stage.addActor(paddle);
        populateBricks();

        actorColors = new ArrayList<>();

        pauseTable = new Table();
        pauseTable.setFillParent(true);
        pauseTable.align(Align.center | Align.top);
        pauseTable.setName("pauseTable");

        gamePausedDialog = new Dialog("GAME PAUSED", game.uiSkin, "large");
        gamePausedDialog.setName("gamePausedDialog");
        escToResumeDialog = new Dialog("[ESC] TO RESUME", game.uiSkin, "default");
        escToResumeDialog.setName("escToResumeDialog");
        qToQuitDialog = new Dialog("[Q] TO QUIT", game.uiSkin, "default");

        pauseTable.padTop(290).add(gamePausedDialog).padBottom(60f).row();
        pauseTable.add(escToResumeDialog).padBottom(30f).row();
        pauseTable.add(qToQuitDialog);

        Gdx.input.setInputProcessor(this);
        Gdx.input.setCursorCatched(true);
    }

    private void update(float delta) {
        // don't update if paused
        if (isPaused) return;

        // win test
        tempBool = true;
        for (Actor actor : stage.getActors()) if (actor.getName().equals("brick")) tempBool = false;
        if (tempBool) {
            win();
        }

        pv = (getPaddleCenterX() - lastPaddleX) / delta;

        v = (float)Math.sqrt(vx*vx + vy*vy);
        ball.moveBy(vx * delta, vy * delta);

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
                        vy = Math.abs((int)Math.sqrt(v*v - vx*vx));

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
        if (getBallTop() >= 720 && vy > 0) { // ceiling collision
            vy = -Math.abs(vy);
            game.bounceSound.play();
        }
        if (getBallTop() <= 0 && vy < 0) { // floor collision = death
            lose();
        }
        if (getBallLeft() <= 0 && vx < 0) { // left wall collision
            vx = Math.abs(vx);
            game.bounceSound.play();
        }
        if (getBallRight() >= 1280 && vx > 0) { // right wall collision
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

    private void win() {
        isPaused = true;
        saveHighScore();
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                game.setScreen(new WinScreen(game, score));
            }
        }, 1f);
    }

    private void lose() {
        isPaused = true;
        lives--;
        if (lives == 0) {
            saveHighScore();
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    game.setScreen(new GameOverScreen(game, score));
                }
            }, 1f);
        } else {
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    ball.setPosition(Gdx.graphics.getWidth() / 2f - ball.getWidth() / 2f, 325f);
                    paddle.setPosition(Gdx.graphics.getWidth() / 2f - paddle.getWidth() / 2f, 50f);
                    vy = (int) -Math.abs(v);
                    vx = 0;
                    isPaused = false;
                }
            }, 1f);

        }
    }

    private void saveHighScore() {
        if (score > game.highScore) {
            FileUtils.writeFile(Gdx.files.internal("save\\highScore.save"), String.valueOf(score));
            game.highScore = score;
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
        stage.act(delta);
        stage.draw();
        batch.begin();
        game.pixellari64.setColor(1f, 1f, 1f, .5f);
        game.pixellari64.draw(batch, String.valueOf(score), 30f, 720 - 30f);
        batch.end();
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
        if (keycode == Input.Keys.ESCAPE) {
            if (!isPaused) {
                isPaused = true;
                Gdx.input.setCursorCatched(false);
                lastMouseX = (int)paddle.getX();
                stage.addActor(pauseTable);
            }
            else {
                Gdx.input.setCursorCatched(true);
                Gdx.input.setCursorPosition((int)paddle.getX(), (int)(Gdx.graphics.getHeight() / 2f));
                pauseTable.remove();
                isPaused = false;
            }
        };
        if (keycode == Input.Keys.Q && isPaused) Gdx.app.exit();
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
        if (!isPaused) {
            int deltaX = screenX - lastMouseX;
            paddle.moveBy(deltaX, 0);
            paddle.setX(MathUtils.clamp(paddle.getX(), 0 , 1280 - paddle.getWidth()));
        }
        lastMouseX = screenX;
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
