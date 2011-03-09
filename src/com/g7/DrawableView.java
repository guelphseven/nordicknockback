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
        
        int fps =0, frameCount = 0, gold = 2;
        long lastFrameTime = 0, lastBaddieTime = 0, lastTouchTime = 0;
        
        int numTowers = 0;
        
        Vector<Baddie> baddies1 = new Vector<Baddie>();
        Vector<Baddie> baddies2 = new Vector<Baddie>();
        Vector<Baddie> baddies3 = new Vector<Baddie>();
        
        Tower[] towers = new Tower[25];
        
        Random random;
        
        float[] points = {
        		0.0f, 200.0f, 500.0f, 200.0f,
        		0.0f, 250.0f, 500.0f, 250.0f,
        		0.0f, 300.0f, 500.0f, 300.0f,
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
    	}

    	protected void onDraw(Canvas canvas){
    		//canvas.scale(0.5f, 0.5f, 0.5f, 0.5f);
    		canvas.drawBitmap(backgroundImg, widthHeight, widthHeight, null);	
        	canvas.drawLines(points, paint);
        	drawTowers(canvas);
        	
            if( gold > 0 ) {
            	addBaddies();
    		
            	drawBaddies(canvas, baddies1);
            	drawBaddies(canvas, baddies2);
            	drawBaddies(canvas, baddies3);
    		
            	buildFireList();
            	fireTowers(canvas);
            	
            	canvas.drawText("fps: "+fps, 25.0f, 25.0f, paint);
            	canvas.drawText("gold: "+gold, 75.0f, 25.0f, paint);
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
        	
    		if ((System.currentTimeMillis() - lastTouchTime) > 1000) {
    			float x = event.getX();
    			float y = event.getY();
        	
    			if( y < 200.0f ) {
    				addTower(x,180.0f,150.0f,1, 750);
    			} else if( y <= 250.0f) {
    				addTower(x,230.0f,150.0f,2, 750);
    			} else {
    				addTower(x,280.0f,150.0f,3, 750);
    			}
    			lastTouchTime = System.currentTimeMillis();
    			
    			return true;
    		} else {
    			return false;
    		}
        }
        
    	private void addTower(float x, float y, float fireRadius, int row, long fireInterval) {
    		if( gold > 0 ) {
	    		towers[numTowers] = new Tower(x, y, fireRadius, row, fireInterval);
	    		numTowers++;
	    		if(numTowers >= 25) {
	    			numTowers = 24;
	    		}
	    		gold--;
    		}
    	}
    	
    	private void addBaddies() {
    		if ((System.currentTimeMillis() - lastBaddieTime) > (5000/gold) ) {
    			float rand = random.nextFloat();

            	float y = 0.0f;
        		if( rand <= 0.3f) {
        			y = 200.0f;
                	baddies1.add( new Baddie( 0.0f, y, 1.0f ));
        		} else if( rand <= 0.6f ) {
        			y = 250.0f;
                	baddies2.add( new Baddie( 480.0f, y, -1.5f));
        		} else if( rand <= 0.9f ) {
        			y = 300.0f;
                	baddies3.add( new Baddie( 480.0f, y, -1.0f ) );
        		} else {
        			y = 300.0f;
                	baddies3.add( new Baddie( 0.0f, y, 1.5f ));
        		}
                lastBaddieTime = System.currentTimeMillis();
    		}
    	}
    	
    	private void drawBaddies(Canvas canvas, Vector<Baddie> baddies) {
    		for(int i=0; i < baddies.size(); i++ ) {
    			Baddie baddie = baddies.get(i);
    			if( !baddie.isDead() ) {
	    			//canvas.drawBitmap(icon, baddie.getX(), baddie.getY(), null);
	    			canvas.drawCircle(baddie.getX(), baddie.getY(), 10.0f, paint);
	    			baddie.setX( baddie.getX() + baddie.getSpeed() );
	    			if( baddie.getX() > 480.0f || baddie.getX() < 0.0f ) {
	    				gold -= 1;
	    				baddies.remove(baddie);
	    			}
	    			
	    			if( baddie.getHealth() <= 0) {
	    				baddie.setDead(true);
	    				baddies.remove(baddie);
	    				gold++;
	    			}
    			}
    		}
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
    