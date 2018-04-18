package com.austinlee.shootingstar;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;

import java.util.Random;

import static java.lang.Math.abs;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	int gameState;

	//Textures
	Texture background;
	Texture trump;
	Texture fem;
	Texture gameOver;

	int screenWidth;
	int screenHeight;
	int trumpCenterX;
	int trumpY;
	float femWidth;
	float femHeight;

	int velocity;
	float femVelocity = 4;
	int flag;
	int health;
	int score;

	int numOfFem = 5;
	float[] femX = new float[numOfFem];
	float[] femY = new float[numOfFem];

	int[] touched = new int[numOfFem];

	Random rand = new Random();

	Circle trumpCircle;
	Circle[] femCircle = new Circle[numOfFem];

	BitmapFont healthInd;
	BitmapFont scoreInd;

	private Music sstar;

	@Override
	public void create () {
		batch = new SpriteBatch();
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		healthInd = new BitmapFont();
		healthInd.setColor(Color.WHITE);
		healthInd.getData().setScale(5);
		scoreInd = new BitmapFont();
		scoreInd.setColor(Color.GREEN);
		scoreInd.getData().setScale(5);

		trump = new Texture("trump2.png");
		fem = new Texture("fem.png");
		femWidth = fem.getWidth();
		femHeight = fem.getHeight();
		gameOver = new Texture("gameover.jpg");

		trumpCenterX = screenWidth/2 - trump.getWidth()/2;
		trumpY = screenHeight/2 - trump.getHeight()/2;

		sstar = Gdx.audio.newMusic(Gdx.files.internal("ss.mp3"));
		sstar.setLooping(true);

		background = new Texture("bg.jpg");
		initialize();
	}

	public void initialize()
	{
		trumpCenterX = screenWidth/2 - trump.getWidth()/2;
		trumpY = screenHeight/2 - trump.getHeight()/2;
		velocity = 0;
		score = 0;
		gameState = 0;
		flag = 0;
		health = 25;
		int min = screenWidth/2 + (int)femWidth/2;
		for (int i = 0; i < numOfFem; i++) {
			femY[i] = rand.nextInt((screenWidth - min) + 1) + min;
			femX[i] = rand.nextInt(screenHeight);
			femCircle[i] = new Circle(femX[i] + femWidth/2, femY[i], fem.getWidth()/2);
		}//endfor
		for(int j = 0; j < numOfFem; j++){
			touched[j] = 0;
		}

		trumpCircle = new Circle(screenWidth/2, trumpY + trump.getHeight()/2, trump.getWidth()/2);
		sstar.play();
	}

	public void drawTrump(){
		batch.draw(trump, trumpCenterX, trumpY);
	}

	public void drawFemmes(){
		int min = screenWidth/2 + (int)femWidth/2;
		for (int i = 0; i < numOfFem; i++) {
			if (femX[i] < -femWidth){
				femX[i] =  rand.nextInt((screenWidth - min) + 1) + min;
				femY[i] =  rand.nextInt(screenHeight);
				femCircle[i] = new Circle(femX[i] + femWidth/2, femY[i], fem.getWidth()/2);
				touched[i] = 0;
			} else {
				femX[i] = femX[i] - femVelocity;
				femCircle[i].set(femX[i] + femWidth/2, femY[i], fem.getWidth()/2);
				score++;
			}//endid

			batch.draw(fem, femX[i], femY[i]);
		}
	}

	public void checkIntersect(){
		for (int i = 0; i < numOfFem; i++){
			if (Intersector.overlaps(trumpCircle, femCircle[i]) && touched[i] == 0){
				touched[i] = 1;
				health--;
				break;
			}//if
		}//endfor
		drawFemmes();
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, screenWidth, screenHeight);
		if (gameState == 1 ) {
			if (Gdx.input.justTouched() && trumpY > 0) {
				if(flag == 0){
					velocity = -5;
					flag = 1;
				}
				else if(flag == 1){
					velocity = 5;
					flag = 0;
				}
			}
			if (trumpY > 0 && trumpY < screenHeight && health != 0){
				trumpY = trumpY + velocity;
			}
			else if(trumpY >= screenHeight && health != 0){
				int bounce = trumpY - screenHeight;
				trumpY = screenHeight - bounce;
			}
			else if(trumpY <= 0 && health != 0){
				int bounce =  abs(trumpY);
				trumpY = bounce;
			}
			else if(health <= 0 ) {
				gameState = 2;
			}

		}else if (gameState == 0) {
			if (Gdx.input.justTouched()) {
				gameState = 1;
			}
		} else if (gameState == 2) {
			batch.draw(gameOver, screenWidth/2 - gameOver.getWidth()/2, screenHeight/2);
			if (Gdx.input.justTouched()) {
				gameState = 1;
				initialize();
			}//if
		}
		drawTrump();
		healthInd.draw(batch, "HP: "+health, 100, 200);
		scoreInd.draw(batch, "SCR: " + score, 100, 400);

		trumpCircle.set(screenWidth/2, trumpY + trump.getHeight()/2, trump.getWidth()/2);
		checkIntersect();

		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}
}
