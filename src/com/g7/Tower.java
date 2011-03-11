package com.g7;

import java.math.*;

import android.content.Context;
import android.media.MediaPlayer;

public class Tower implements MediaPlayer.OnCompletionListener {
	private float _x, _y, _y0, _x0, _targetX, _targetY, _fireX, _fireY, _fireRadius;
	private boolean _fire = false;
	private Baddie _baddieTarget;
	private int _row;
	
	private long _fireInterval;
	private long _lastFireTime = 0;
	
	private Sound _sound;
	private boolean _soundPlaying;
	
	public Tower( Context context, float startX, float startY, float fireRadius, int towerRow, long fireInterval ) {
		_x = startX;
		_y = startY;
		_fireX = _x;
		_fireY = _y;
		_fireRadius = fireRadius;
		_row = towerRow;
		_fireInterval = fireInterval;
		_sound = new Sound(context);
		//_sound.addListener(this);
	}
	
	public void onCompletion(MediaPlayer mp) {
		_soundPlaying = false;
	}
	
	public void playSound() {
		//if( !_soundPlaying ) {
		//	_soundPlaying = true;
			_sound.playSound(R.raw.rockthrow);
		//}
	}
	
	public long lastFireTime() {
		return _lastFireTime;
	}
	
	public void setLastFireTime(long time) {
		_lastFireTime = time;
	}
	
	public long getFireInterval() {
		return _fireInterval;
	}
	
	public int getRow() {
		return _row;
	}
	
	public void setBaddie( Baddie target ) {
		_baddieTarget = target;
	}
	
	public Baddie getBaddie() {
		return _baddieTarget;
	}
	
	public void setTarget(float x, float y) {
		float angle = (float)(Math.atan2(_fireY - y, _fireX - x));
				
		_targetX = -(float)Math.cos(angle) * 3.0f;
		_targetY = -(float)Math.sin(angle) * 3.0f;
	}
	
	public float getTargetX() {
		return _targetX;
	}
	
	public float getTargetY() {
		return _targetY;
	}
	
	public void setFiring(boolean firing) {
		_fire = firing;
	}
	
	public boolean firing() {
		return _fire;
	}
	
	public float getFireRadius() {
		return _fireRadius;
	}
	
	public void setFireX( float x ) {
		_fireX = x;
	}
	
	public void setFireY( float y ) {
		_fireY = y;
	}
	
	public float fireX() {
		return _fireX;
	}
	
	public float fireY() {
		return _fireY;
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

}
