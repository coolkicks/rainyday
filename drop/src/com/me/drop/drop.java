package com.me.drop;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import com.me.drop.Plant;
import com.me.drop.Raindrop;

public class drop implements ApplicationListener {
   Texture bucketImage;
   Texture bgImage;
   Texture fgImage;
   Texture levelUp;
   Texture titleText;
   Sound dropSound;
   Music rainMusic;
   SpriteBatch batch;
   OrthographicCamera camera;
   Rectangle bucket;
   Rectangle screen;
   Array<Raindrop> raindrops;
   Plant plant;
   AssetManager manager;
   
   long lastDropTime;
   
   final int WIDTH = 800;
   final int HEIGHT = 480;
   final int BUCKETWIDTH = 48;
   final int BUCKETHEIGHT = 48;
   final int SCENEFLOOR = 20;
   final int BUCKETSPEED = 200;
   final int SECOND = 1000000000;
   
   int dropAccel = 0;
   int dropsCaught = 0;
   int dropsFallen = 0;
   int dropsMissed = 0;
   int plantsGrown = 0;
   int difficulty = 0;
   boolean isLevelUp = false;
   boolean isPlaying = false;
   
   public void loadAssets() {
	   manager.load("bucket.png", Texture.class);
	   manager.load("drop1.png", Texture.class);
	   manager.load("drop3.png", Texture.class);
	   manager.load("sprites/sunFlower.png", Texture.class);
	   manager.load("sprites/yellowFlower.png", Texture.class);
	   manager.load("sprites/purpleFlower.png", Texture.class);
	   manager.load("background2.png", Texture.class);
	   manager.load("foreground2.png", Texture.class);
	   manager.load("drop.wav", Sound.class);
	   manager.load("rain.mp3", Music.class);
	   manager.load("txtLevelUp.png", Texture.class);
	   manager.load("titleText.png", Texture.class);
	   
	   manager.update();
	   manager.finishLoading();
   }
   
   @Override
   public void create() {
	  //setup Screen rectangle for sub classes
	  screen = new Rectangle();
	  screen.width = WIDTH;
	  screen.height = HEIGHT;
	  
	  // declare the asset manager
	  manager = new AssetManager();
	  //load the assets
	  loadAssets();
	  
      // load the images for the droplet and the bucket, 48x48 pixels each
      bucketImage = manager.get("bucket.png", Texture.class);
      
      //load the plant sprite
      plant = new Plant("Yellow Flower", manager.get("sprites/yellowFlower.png", Texture.class));
      
      //plant.setDifficulty(difficulty);
      plant.setDifficulty(-5);
      
      //load the background image, 800x480
      bgImage = manager.get("background2.png", Texture.class);
      fgImage = manager.get("foreground2.png", Texture.class);
      titleText = manager.get("titleText.png", Texture.class);
      
      // load the drop sound effect and the rain background "music"
      dropSound = manager.get("drop.wav", Sound.class);
      rainMusic = manager.get("rain.mp3", Music.class);
      
      //load level up image
      levelUp = manager.get("txtLevelUp.png", Texture.class);
      
      // start the playback of the background music immediately
      rainMusic.setLooping(true);
      rainMusic.play();
      
      // create the camera and the SpriteBatch
      camera = new OrthographicCamera();
      camera.setToOrtho(false, WIDTH, HEIGHT);
      batch = new SpriteBatch();
      
      // create a Rectangle to logically represent the bucket
      bucket = new Rectangle();
      bucket.x = WIDTH / 2 - BUCKETWIDTH / 2; // center the bucket horizontally
      bucket.y = SCENEFLOOR; // bottom left corner of the bucket is 20 pixels above the bottom screen edge
      bucket.width = BUCKETWIDTH;
      bucket.height = BUCKETHEIGHT;
      
      // create the raindrops array and spawn the first raindrop
      raindrops = new Array<Raindrop>();
      spawnRaindrop();
   }
   

   
   private void spawnRaindrop() {
	   Raindrop raindrop = new Raindrop(1, manager, screen);
	   raindrops.add(raindrop);
	   lastDropTime = TimeUtils.nanoTime();
	   dropsFallen++;
   }
   
