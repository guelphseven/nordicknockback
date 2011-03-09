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

class DrawableView extends View {
    	Context mContext;
        Bitmap backgroundImg;
        Bitmap icon;
        Paint paint;
        Rect widthHeight, iconWH;
        
        int fps =0, frameCount = 0, gold = 5;
        long lastFrameTime = 0, lastBaddieTime = 0, lastTouchTime = 0;
        
        int numTowers = 0, numBaddies = 1, numKegs = 3;//avoid divide by 0, monsters freq. incr. by divide
        
        Vector<Baddie> baddies1 = new Vector<Baddie>();
        Vector<Baddie> baddies2 = new Vector<Baddie>();
        Vector<Baddie> baddies3 = new Vector<Baddie>();
        
        Tower[] towers = new Tower[25];
        Vector<Keg> kegs = new Vector<Keg>();
        Random random;
        
        float[] linesHorizontal = {
        		0.0f, 150.0f, 500.0f, 150.0f,
        		0.0f, 225.0f, 500.0f, 225.0f,
        		0.0f, 300.0f, 500.0f, 300.0f,
        };
        
        float[] linesVertical = {
        		230.0f, 320.0f, 230.0f, 150.0f,
        		250.0f, 320.0f, 250.0f, 150.0f
        };
        
        float[] towerSpots1 = {
        		175.0f, 150.0f, 225.0f, 150.0f,
        		255.0f, 150.0f, 305.0f, 150.0f,
        };
        
        float[] towerSpots2 = {
        		175.0f, 225.0f, 225.0f, 225.0f,
        		255.0f, 225.0f, 305.0f, 225.0f,
        };
        
        float[] towerSpots3 = {
        		175.0f, 300.0f, 225.0f, 300.0f,
        		255.0f, 300.0f, 305.0f, 300.0f,
        };
        
    	public DrawableView(Context context) {
    		super(context);
    		mContext = context;
            backgroundImg = BitmapFactory.decodeResource(getResources(), R.drawable.castle_sunset);
            icon = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
            paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(5.0f);
            paint.setTextSize(10.0f);
            widthHeight = new Rect(0,0,backgroundImg.getWidth(),backgroundImg.getHeight());
            random = new Random(System.currentTimeMillis());
            kegs.add(new Keg(1, 240.0f, 150.0f));
            kegs.add(new Keg(2, 240.0f, 225.0f));
            kegs.add(new Keg(3, 240.0f, 300.0f));
    	}

    	protected void onDraw(Canvas canvas) {
    		canvas.drawBitmap(backgroundImg, widthHeight, widthHeight, null);	
        	canvas.drawLines(linesHorizontal, paint);
        	canvas.drawLines(linesVertical, paint);
        	
        	if( numKegs > 0 ) {
	        	
	        	//Tower spots
	        	paint.setColor(Color.GREEN);
	        	canvas.drawLines(towerSpots1, paint);
	        	canvas.drawLines(towerSpots2, paint);
	        	canvas.drawLines(towerSpots3, paint);
	        	paint.setColor(Color.WHITE);
	        	
	        	drawTowers(canvas);
            
	        	//Add and draw the three rows of baddies
            	addBaddies();
            	drawBaddies(canvas, baddies1, 1);
            	drawBaddies(canvas, baddies2, 2);
            	drawBaddies(canvas, baddies3, 3);

        		//Kegs
	        	paint.setColor(Color.RED);
	        	for(int i=0; i < kegs.size(); i++ ) {
	        		canvas.drawCircle(kegs.get(i).getX(), kegs.get(i).getY(), 7.5f, paint);
	        	}
	        	paint.setColor(Color.WHITE);
    		
	        	//Set towers to fire and begin animating
            	buildFireList();
            	fireTowers(canvas);
            	
            	canvas.drawText("fps: "+fps, 25.0f, 25.0f, paint);
            	canvas.drawText("gold: "+gold, 75.0f, 25.0f, paint);
            	canvas.drawText("gold: "+gold, 75.0f, 25.0f, paint);
            	displayKegStatus(canvas);
            } else {
            	canvas.drawText("fps: "+fps, 25.0f, 25.0f, paint);
            	canvas.drawText("gold:"+gold, 75.0f, 25.0f, paint);
            	canvas.drawText("YOU LOSE!", 150.0f, 100.0f, paint);
            }
    		invalidate();
    		fps();
    	}
    	
    	private void displayKegStatus(Canvas canvas) {
    		for(int i = 0; i < kegs.size(); i++ ) {
    			canvas.drawText("keg", 200.0f + (i*25.0f), 25.0f, paint);
    		}
    	}
    	
    	private void checkPlacement(float[] towerSpots, float x, float y, int row ) {
    		for( int i=0; i < towerSpots.length - 2; i+=2 ) {
    			if( x >= towerSpots[i] && x <= towerSpots[i+2] ) {
    				addTower((towerSpots[i]+towerSpots[i+2])/2, y, 150.0f, row, 2000);
    			}
    		}
    	}
    	
