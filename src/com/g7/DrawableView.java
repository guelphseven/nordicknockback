package com.g7;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.WindowManager.LayoutParams;
import android.view.*;
import android.content.pm.ActivityInfo;
import android.graphics.*;
import android.graphics.drawable.*;
import android.content.Context;
import java.math.*;
import java.util.*;
import android.graphics.Matrix;

class DrawableView extends View implements BlowListener, ShakeListener {
    private final static float BASE_HEIGHT = 320.0f;
    private final static float BASE_WIDTH = 480.0f;
	private final static float BASE_SPEED = 0.75f;
	
	private final static float ROW1_Y = 96.0f;
	private final static float ROW2_Y = 192.0f;
	private final static float ROW3_Y = 288.0f;
	
	private final static float BADDIE_RADIUS = 10.0f;
	private final static float STONE_RADIUS = 5.0f;
	
	private final static float BUTTON_X = 400.0f;
	private final static float BUTTON_Y = 0.0f;
	private final static float BUTTON_RADIUS = 30.0f;
	private final static float FINGER_RADIUS = 30.0f;
	
    private final static float ADD_TEXT_X = 440.0f;
    private final static float ADD_TEXT_Y = 25.0f;
	
	private final static float TOWER_Y_FUDGE = 64.0f;
	private final static float BADDIE_Y_FUDGE = 32.0f;
	private final static float KEG_Y_FUDGE = 32.0f;
    private final static float KEG_DROPPED_Y_FUDGE = 20.0f;
    
	private final static float TOWER_FIRE_RADIUS = 75.0f;
	private final static float DEAD_SPACE_Y = 32.0f;
   
    private final static float TEXT_SIZE = 10.0f;
    private final static float TEXT_SIZE_BIG = 15.0f;
    
	private final static long TOWER_FIRE_INTERVAL = 1750;
    
    public static float[] linesHorizontal = {
		0.0f, ROW1_Y, 500.0f, ROW1_Y,
		0.0f, ROW2_Y, 500.0f, ROW2_Y,
		0.0f, ROW3_Y, 500.0f, ROW3_Y,
    };
    
	Context mContext;
    Bitmap backgroundImg, icon, vikingBmp, rockBmp, 
    	   kegBmp, towerBmp, blowBmp, shakeBmp, buttonBmp;
    Paint paint;
    Rect widthHeight, iconWH;
    
    boolean addTower;
    int fps =0, frameCount = 0, gold = 20;
    long lastFrameTime = 0, lastBaddieTime = 0, lastTouchTime = 0;
    int level = 1, numBaddies = 0, numKegs = 3;

    int numShakes = 0, numBlows = 0;
    
    TextDisplay blowDisp, shakeDisp, lvlDisp, towerDisp;
    
    Vector<Baddie> baddies1 = new Vector<Baddie>();
    Vector<Baddie> baddies2 = new Vector<Baddie>();
    Vector<Baddie> baddies3 = new Vector<Baddie>();
    
    Vector<Tower> towers1 = new Vector<Tower>();
    Vector<Tower> towers2 = new Vector<Tower>();
    Vector<Tower> towers3 = new Vector<Tower>();
    
    Vector<Keg> kegs = new Vector<Keg>();
    Vector<Powerup> powerups = new Vector<Powerup>();

    Random random;
    
    BlowDetect blowDetector;
    Shake shakeDetector;
    Sound sound;
    
    private float _width, _height;
    private float _scaleX, _scaleY;
    
