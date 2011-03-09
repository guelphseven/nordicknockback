package com.g7;

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
    private boolean _hasKeg = false;
    private Keg _keg;
	
	public Baddie( float x, float y, float speed ) {
		_x = x;
		_y = y;
        _speed = speed;
        mSRectangle = new Rect(0,0,0,0);
        mFrameTimer = 0;
        mCurrentFrame = 0;
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
    
    public boolean hasKeg() {
    	return _hasKeg;
    }
    
    public Keg getKeg() {
    	return _keg;
    }
    
    public void pickupKeg(Keg keg) {
    	_keg = keg;
    	_hasKeg = true;
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

    public void Update(long GameTime) {
        if(GameTime > mFrameTimer + mFPS ) {
            mFrameTimer = GameTime;
            mCurrentFrame +=1;
 
            if(mCurrentFrame >= mNoOfFrames) {
                mCurrentFrame = 0;
            }
        }
 
        mSRectangle.left = mCurrentFrame * mSpriteWidth;
        mSRectangle.right = mSRectangle.left + mSpriteWidth;
    }
	 
    public void draw(Canvas canvas) {
        Rect dest = new Rect((int)getX(), (int)getY(), (int)getX() + mSpriteWidth, (int)getY() + mSpriteHeight);
 
        canvas.drawBitmap(mAnimation, mSRectangle, dest, null);
    }

}