        @Override
        public boolean onTouchEvent(MotionEvent event) {
        	
    		if ((System.currentTimeMillis() - lastTouchTime) > 500) {
    			float x = event.getX();
    			float y = event.getY();
    			
    			if( y <= 150.0f ) {
    				checkPlacement( towerSpots1, x, 125.0f, 1);
    			} else if( y <= 225.0f ) {
    				checkPlacement( towerSpots2, x, 200.0f, 2);
    			} else if( y <= 300.0f ) {
    				checkPlacement( towerSpots3, x, 275.0f, 3);
    			}
    			lastTouchTime = System.currentTimeMillis();
    			
    			return true;
    		} else {
    			return false;
    		}
        }
        
    	private void addTower(float x, float y, float fireRadius, int row, long fireInterval) {
    		if( gold > 2 ) {
	    		towers[numTowers] = new Tower(x, y, fireRadius, row, fireInterval);
	    		numTowers++;
	    		if(numTowers >= 25) {
	    			numTowers = 24;
	    		}
	    		gold-=2;
    		}
    	}
    	
    	private void addBaddies() {
    		if ((System.currentTimeMillis() - lastBaddieTime) > (2500 - (numBaddies*10) ) ) {
    			float rand = random.nextFloat();

            	float y = 0.0f;
        		if( rand <= 0.3f) {
        			y = 150.0f;
                	baddies1.add( new Baddie( 0.0f, y, 1.5f ));
        		} else if( rand <= 0.6f ) {
        			y = 225.0f;
                	baddies2.add( new Baddie( 480.0f, y, -2.0f));
        		} else if( rand <= 0.9f ) {
        			y = 300.0f;
                	baddies3.add( new Baddie( 480.0f, y, -1.5f ) );
        		} else {
        			y = 300.0f;
                	baddies3.add( new Baddie( 0.0f, y, 2.0f ));
        		}
        		
        		numBaddies++;
                lastBaddieTime = System.currentTimeMillis();
    		}
    	}
    	
    	private void drawBaddies(Canvas canvas, Vector<Baddie> baddies, int row) {
    		for(int i=0; i < baddies.size(); i++ ) {
    			Baddie baddie = baddies.get(i);
    			if( !baddie.isDead() ) {
	    			//canvas.drawBitmap(icon, baddie.getX(), baddie.getY(), null);
	    			canvas.drawCircle(baddie.getX(), baddie.getY(), 10.0f, paint);
	    			baddie.setX( baddie.getX() + baddie.getSpeed() );
	    			
    				if( baddie.hasKeg()) {
    					moveKeg(baddie);
    				}
    				
	    			if( baddie.getX() > 480.0f || baddie.getX() < 0.0f ) {
	    				gold -= 1;
	    				if( baddie.hasKeg() ) {
	    					kegs.remove(baddie.getKeg());
	    				}
	    				baddies.remove(baddie);
	    			}
	    			
	    			if( baddie.getHealth() <= 0) {
	    				baddie.setDead(true);
	    				if( baddie.hasKeg() ) {
	    					dropKeg(baddie);
	    				}
	    				baddies.remove(baddie);
	    				gold++;
	    			}
	    			
	    			for( int j = 0; j < kegs.size(); j++ ) {
	    				if( checkCollision( baddie, kegs.get(j) ) ) {
	    					baddie.pickupKeg(kegs.get(j));
	    				}
	    			}
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
    		}
    	}
    	
    /*	private void pickupKeg(int row, Baddie baddie) {
			if( row == 1 && !keg1Picked ) {
				baddie.pickupKeg(keg);
				keg1Picked = true;
			}
			if( row == 2 && !keg2Picked ) {
				baddie.pickupKeg(row);
				keg2Picked = true;
			}
			if( row == 3 && !keg3Picked ) {
				baddie.pickupKeg(row);
				keg3Picked = true;
			}
    	}*/
    	
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
							//towers[i].setBaddie(j);
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
    	private boolean checkCollision(Baddie baddie, Keg keg) {
    		float distX = baddie.getX() - keg.getX();
    		float distY = baddie.getY() - keg.getY();
    		float radii = 10.0f + 10.0f;
    		
    		if( (distX * distX) + (distY * distY) < (radii * radii) ) {
    			return true;
    		}
    			
    		return false;
    	}
    	
    	private boolean checkCollision(Tower tower, Baddie baddie) {
    		float distX = tower.getX() - baddie.getX();
    		float distY = tower.getY() - baddie.getY();
    		float radii = tower.getFireRadius() + 50.0f;
    		
    		if( (distX * distX) + (distY * distY) < (radii * radii) ) {
    			return true;
    		}
    			
    		return false;
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

    }
    