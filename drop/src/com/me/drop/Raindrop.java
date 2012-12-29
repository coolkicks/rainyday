package com.me.drop;

import java.util.Random;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Raindrop {
	private int points;
	private int speed;
	private Texture image;
	
	private Rectangle rect;
	
	public Raindrop() {
		
	}
	
	public Raindrop(int difficulty, AssetManager mgr, Rectangle screen) {
		Random rnd = new Random();
		rect = new Rectangle();
		
		//set drop speed based on difficulty
		speed = rnd.nextInt(difficulty*25) + 75;
		
		//set drop points
		if(rnd.nextInt(10)+1 <= 7) {
			points = 1;
			image = mgr.get("drop1.png", Texture.class);
		} else {
			points = 3;
			image = mgr.get("drop3.png", Texture.class);
		}
		
		rect.width = image.getWidth();
		rect.height = image.getHeight();
		rect.y = screen.height;
		rect.x = MathUtils.random(0, screen.width-rect.width);
		
	}
	
	public void dispose() {
		image.dispose();
	}
	
	public void fall(float offset, float elapsedTime) {
		rect.y -= (speed + offset) * elapsedTime;
	}
	
	public int getPoints() {
		return points;
	}

	public int getSpeed() {
		return speed;
	}

	public Texture getImage() {
		return image;
	}

	public float getY() {
		return rect.y;
	}

	public float getX() {
		return rect.x;
	}
	
	public Rectangle getRect() {
		return rect;
	}
	
}
