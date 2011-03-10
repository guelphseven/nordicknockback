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

class DrawableView extends View implements BlowListener, ShakeListener {
    	Context mContext;
        Bitmap backgroundImg;
        Bitmap icon;
        Paint paint;
        Rect widthHeight, iconWH;
        
        boolean addTower;
        int fps =0, frameCount = 0, gold = 20;
        long lastFrameTime = 0, lastBaddieTime = 0, lastTouchTime = 0;
        int level = 1 ;
        int numTowers = 0, numBaddies = 1, numKegs = 3;//avoid divide by 0, monsters freq. incr. by divide
        int numShakes = 0, numBlows = 0;
        
        Vector<Baddie> baddies1 = new Vector<Baddie>();
        Vector<Baddie> baddies2 = new Vector<Baddie>();
        Vector<Baddie> baddies3 = new Vector<Baddie>();
        
        Tower[] towers = new Tower[25];
        Vector<Keg> kegs = new Vector<Keg>();
        
        Vector<Powerup> powerups = new Vector<Powerup>();
        //Powerup powerup;
        Random random;
        Bitmap sprite;
        
        BlowDetect blowDetector;
        Shake shakeDetector;
        
    	public DrawableView(Context context) {
    		super(context);
    		mContext = context;
            backgroundImg = BitmapFactory.decodeResource(getResources(), R.drawable.castle_sunset);
            icon = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
            sprite = BitmapFactory.decodeResource(getResources(), R.drawable.sprite_viking);
            
            paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(5.0f);
            paint.setTextSize(10.0f);
            
            widthHeight = new Rect(0,0,backgroundImg.getWidth(),backgroundImg.getHeight());
            random = new Random(System.currentTimeMillis());
            
            kegs.add(new Keg(1, 240.0f, 125.0f));
            kegs.add(new Keg(2, 240.0f, 200.0f));
            kegs.add(new Keg(3, 240.0f, 275.0f));

            blowDetector = new BlowDetect(this);
            shakeDetector = Shake.getShake(context, this);
            shakeDetector.setThreshold(800);
    	}
    	
        int towers1PlacedLeft, towers2PlacedLeft, towers3PlacedLeft,
        towers1PlacedRight, towers2PlacedRight, towers3PlacedRight;
        
        int blowPrntCtr = 0, shakePrntCtr = 0, lvlPrtCtr = 0;

    	protected void onDraw(Canvas canvas) {
    		canvas.drawBitmap(backgroundImg, widthHeight, widthHeight, null);	
    		if( addTower ) {
    			paint.setColor(Color.GREEN);
    		}
        	canvas.drawLines(TowerPlacements.linesHorizontal, paint);
        	paint.setColor(Color.WHITE);
        	canvas.drawLines(TowerPlacements.linesVertical, paint);
        	
        	if( numKegs > 0 ) {
	        	
        		paint.setColor(Color.GREEN);
        		canvas.drawCircle( 420.0f, 25.0f, 30.0f, paint);
        		paint.setColor(Color.WHITE);
        		
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
	        	paint.setColor(Color.RED);
	        	for(int i=0; i < kegs.size(); i++ ) {
	        		if( !kegs.get(i).removed() ) {
	        			canvas.drawCircle(kegs.get(i).getX(), kegs.get(i).getY(), 7.5f, paint);
	        		}
	        	}
	        	paint.setColor(Color.WHITE);
    		
	        	//Set towers to fire and begin animating
            	buildFireList();
            	fireTowers(canvas);

            	displayTextStatus(canvas, paint);
            	displayKegStatus(canvas, paint);
 
            } else {
            	canvas.drawText("fps: "+fps, 25.0f, 25.0f, paint);
            	canvas.drawText("gold:"+gold, 75.0f, 25.0f, paint);
            	canvas.drawText("YOU LOSE!", 150.0f, 100.0f, paint);
            }
    		invalidate();
    		fps();
    	}
    	
    	
        @Override
        public boolean onTouchEvent(MotionEvent event) {
        	
    		if ((System.currentTimeMillis() - lastTouchTime) > 500) {
    			float x = event.getX();
    			float y = event.getY();
    		
    			if( !addTower && checkCollision(x, 420.0f, y, 30.0f, 30.0f, 30.0f) ) {
    				addTower = true;
    			} else if( !addTower ) {
    				checkPickupPowerup(x,y);
    			} else if( addTower ) {
	    			if( y <= 150.0f ) { 
	    				addTower( x, 125.0f, 150.0f, 1, 2000);
	    			} else if( y <= 225.0f ) {
	    				addTower( x, 200.0f, 150.0f, 2, 2000);
	    			} else {
	    				addTower( x, 275.0f, 150.0f, 3, 2000);
	    			}
    				addTower = false;
    			}

    			lastTouchTime = System.currentTimeMillis();
    			
    			return true;
    		} else {
    			return false;
    		}
        }
    	
