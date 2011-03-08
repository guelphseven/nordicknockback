package com.g7;

import android.graphics.*;

public class Baddie {
	private float _x, _y;
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
	
	public Baddie( float startX, float startY ) {
		_x = startX;
		_y = startY;
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
