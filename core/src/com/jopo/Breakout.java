package com.jopo;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Breakout extends Game {

	Skin uiSkin;

	Texture ballTexture;
	Texture brickTexture;
	Texture paddleTexture;

	Sound bounceSound;
	Sound breakSound;

	@Override
	public void create () {
		uiSkin = new Skin(Gdx.files.internal("ui\\uiskin.json"));

		ballTexture = new Texture(Gdx.files.internal("images\\ball.png"));
		brickTexture = new Texture(Gdx.files.internal("images\\brick.png"));
		paddleTexture = new Texture(Gdx.files.internal("images\\paddle.png"));

		bounceSound = null;
		breakSound = null;

		setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		uiSkin.dispose();
		ballTexture.dispose();
		brickTexture.dispose();
		paddleTexture.dispose();
		bounceSound.dispose();
		breakSound.dispose();
	}
}
