package com.g7;

public class Tower {
	private float _x, _y, _fireX, _fireY, _fireRadius;
	private boolean _fire = false;
	private Baddie _baddieTarget;
	
	public Tower( float startX, float startY, float fireRadius ) {
		_x = startX;
		_y = startY;
		_fireRadius = fireRadius;
	}
	
	public void setTarget(Baddie baddie) {
		_baddieTarget = baddie;
	}
	
	public Baddie getTarget() {
		return _baddieTarget;
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
