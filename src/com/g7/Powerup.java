package com.g7;

public class Powerup {
	public final static int TYPE_SHAKE = 0;
	public final static int TYPE_BLOW = 1;
	
	private int _type;
	private float _x, _y;
	private boolean _active;
	
	public Powerup(float type, float x, float y) {
		if( type < 0.5f ) {
			_type = TYPE_SHAKE;
		} else {
			_type = TYPE_BLOW;
		}
		_x = x;
		_y = y;
	}
	
	public void setType(float type) {
		if( type < 0.5f ) {
			_type = TYPE_SHAKE;
		} else {
			_type = TYPE_BLOW;
		}
	}
	
	public int getType() {
		return _type;
	}
	
	public boolean getActive() {
		return _active;
	}
	
	public void setActive(boolean active) {
		_active = active;
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
