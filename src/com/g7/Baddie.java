package com.g7;

import android.content.Context;
import android.graphics.*;

public class Baddie {
	private float _x, _y, _speed;
    private Bitmap mAnimation;
    private int mXPos;
    private int mYPos;
    private Rect mSRectangle;
    
    private int mFPS;
    private int mNoOfFrames;
    private int mCurrentFrame;
    private long mFrameTimer;
    private int mSpriteHeight;
    private int mSpriteWidth;
    
    private boolean _dead = false;
    private int _health = 3;
   // private boolean _hasKeg = false;
    private Keg _keg;
    private Rect destRect;
    
    private Sound _deathSound;
	
	public Baddie( Context context, Bitmap bitmap, float x, float y, int health, float speed ) {
		_x = x;
		_y = y;
        _speed = speed;
        _health = health;
        mSRectangle = new Rect(0,0,0,0);
        destRect = new Rect(0,0,0,0);
        mFrameTimer = 0;
        mCurrentFrame = 0;
        init( bitmap, 32, 32, 10, 5 );
        _deathSound = new Sound(context);
	}
	 
    public void init(Bitmap theBitmap, int Height, int Width, int theFPS, int theFrameCount) {
        mAnimation = theBitmap;
        mSpriteHeight = Height;
        mSpriteWidth = Width;
        mSRectangle.top = 0;
        mSRectangle.bottom = mSpriteHeight;
        mSRectangle.left = 0;
        mSRectangle.right = mSpriteWidth;
        mFPS = 1000 /theFPS;
        mNoOfFrames = theFrameCount;
    }
    
    public void playSound() {
    	_deathSound.playSound(R.raw.enemy_death);
    }
    
    public boolean hasKeg() {
    	return (_keg != null);
    }
    
    public Keg getKeg() {
    	return _keg;
    }
    
    public void pickupKeg(Keg keg) {
    	_keg = keg;
    	//_hasKeg = true;
    }
    
    public void dropKeg() {
    	//_hasKeg = false;
    	_keg = null;
    }
    
    public void setHealth( int health ) {
    	_health = health;
    }
    
    public int getHealth() {
    	return _health;
    }
    
    public void setDead( boolean dead ) {
    	_dead = dead;
    }
    
    public boolean isDead() {
    	return _dead;
    }
	
	public void setX( float x ) {
		_x = x;
	}
	
	public void setY( float y ) {
		_y = y;
	}
	
	public float getX() {
		return _x;
	}
	
	public float getY() {
		return _y;
	}
	
	public float getSpeed() {
		return _speed;
	}
	
	boolean rewind;

    public void Update(long GameTime) {
        if(GameTime > mFrameTimer + mFPS ) {
            mFrameTimer = GameTime;
            if( !rewind ) {
            	mCurrentFrame ++;
            } else {
            	mCurrentFrame --;
            }
 
            if(mCurrentFrame >= mNoOfFrames - 1) {
            	rewind = true;
            }
            if( mCurrentFrame == 0 ) {
            	rewind = false;
            }
        }
 
        mSRectangle.left = mCurrentFrame * mSpriteWidth;
        mSRectangle.right = mSRectangle.left + mSpriteWidth;
    }
	 
    public void draw(Canvas canvas, Paint paint) {
    	destRect.set((int)getX(), (int)getY(), (int)getX() + mSpriteWidth, (int)getY() + mSpriteHeight);
      //  canvas.scale(2.0f, 2.0f, getX(), getY());
    	if( getSpeed() < 0 ) {
        	canvas.scale(-1,1,getX(),getY());
    	}
        canvas.drawBitmap(mAnimation, mSRectangle, destRect, paint);
        if( getSpeed() < 0 ) {
        	canvas.scale(-1,1,getX(),getY());
        }
       // canvas.scale(0.5f, 0.5f, getX(), getY());
    }

}