	public DrawableView(Context context, float width, float height) {
		super(context);
		mContext = context;
		
        _width = width;
        _height = height;
        
        _scaleX = _width/BASE_WIDTH;
        _scaleY = _height/BASE_HEIGHT;
        
		loadBitmaps();

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(5.0f);
        paint.setTextSize(10.0f);
        
        blowDisp = new TextDisplay(0,"BLOW INTO THE MIC!", 130.0f, 150.0f);
        shakeDisp = new TextDisplay(0,"SHAKE THE PHONE!", 140.0f, 200.0f);
        lvlDisp = new TextDisplay(0,"LEVEL ", 200.0f, 125.0f);
        towerDisp = new TextDisplay(60,null, 0.0f, 0.0f);

        widthHeight = new Rect(0,0,backgroundImg.getWidth(),backgroundImg.getHeight());
        random = new Random(System.currentTimeMillis());
        
        kegs.add(new Keg(1, BASE_WIDTH/2, ROW1_Y-KEG_Y_FUDGE));
        kegs.add(new Keg(2, BASE_WIDTH/2, ROW2_Y-KEG_Y_FUDGE));
        kegs.add(new Keg(3, BASE_WIDTH/2, ROW3_Y-KEG_Y_FUDGE));

        blowDetector = new BlowDetect(this);
        shakeDetector = Shake.getShake(context, this);
        shakeDetector.setThreshold(35);
        
        sound = new Sound(context);
	}
	
	private void loadBitmaps() {
        backgroundImg = BitmapFactory.decodeResource(getResources(), R.drawable.world);
        vikingBmp = BitmapFactory.decodeResource(getResources(), R.drawable.sprite_viking);
        rockBmp = BitmapFactory.decodeResource(getResources(), R.drawable.stone);
        kegBmp = BitmapFactory.decodeResource(getResources(), R.drawable.barrel);
        towerBmp = BitmapFactory.decodeResource(getResources(), R.drawable.tower);
        blowBmp = BitmapFactory.decodeResource(getResources(), R.drawable.blow); 
        shakeBmp = BitmapFactory.decodeResource(getResources(), R.drawable.shake); 
        buttonBmp = BitmapFactory.decodeResource(getResources(), R.drawable.button); 
	}
	
	private void playSound(int file) {
		sound.playSound(file);
	}

	protected void onDraw(Canvas canvas) {
		canvas.scale(_scaleX, _scaleY);
		canvas.drawBitmap(backgroundImg, widthHeight, widthHeight, null);
		
		handleRowConditions(canvas, paint);
    	
    	if( numKegs > 0 ) {
        	
    		//Tower button
    		drawAddTowerButton(canvas,paint);
        	drawTowers(canvas);
        
        	//Add and draw the three rows of baddies
        	addBaddies(level);
        	drawBaddies(canvas, baddies1, 1);
        	drawBaddies(canvas, baddies2, 2);
        	drawBaddies(canvas, baddies3, 3);

        	//Powerups
        	drawPowerups(canvas, paint);
        	drawPowerupsStatus(canvas, paint);
        	
    		//Kegs
        	drawKegs(canvas, paint);
        	displayKegStatus(canvas, paint);
		
    		//Set towers to fire and begin animating
        	buildFireList();
        	fireTowers(canvas, towers1);
        	fireTowers(canvas, towers2);
        	fireTowers(canvas, towers3);

        	displayTextStatus(canvas, paint);
 
        } else {
        	canvas.drawText("fps: "+fps, 25.0f, 25.0f, paint);
        	canvas.drawText("gold:"+gold, 75.0f, 25.0f, paint);
        	paint.setTextSize(15.0f);
        	canvas.drawText("Game Over", 200.0f, 125.0f, paint);
        	paint.setTextSize(10.0f);
        }
		invalidate();
		fps();
	}
	