    	private void drawPowerups(Canvas canvas, Paint paint) {
    		for( int i = 0; i < powerups.size(); i++ ) {
    			if( powerups.get(i).getStatus() == Powerup.STATUS_DROPPED ) {
    				paint.setColor(powerups.get(i).getColor());
    				canvas.drawCircle(powerups.get(i).getX(), powerups.get(i).getY(), 7.5f, paint);
    				paint.setColor(Color.WHITE);
    			}
    		}
    	}
    	
    	private void drawPowerupsStatus(Canvas canvas, Paint paint ) {
    		canvas.drawText(" x " + numBlows, 32.0f, 75.0f, paint );
			canvas.drawText(" x " + numShakes, 32.0f, 50.0f, paint );
			
    		paint.setColor(Color.DKGRAY);
			canvas.drawCircle( 25.0f, 50.0f, 7.5f, paint);
			
			paint.setColor(Color.CYAN);
			canvas.drawCircle( 25.0f, 75.0f, 7.5f, paint );
			paint.setColor(Color.WHITE);
    	}
    	
    	private void displayKegStatus(Canvas canvas, Paint paint) {
    		paint.setColor(Color.BLACK);
    		for(int i = 0; i < numKegs; i++ ) {
    			canvas.drawText("keg", 200.0f + (i*25.0f), 25.0f, paint);
    		}
	    	canvas.drawText("baddies:"+numBaddies, 300.0f, 25.0f, paint);
    		paint.setColor(Color.WHITE);
    	}
    	
    	private void displayTextStatus(Canvas canvas, Paint paint) {
	    	canvas.drawText("fps: "+fps, 25.0f, 25.0f, paint);
	    	canvas.drawText("gold: "+gold, 75.0f, 25.0f, paint);
	    	canvas.drawText("level: "+level, 125.0f, 25.0f, paint);
	    	if( numTowers == 24 ) {
	        	canvas.drawText("Tower limit reached!", 150.0f, 125.0f, paint);
	    	}
	    	if( gold < 5 && addTower ) {
	    		canvas.drawText("Not enough gold!", 175.0f, 150.0f, paint);
	    	}
			if( blowPrntCtr < 120 && numBlows > 0 ) {
    			paint.setTextSize(25.0f);
				canvas.drawText("BLOW INTO THE MIC!", 130.0f, 150.0f, paint);
				blowPrntCtr++;
	    		paint.setTextSize(10.0f);
			} else if( shakePrntCtr < 120 && numShakes > 0 ) {
    			paint.setTextSize(25.0f);
				canvas.drawText("SHAKE THE PHONE!", 140.0f, 200.0f, paint);
				shakePrntCtr++;
   	    		paint.setTextSize(10.0f);
			}
	    	if( lvlPrtCtr < 120 ) {
	    		paint.setTextSize(25.0f);
	    		canvas.drawText("LEVEL "+level, 200.0f, 125.0f, paint);
	    		lvlPrtCtr++;
	    		paint.setTextSize(10.0f);
	    	}
    	}
        
        private void checkPickupPowerup(float x, float y) {
			for( int i=0; i < powerups.size(); i++ ) {
				if( powerups.get(i).getStatus() == Powerup.STATUS_DROPPED 
				 && checkCollision( x, powerups.get(i).getX(), y, powerups.get(i).getY(), 50.0f, 10.0f) ) {
					powerups.get(i).setStatus(Powerup.STATUS_PICKED_UP);
					if( powerups.get(i).getType() == Powerup.TYPE_BLOW ) {
						blowDetector.begin();
			    		numBlows++;
			    		blowPrntCtr = 0;
					} else {
						numShakes++;
						shakePrntCtr = 0;
					}
					break;
				}
			}
        }
        
