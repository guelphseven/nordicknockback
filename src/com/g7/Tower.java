package com.g7;

public class Tower {
	private float _x, _y, _fireX, _fireY;
	private boolean _fire = false;
	
	public Tower( float startX, float startY ) {
		_x = startX;
		_y = startY;
	}
	
	public void setFiring(boolean firing) {
		_fire = firing;
	}
	
	public boolean firing() {
		return _fire;
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
