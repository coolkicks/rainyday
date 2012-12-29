package com.me.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Plant {
	private String name;
	private Texture imageSprite;

	private final int width = 48;
	private int height;
	
	private int difficulty;
	private int stage;
	private int stages;
	private int points;
	
	private boolean complete;
	
	public Plant() {
		
	}
	
	public Plant(String vName, String filePath) {
		imageSprite = new Texture(Gdx.files.internal(filePath));
		stage = 0;
		difficulty = 0;
		points = 0;
		stages = imageSprite.getWidth()/width;
		height = imageSprite.getHeight();

		name = vName;
		
		complete = false;
	}
	
	public Plant(String vName, Texture img) {
		imageSprite = img;
		stage = 0;
		difficulty = 0;
		points = 0;
		stages = imageSprite.getWidth()/width;
		height = imageSprite.getHeight();

		name = vName;
		
		complete = false;
	}
	
	public void resetPlant(String vName, Texture img) {
		imageSprite = img;
		points = 0;
		stage = 0;
		difficulty = 0;
		stages = imageSprite.getWidth()/width;
		height = imageSprite.getHeight();

		name = vName;
		
		complete = false;
		
	}
	
	public void dispose(){
		imageSprite.dispose();
	}
	
	public void addPoint() {
		points++;
		if(points >= (10+difficulty)*(stage+1)) {
			if(stage+1 < stages) {
				stage ++;
			} else {
				complete = true;
			}
		}
	}
	
	public void addPoints(int pts) {
		points += pts;
		if(points >= (10+difficulty)*(stage+1)) {
			if(stage+1 < stages) {
				stage ++;
			} else {
				complete = true;
			}
		}
	}
	
	public boolean getComplete() {
		return complete;
	}
	
	public TextureRegion getFrame() {
		return new TextureRegion(imageSprite, stage*width, 0, width, height);
	}
	
	public Texture getImageSprite() {
		return imageSprite;
	}
	
	public int getStages() {
		return stages;
	}

	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
	}
	public int getDifficulty() {
		return difficulty;
	}
	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}
	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public String getName() {
		return name;
	}

	public int getPoints() {
		return points;
	}	
	
}