   @Override
   public void render() {

      // clear the screen with a dark blue color. The
      // arguments to glClearColor are the red, green
      // blue and alpha component in the range [0,1]
      // of the color to be used to clear the screen.
      Gdx.gl.glClearColor(0, 0, 0.2f, 1);
      Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
      
      // tell the camera to update its matrices.
      camera.update();
      
      // tell the SpriteBatch to render in the
      // coordinate system specified by the camera.
      batch.setProjectionMatrix(camera.combined);
      
      // begin a new batch and draw the bucket and
      // all drops
      batch.begin();
	      batch.draw(bgImage, 0, 0);
	      if(isPlaying) {
		      batch.draw(bucketImage, bucket.x, bucket.y);
		      batch.draw(plant.getFrame(), bucket.x, bucket.y+BUCKETHEIGHT);
		      for(Raindrop raindrop: raindrops) {
		    	  batch.draw(raindrop.getImage(), raindrop.getX(), raindrop.getY());
		      }
	      } else {
	    	  batch.draw(titleText, screen.width/2 - titleText.getWidth()/2, screen.height/2 - titleText.getHeight()/2);
	      }
	      batch.draw(fgImage, 0, 0);
      batch.end();
      
      if(!isPlaying){
    	  if(Gdx.input.isTouched())
    		  isPlaying = true;
      } else {
	      // process user input
	      if(Gdx.input.isTouched()) {
	         Vector3 touchPos = new Vector3();
	         touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
	         camera.unproject(touchPos);
	         bucket.x = touchPos.x - BUCKETWIDTH / 2;
	      }
	      if(Gdx.input.isKeyPressed(Keys.LEFT)) bucket.x -= BUCKETSPEED * Gdx.graphics.getDeltaTime();
	      if(Gdx.input.isKeyPressed(Keys.RIGHT)) bucket.x += BUCKETSPEED * Gdx.graphics.getDeltaTime();
	      
	      // make sure the bucket stays within the screen bounds
	      if(bucket.x < 0) bucket.x = 0;
	      if(bucket.x > WIDTH - BUCKETWIDTH) bucket.x = WIDTH - BUCKETWIDTH;
	      
	      // check if we need to create a new raindrop
	      if(TimeUtils.nanoTime() - lastDropTime > SECOND*.33) {
	    	  spawnRaindrop();
	      }
	      
	      // move the raindrops, remove any that are beneath the bottom edge of
	      // the screen or that hit the bucket. In the later case we play back
	      // a sound effect as well.
	
	      Iterator<Raindrop> iter = raindrops.iterator();
	      while(iter.hasNext()) {
	
	         Raindrop raindrop = iter.next();
	         raindrop.fall(dropsFallen/((difficulty+1)*2), Gdx.graphics.getDeltaTime());
	         if(raindrop.getY() + raindrop.getImage().getHeight() < 0) 
	        	 iter.remove();
	         if(raindrop.getRect().overlaps(bucket)) {
	        	dropsCaught++;
	        	plant.addPoints(raindrop.getPoints());
	            dropSound.play();
	            iter.remove();
	            if(plant.getComplete()) {
	     		   isLevelUp = true;
	            	//load the next flower
	            	difficulty++;
	            	plantsGrown++;
	            	dropsMissed = 0;
	            	if(plantsGrown == 1){
	            		plant.resetPlant("Purple Flower", manager.get("sprites/purpleFlower.png", Texture.class));
	            		plant.setDifficulty(difficulty);
	            	} else {
	            		plant.resetPlant("Sun Flower", manager.get("sprites/sunFlower.png", Texture.class));
	            		plant.setDifficulty(difficulty);
	            	}
	            	plant.setDifficulty(difficulty);
	
	            } 
	            
	         }
	      }
      }
   }
   
   @Override
   public void dispose() {
      // dispose of all the native resources
	  for(Raindrop raindrop: raindrops) {
		  raindrop.dispose();
	  }
      bucketImage.dispose();
      bgImage.dispose();
      fgImage.dispose();
      dropSound.dispose();
      rainMusic.dispose();
      batch.dispose();
      plant.dispose();
      titleText.dispose();
      levelUp.dispose();
      manager.dispose();
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
}