	private void handleRowConditions(Canvas canvas, Paint paint) {
		int alpha = paint.getAlpha();
		if( addTower ) {
			paint.setAlpha(128);
			paint.setColor(Color.GREEN);
	    	canvas.drawLines(linesHorizontal, paint);
	    } else if( towerDisp.getPrintCounter() < 60 ) {
			paint.setColor(Color.RED);
	    	canvas.drawLines(linesHorizontal, paint);
			towerDisp.setPrintCounter(towerDisp.getPrintCounter() + 1);
		}
		paint.setAlpha(alpha);
    	paint.setColor(Color.WHITE);
	}
	
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	
		if ((System.currentTimeMillis() - lastTouchTime) > 500) {
			float x = event.getX();
			float y = event.getY();
			
			//Scale down big device touches
			x /= _scaleX;
			y /= _scaleY;
		
			//Check for add button click
			if( !addTower && checkCollision(x, BUTTON_X, y, BUTTON_Y, BUTTON_RADIUS, FINGER_RADIUS) ) {
				addTower = true;
			} else if( !addTower ) {
				//Looking for a powerup?
				checkPickupPowerup(x,y);
			} else if( addTower && y > DEAD_SPACE_Y ) {
				//Add tower mode, is placement valid?
				boolean notAdded = true;
    			if( y <= ROW1_Y ) { 
    				notAdded = addTower( x, ROW1_Y - TOWER_Y_FUDGE, TOWER_FIRE_RADIUS, 1, TOWER_FIRE_INTERVAL);
    			} else if( y <= ROW2_Y ) {
    				notAdded = addTower( x, ROW2_Y - TOWER_Y_FUDGE, TOWER_FIRE_RADIUS, 2, TOWER_FIRE_INTERVAL);
    			} else {
    				notAdded = addTower( x, ROW3_Y - TOWER_Y_FUDGE, TOWER_FIRE_RADIUS, 3, TOWER_FIRE_INTERVAL);
    			}
    			if( notAdded ) {
    				towerDisp.setPrintCounter(0);
    			}
    			
				addTower = false;
			}

			lastTouchTime = System.currentTimeMillis();
			
			return true;
		} else {
			return false;
		}
    }
    
    private void drawKegs(Canvas canvas, Paint paint ) {
    	paint.setColor(Color.RED);
    	for(int i=0; i < kegs.size(); i++ ) {
    		if( !kegs.get(i).removed() ) {
    			if(kegs.get(i).pickedUp()) {
    				canvas.drawBitmap(kegBmp, kegs.get(i).getX(), kegs.get(i).getY()-KEG_DROPPED_Y_FUDGE, null);
    			} else {
    				canvas.drawBitmap(kegBmp, kegs.get(i).getX(), kegs.get(i).getY(), null);
    			}
    		}
    	}
    	paint.setColor(Color.WHITE);
    }
    
    private void drawAddTowerButton(Canvas canvas, Paint paint) {
		paint.setColor(Color.GREEN);
		//canvas.drawCircle( 460.0f, 25.0f, 40.0f, paint);
		canvas.drawBitmap(buttonBmp, BUTTON_X, BUTTON_Y, paint);
		paint.setColor(Color.WHITE);
		paint.setTextSize(TEXT_SIZE_BIG);
		canvas.drawText("Add", ADD_TEXT_X, ADD_TEXT_X, paint);
		paint.setTextSize(TEXT_SIZE);
    }
	
	private void drawPowerups(Canvas canvas, Paint paint) {
		for( int i = 0; i < powerups.size(); i++ ) {
			if( powerups.get(i).getStatus() == Powerup.STATUS_DROPPED ) {
				paint.setColor(powerups.get(i).getColor());
				canvas.drawCircle(powerups.get(i).getX(), powerups.get(i).getY()+7.5f, 7.5f, paint);
				paint.setColor(Color.WHITE);
			}
		}
	}
	
	private void drawPowerupsStatus(Canvas canvas, Paint paint ) {
		canvas.drawText(" x " + numBlows, 32.0f, 80.0f, paint );
		canvas.drawText(" x " + numShakes, 32.0f, 55.0f, paint );
		
		//paint.setColor(Color.DKGRAY);
		//canvas.drawCircle( 25.0f, 50.0f, 7.5f, paint);
		canvas.drawBitmap(shakeBmp, 15.0f, 34.0f, null);
		
		//paint.setColor(Color.CYAN);
		//canvas.drawCircle( 25.0f, 75.0f, 7.5f, paint );
		canvas.drawBitmap(blowBmp, 7.5f, 70.0f, null);
		//paint.setColor(Color.WHITE);
	}
	
	private void displayKegStatus(Canvas canvas, Paint paint) {
		paint.setColor(Color.BLACK);
		for(int i = 0; i < numKegs; i++ ) {
			canvas.drawBitmap(kegBmp,  200.0f + (i*25.0f), 15.0f, null);
		}
    	canvas.drawText("baddies:"+numBaddies, 300.0f, 25.0f, paint);
		paint.setColor(Color.WHITE);
	}
	
	private void displayTextStatus(Canvas canvas, Paint paint) {
    	canvas.drawText("fps: "+fps, 25.0f, 25.0f, paint);
    	canvas.drawText("gold: "+gold, 75.0f, 25.0f, paint);
    	canvas.drawText("level: "+level, 125.0f, 25.0f, paint);

    	if( gold < 5 && addTower ) {
    		canvas.drawText("Not enough gold!", 175.0f, 150.0f, paint);
    	}
		if( blowDisp.getPrintCounter() < 120 && numBlows > 0 ) {
			paint.setTextSize(25.0f);
			blowDisp.draw(canvas, paint);
			blowDisp.setPrintCounter(blowDisp.getPrintCounter() + 1);
    		paint.setTextSize(10.0f);
		} else if( shakeDisp.getPrintCounter() < 120 && numShakes > 0 ) {
			paint.setTextSize(25.0f);
			shakeDisp.draw(canvas,paint);
			shakeDisp.setPrintCounter(shakeDisp.getPrintCounter() + 1);
    		paint.setTextSize(10.0f);
		}
    	if( lvlDisp.getPrintCounter() < 120 ) {
    		paint.setTextSize(25.0f);
    		lvlDisp.setText("LEVEL "+level);
    		lvlDisp.draw(canvas, paint);
    		lvlDisp.setPrintCounter(lvlDisp.getPrintCounter() + 1);
    		paint.setTextSize(10.0f);
    	}
	}
    
    private void checkPickupPowerup(float x, float y) {
		for( int i=powerups.size()-1; i >= 0; i-- ) {
			if( powerups.get(i).getStatus() == Powerup.STATUS_DROPPED 
			 && checkCollision( x, powerups.get(i).getX(), y, powerups.get(i).getY(), 50.0f, 10.0f) ) {
				powerups.get(i).setStatus(Powerup.STATUS_PICKED_UP);
				if( powerups.get(i).getType() == Powerup.TYPE_BLOW ) {
					blowDetector.begin();
		    		numBlows++;
		    		blowDisp.setPrintCounter(0);
				} else {
					numShakes++;
					shakeDisp.setPrintCounter(0);
				}
				break;
			}
		}
    }
    
	private boolean addTower(float x, float y, float fireRadius, int row, long fireInterval) {
		boolean collides = false;
		if( gold >= 5 ) {
			if(row == 1) 
				collides = checkPlacement(towers1, x, y, collides, fireRadius, row, fireInterval);
			if(!collides && row == 2) {
				collides = checkPlacement(towers2, x, y, collides, fireRadius, row, fireInterval);
			}
			if( !collides && row == 3) {
				collides = checkPlacement(towers3, x, y, collides, fireRadius, row, fireInterval);
			}
		}
		return collides;
	}
	
	private boolean checkPlacement(Vector<Tower> towers, float x, float y, 
								boolean collides, float fireRadius, int row, long fireInterval) {
		if( towers.size() < 5 ) {
			for(int i = 0; i < towers.size(); i++ ) {
				if(checkCollision(x, towers.get(i).getX(), y-TOWER_Y_FUDGE, towers.get(i).getY(), 10.0f, 10.0f )) {
					collides = true;
					break;
				}
			}
			if( !collides ) {
				gold -= 5;
				towers.add(new Tower(getContext(), x, y, fireRadius, row, fireInterval));
				playSound(R.raw.toweradd);
			}
		} else {
			towerDisp.setPrintCounter(0);
			playSound(R.raw.error);
		}
		return collides;
	}
	
	int direction1 = 1, direction2 = 1, direction3 = 1;
	private void changeLevel() {
		float randDir = random.nextFloat();
		if( randDir > 0.5f ) {
			direction1 = -1;
		}
		randDir = random.nextFloat();
		if( randDir > 0.5f ) {
			direction2 = -1;
		}
		randDir = random.nextFloat();
		if( randDir > 0.5f ) {
			direction3 = -1;
		}
		level++;
		playSound(R.raw.levelup);
		lvlDisp.setPrintCounter(0);
		numBaddies = 0;
	}
	
	private void addBaddies(int level) {
		//Check for next level condition
		if( numBaddies < (level) * 10) {
			
			//Increase the speed at which baddies come
			int intervalReduce = level*100;
			if( intervalReduce > 1500 )
				intervalReduce = 1500;
			
    		if ((System.currentTimeMillis() - lastBaddieTime) > ((3000) - intervalReduce ) ) {
            	float y = 0.0f;
    			float x = 0.0f;
    			
    			float rand = random.nextFloat();
    			float lvlSpeed = (float)level/10.0f;
    			boolean added = false;

    			//Create a baddie on a random row
        		if( rand <= 0.3f ) {
        			if(!kegs.get(0).removed()) {
        				y = ROW1_Y - BADDIE_Y_FUDGE;
        				baddies1.add( new Baddie( getContext(), vikingBmp, (direction1 == -1) ? BASE_WIDTH : x, y, 
        						    (level*2), (BASE_SPEED+lvlSpeed) * direction1 ));
        				added = true;
        			}
        		}
        		if( rand <= 0.6f && !added  ) {
        			if(!kegs.get(1).removed()) {
        				y = ROW2_Y - BADDIE_Y_FUDGE;
        				baddies2.add( new Baddie( getContext(), vikingBmp, (direction2 == -1) ? BASE_WIDTH : x, y, 
        							(level*2), (BASE_SPEED+lvlSpeed) * direction2));
        				added = true;
        			}
        		} 
        		if( rand <= 0.9f && !added ) {
        			if(!kegs.get(2).removed()) {
        				y = ROW3_Y - BADDIE_Y_FUDGE;
        				baddies3.add( new Baddie( getContext(), vikingBmp, (direction3 == -1) ? BASE_WIDTH : x, y, 
        						    (level*2), (BASE_SPEED+lvlSpeed) * direction3 ) );
        				added = true;
        			}
        		}
        		
        		//If the baddie wasn't added at random, find a row for him
        		ensureAdded(added, x, y, lvlSpeed);
        		
        		numBaddies++;
                lastBaddieTime = System.currentTimeMillis();
    		}
		} else {
			changeLevel();
		}
	}
	
	private void ensureAdded(boolean added, float x, float y, float lvlSpeed) {
		if( !added ) {
			for(int i=0; i<kegs.size(); i++) {
				if( !kegs.get(i).removed() && i == 0 ) {
    				y = ROW1_Y - BADDIE_Y_FUDGE;
    				baddies1.add( new Baddie( getContext(), vikingBmp, (direction1 == -1) ? BASE_WIDTH : x, y, 
    							(level+1), (BASE_SPEED+lvlSpeed) * direction1 ) );
    				break;
				} else if( !kegs.get(i).removed() && i == 1 ) {
    				y = ROW2_Y - BADDIE_Y_FUDGE;
    				baddies2.add( new Baddie( getContext(), vikingBmp, (direction2 == -1) ? BASE_WIDTH : x, y, 
    							(level+1), (BASE_SPEED+lvlSpeed) * direction2 ) );
    				break;
				} else if( !kegs.get(i).removed() && i == 2 ) {
    				y = ROW3_Y - BADDIE_Y_FUDGE;
    				baddies3.add( new Baddie( getContext(), vikingBmp, (direction3 == -1) ? BASE_WIDTH : x, y, 
    							(level+1), (BASE_SPEED+lvlSpeed) * direction3 ) );
    				break;
				}
			}
		}
	}
	
	private void drawBaddies(Canvas canvas, Vector<Baddie> baddies, int row) {
		for(int i=0; i < baddies.size(); i++ ) {
			Baddie baddie = baddies.get(i);
			
			if( !baddie.isDead() ) {
				
				baddie.draw(canvas,null);
				baddie.Update(System.currentTimeMillis());
    			baddie.setX( baddie.getX() + baddie.getSpeed() );
    			
				if( baddie.hasKeg()) {
					if(!baddie.getKeg().pickedUp())
						dropKeg(baddie);
					else
						moveKeg(baddie);
				}
				
				//Baddie reached end of screen
    			if( baddie.getX() > BASE_WIDTH || baddie.getX() < 0.0f ) {
    				if( baddie.hasKeg() ) {
    					baddie.getKeg().remove(true);
    					//dropKeg(baddie);
    					numKegs--;
						playSound(R.raw.kegloss);
    					if( numKegs <= 0 ) {
    						playSound(R.raw.gameover);
    					}
    				}
    				baddies.remove(baddie);
    				continue;
    			}
    			
    			//Baddie is dead, cleanup
    			if( baddie.getHealth() <= 0) {
    				baddie.setDead(true);
    				if( baddie.hasKeg() ) {
    					dropKeg(baddie);
    				} else {
    					//Based on random
    					dropPowerup(baddie);
    				}
    				baddie.playSound();
    				baddies.remove(baddie);
    				gold++;
    				continue;
    			}
    			
    			//Will the baddie get a keg?
    			for( int j = 0; j < kegs.size(); j++ ) {
    				if( !kegs.get(j).pickedUp() && checkCollision( baddie, kegs.get(j) ) ) {
    					pickupKeg(kegs.get(j),baddie);
    				}
    			}
			}
		}
	}
	
	//Drop it like its hot
	private void dropPowerup(Baddie baddie) {
		float drop = random.nextFloat();
		if( drop >= 0.7f ) {
			float type = random.nextFloat();
			if( type >= 0.5f ) {
				powerups.add(new ShakePowerup(getContext(), baddie.getX(), baddie.getY()));
				playSound(R.raw.pickup);
			} else {
				powerups.add(new BlowPowerup(getContext(), baddie.getX(), baddie.getY()));
				playSound(R.raw.pickup);
			}
		}
	}
	
	private void moveKeg(Baddie baddie) {
		if( baddie.hasKeg() ) {
			baddie.getKeg().setX(baddie.getX());
		}
	}
	
	private void dropKeg(Baddie baddie) {
		if( baddie.hasKeg() ) {
			baddie.getKeg().setX(baddie.getX());
			baddie.getKeg().pickUp(false);
			baddie.dropKeg();
		}
	}
	
	private void pickupKeg(Keg keg, Baddie baddie) {
		keg.pickUp(true);
		baddie.pickupKeg(keg);
	}
	
	private void drawTowers(Canvas canvas) {
		for( int i = 0; i < towers1.size(); i++ ) {
			Tower tower = towers1.get(i);
			canvas.drawBitmap(towerBmp,tower.getX(), tower.getY(), null);
		}
		for( int i = 0; i < towers2.size(); i++ ) {
			Tower tower = towers2.get(i);
			canvas.drawBitmap(towerBmp,tower.getX(), tower.getY(), null);
		}
		for( int i = 0; i < towers3.size(); i++ ) {
			Tower tower = towers3.get(i);
			canvas.drawBitmap(towerBmp,tower.getX(), tower.getY(), null);
		}
	}
	
	private void fireAtBaddies(int i, Vector<Baddie> baddies, Vector<Tower> towers) {
		for( int j = 0; j < baddies.size(); j++ ) {
			Tower tower = towers.get(i);
			if( !tower.firing() ) {
				if((System.currentTimeMillis() - tower.lastFireTime()) > tower.getFireInterval() ) {
					if( checkCollision(tower, baddies.get(j)) ) {
						tower.setBaddie(baddies.get(j));
						tower.setFiring(true);
						tower.setLastFireTime(System.currentTimeMillis());
						tower.playSound();
						break;
					}
				}
			}
		}
	}
	
	private void buildFireList() {
		for( int i = 0; i < towers1.size(); i++ ) {
			fireAtBaddies(i, baddies1, towers1);
		}
		for( int i = 0; i < towers2.size(); i++ ) {
			fireAtBaddies(i, baddies2, towers2);
		}
		for( int i = 0; i < towers3.size(); i++ ) {
			fireAtBaddies(i, baddies3, towers3);
		}
	}
	
	//Polymorph these...
	private boolean checkCollision(float x1, float x2, float y1, float y2, float r1, float r2) {
		float distX = x2 - x1;
		float distY = y2 - y1;
		float radii = r1 + r2;
		
		if( (distX * distX) + (distY * distY) < (radii * radii) ) {
			return true;
		}
			
		return false;
	}
	
	private boolean checkCollision(Tower tower, Baddie baddie) {
		return checkCollision(tower.getX(), baddie.getX(), tower.getY(), baddie.getY(), tower.getFireRadius(), 10.0f);
	}
	
	private boolean checkCollision(Baddie baddie, Keg keg ) {
		return checkCollision(keg.getX(), baddie.getX(), keg.getY(), baddie.getY(), 10.0f, 10.0f);
	}
	
	private boolean checkCollision(Tower tower) {
		//Check bullet vs target radius collision
		float distX = tower.fireX() - tower.getBaddie().getX();
		float distY = tower.fireY() - tower.getBaddie().getY();
		float radii = BADDIE_RADIUS + STONE_RADIUS;
		
		if( (distX * distX) + (distY * distY) < (radii * radii) ) {
			tower.getBaddie().setHealth(tower.getBaddie().getHealth() - 1);
			return true;
		}
			
		return false;
	}
	
	private void fireTowers(Canvas canvas, Vector<Tower> towers) {
		for(int i=0; i< towers.size(); i++) {
			Tower tower = towers.get(i);
			if( tower.firing() ) {
				//Home that missile onto the targets pos
				tower.setTarget(tower.getBaddie().getX(),tower.getBaddie().getY());
				
				tower.setFireX(tower.fireX() + tower.getTargetX());
				tower.setFireY(tower.fireY() + tower.getTargetY());
				
				//canvas.drawCircle( 5.0f, paint);
				canvas.drawBitmap(rockBmp, tower.fireX(), tower.fireY(), null );
				
				//Kill tower firing if collides or fires offscreen
				if( tower.fireY() <= 0 ||tower.fireY() >= BASE_HEIGHT || 
					tower.fireX() <= 0 || tower.fireX() >= BASE_WIDTH || checkCollision(tower) ) {
					tower.setFiring(false);
					tower.setFireX(tower.getX());
					tower.setFireY(tower.getY());
				}

			}
		}
	}
	
	private void fps() {
        frameCount++;
        if ((System.currentTimeMillis() - lastFrameTime) > 1000) {
            fps = frameCount;
            frameCount = 0;
            lastFrameTime = System.currentTimeMillis();
        }
	}
	
	private void removeBaddie() {
		for(int i=0; i < baddies1.size(); i++ ) {
			baddies1.get(i).dropKeg();
		}
		for(int i=0; i < baddies2.size(); i++ ) {
			baddies2.get(i).dropKeg();
		}
		for(int i=0; i < baddies3.size(); i++ ) {
			baddies3.get(i).dropKeg();
		}
	}
	
	public void onBlow() {
    	for(int i=0; i < powerups.size(); i++ ) { 
    		if( powerups.get(i).getStatus() == Powerup.STATUS_PICKED_UP 
    		 && powerups.get(i).getType() == Powerup.TYPE_BLOW ) {
    			for(int j=0; j < kegs.size(); j++ ) {
    				if(!kegs.get(j).pickedUp())
    					kegs.get(j).setX(BASE_WIDTH/2.0f);
    			}
	    		numBlows--;
	    		powerups.get(i).playSound();
	    		powerups.remove(i);
	    		break;
    		}
    	}
	}
	
	public void onShake() {
    	for(int i=0; i < powerups.size(); i++ ) { 
    		if( powerups.get(i).getStatus() == Powerup.STATUS_PICKED_UP 
    		 && powerups.get(i).getType() == Powerup.TYPE_SHAKE ) {
    			for(int j=0; j < kegs.size(); j++ ) {
    				if(kegs.get(i).pickedUp()) {
    					kegs.get(j).pickUp(false);
    					//removeBaddie();
    				}
    			}
	    		numShakes--;
	    		powerups.get(i).playSound();
	    		powerups.remove(i);
	    		break;
    		}
    	}
	}

}
    