    	private void addTower(float x, float y, float fireRadius, int row, long fireInterval) {
    		if(numTowers < 24) {
	    		if( gold >= 5 ) {
		    		towers[numTowers] = new Tower(x, y, fireRadius, row, fireInterval);
		    		numTowers++;
		    		gold -= 5;
	    		}
    		}
    	}
    	
    	int direction1 = 1, direction2 = 1, direction3 = 1;
    	private void changeLevel() {
			float randDir = random.nextInt();
			if( randDir > 0.5 ) {
				direction1 = -1;
			}
			randDir = random.nextInt();
			if( randDir > 0.5 ) {
				direction2 = -1;
			}
			randDir = random.nextInt();
			if( randDir > 0.5 ) {
				direction3 = -1;
			}
			level++;
			lvlPrtCtr = 0;
    	}
    	
    	private final static float BASE_SPEED = 0.75f;
    	
    	private void addBaddies(int level) {
    		if( numBaddies < (level) * 10) {
	    		if ((System.currentTimeMillis() - lastBaddieTime) > (3000 - (numBaddies*10) ) ) {
	            	float y = 0.0f;
	    			float x = 0.0f;
	    			
	    			float rand = random.nextFloat();
	    			float lvlSpeed = (float)level/10.0f;
	    			boolean added = false;

	        		if( rand <= 0.3f ) {
	        			if(!kegs.get(0).pickedUp()) {
	        				y = 125.0f;
	        				baddies1.add( new Baddie( sprite, (direction1 == -1) ? 480.0f:x, y, (BASE_SPEED+lvlSpeed) * direction1 ));
	        				added = true;
	        			}
	        		}
	        		if( rand <= 0.6f && !added  ) {
	        			if(!kegs.get(1).pickedUp()) {
	        				y = 200.0f;
	        				baddies2.add( new Baddie( sprite, (direction2 == -1) ? 480.0f:x, y, (BASE_SPEED+lvlSpeed) * direction2));
	        				added = true;
	        			}
	        		} 
	        		if( rand <= 0.9f && !added ) {
	        			if(!kegs.get(2).pickedUp()) {
	        				y = 275.0f;
	        				baddies3.add( new Baddie( sprite, (direction3 == -1) ? 480.0f:x, y, (BASE_SPEED+lvlSpeed) * direction3 ) );
	        				added = true;
	        			}
	        		}
	        		
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
    				if( !kegs.get(i).pickedUp() && i == 0 ) {
        				y = 125.0f;
        				baddies1.add( new Baddie( sprite, (direction1 == -1) ? 480.0f:x, y, (BASE_SPEED+lvlSpeed) * direction1 ) );
        				break;
    				} else if( !kegs.get(i).pickedUp() && i == 1 ) {
        				y = 200.0f;
        				baddies2.add( new Baddie( sprite, (direction2 == -1) ? 480.0f:x, y, (BASE_SPEED+lvlSpeed) * direction2 ) );
        				break;
    				} else if( !kegs.get(i).pickedUp() && i == 2 ) {
        				y = 275.0f;
        				baddies3.add( new Baddie( sprite, (direction3 == -1) ? 480.0f:x, y, (BASE_SPEED+lvlSpeed) * direction3 ) );
        				break;
    				}
    			}
    		}
    	}
    	
    	private void drawBaddies(Canvas canvas, Vector<Baddie> baddies, int row) {
    		for(int i=0; i < baddies.size(); i++ ) {
    			Baddie baddie = baddies.get(i);
    			if( !baddie.isDead() ) {
    				baddie.draw(canvas);
    				baddie.Update(System.currentTimeMillis());
	    			baddie.setX( baddie.getX() + baddie.getSpeed() );
	    			
    				if( baddie.hasKeg()) {
    					moveKeg(baddie);
    				}
    				
	    			if( baddie.getX() > 480.0f || baddie.getX() < 0.0f ) {
	    				if( baddie.hasKeg() ) {
	    					baddie.getKeg().remove(true);
	    					baddie.dropKeg();
	    					numKegs--;
	    				}
	    				baddies.remove(baddie);
	    				continue;
	    			}
	    			
	    			if( baddie.getHealth() <= 0) {
	    				baddie.setDead(true);
	    				if( baddie.hasKeg() ) {
	    					dropKeg(baddie);
	    				} else {
	    					//Based on random
	    					dropPowerup(baddie);
	    				}
	    				baddies.remove(baddie);
	    				gold++;
	    				continue;
	    			}
	    			
	    			for( int j = 0; j < kegs.size(); j++ ) {
	    				if( !kegs.get(j).pickedUp() && checkCollision( baddie, kegs.get(j) ) ) {
	    					pickupKeg(kegs.get(j),baddie);
	    				}
	    			}
    			}
    		}
    	}
    	
