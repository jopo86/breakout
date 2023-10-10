package com.jopo;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Breakout extends Game {

	Skin uiSkin;
	BitmapFont pixellari64;

	Texture ballTexture;
	Texture brickTexture;
	Texture paddleTexture;

	Sound bounceSound;
	Sound gameOverSound;
	Sound victorySound;

	@Override
	public void create () {
		uiSkin = new Skin(Gdx.files.internal("ui\\uiskin.json"));
		pixellari64 = new BitmapFont(Gdx.files.internal("fonts\\pixellari-64.fnt"));

		ballTexture = new Texture(Gdx.files.internal("images\\ball.png"));
		brickTexture = new Texture(Gdx.files.internal("images\\brick.png"));
		paddleTexture = new Texture(Gdx.files.internal("images\\paddle.png"));

		bounceSound = Gdx.audio.newSound(Gdx.files.internal("audio\\bounce.ogg"));
		gameOverSound = Gdx.audio.newSound(Gdx.files.internal("audio\\game-over.ogg"));
		victorySound = Gdx.audio.newSound(Gdx.files.internal("audio\\victory.ogg"));

		setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		uiSkin.dispose();
		pixellari64.dispose();
		ballTexture.dispose();
		brickTexture.dispose();
		paddleTexture.dispose();
		bounceSound.dispose();
		gameOverSound.dispose();
		victorySound.dispose();
	}
}