    	private void dropPowerup(Baddie baddie) {
    		float drop = random.nextFloat();
    		if( drop >= 0.5f ) {
    			float type = random.nextFloat();
    			if( type >= 0.5f ) {
    				powerups.add(new ShakePowerup(baddie.getX(), baddie.getY()));
    			} else {
    				powerups.add(new BlowPowerup(baddie.getX(), baddie.getY()));
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
    		for(int i=0; i< numTowers; i++) {
    			canvas.drawCircle(towers[i].getX(), towers[i].getY(), 20.0f, paint);
    		}
    	}
    	
    	private void fireAtBaddies(int i, Vector<Baddie> baddies) {
			for( int j = 0; j < baddies.size(); j++ ) {
				if( !towers[i].firing() ) {
					if((System.currentTimeMillis() - towers[i].lastFireTime()) > towers[i].getFireInterval() ) {
						if( checkCollision(towers[i], baddies.get(j)) ) {
							towers[i].setBaddie(baddies.get(j));
							towers[i].setFiring(true);
							towers[i].setLastFireTime(System.currentTimeMillis());
							break;
						}
					}
				}
			}
    	}
    	
    	private void buildFireList() {
    		for( int i = 0; i < numTowers; i++ ) {
    			if( towers[i].getRow() == 1 ) {
    				fireAtBaddies(i, baddies1);
    			} else if( towers[i].getRow() == 2 ) {
    				fireAtBaddies(i, baddies2);
    			} else {
    				fireAtBaddies(i, baddies3);
    			}
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
    		float radii = 10.0f + 5.0f;
    		
    		if( (distX * distX) + (distY * distY) < (radii * radii) ) {
    			tower.getBaddie().setHealth(tower.getBaddie().getHealth() - 1);
    			return true;
    		}
    			
    		return false;
    	}
    	
    	private void fireTowers(Canvas canvas) {
    		for(int i=0; i< numTowers; i++) {
				if( towers[i].firing()  ) {
					towers[i].setTarget(towers[i].getBaddie().getX(),towers[i].getBaddie().getY());
					
					towers[i].setFireX(towers[i].fireX() + towers[i].getTargetX());
					towers[i].setFireY(towers[i].fireY() + towers[i].getTargetY());
					
					canvas.drawCircle(towers[i].fireX(), towers[i].fireY(), 5.0f, paint);
					
					if( towers[i].fireY() <= 0 || towers[i].fireY() >= 600 || 
						towers[i].fireX() <= 0 || towers[i].fireX() >= 800 || checkCollision(towers[i]) ) {
						towers[i].setFiring(false);
						towers[i].setFireX(towers[i].getX());
						towers[i].setFireY(towers[i].getY());
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
    	
    	public void onBlow() {
	    	for(int i=0; i < powerups.size(); i++ ) { 
	    		if( powerups.get(i).getStatus() == Powerup.STATUS_PICKED_UP 
	    		 && powerups.get(i).getType() == Powerup.TYPE_BLOW ) {
		    		baddies1.removeAllElements();
		    		baddies2.removeAllElements();
		    		baddies3.removeAllElements();
		    		baddies1.clear();
		    		baddies2.clear();
		    		baddies3.clear();
		    		blowDetector.kill();
		    		numBlows--;
		    		powerups.remove(i);
		    		break;
	    		}
	    	}
    	}
    	
    	public void onShake() {
	    	for(int i=0; i < powerups.size(); i++ ) { 
	    		if( powerups.get(i).getStatus() == Powerup.STATUS_PICKED_UP 
	    		 && powerups.get(i).getType() == Powerup.TYPE_SHAKE ) {
		    		baddies1.removeAllElements();
		    		baddies2.removeAllElements();
		    		baddies3.removeAllElements();
		    		baddies1.clear();
		    		baddies2.clear();
		    		baddies3.clear();
		    		numShakes--;
		    		powerups.remove(i);
		    		break;
	    		}
	    	}
    	}

    }
    