package com.g7;

public abstract class Powerup {
	public final static int TYPE_SHAKE = 0;
	public final static int TYPE_BLOW = 1;
	
	public final static int STATUS_INACTIVE = 0;
	public final static int STATUS_DROPPED = 1;
	public final static int STATUS_PICKED_UP = 2;

	private int _type;
	private int _status;
	private float _x, _y;
	
	private boolean _active;
	
	public Powerup(float type, float x, float y) {
	/*	if( type < 0.5f ) {
			_type = TYPE_SHAKE;
		} else {
			_type = TYPE_BLOW;
		}*/
		_x = x;
		_y = y;
		_status = STATUS_DROPPED;
	}
	
	/*public void setType(float type) {
		if( type < 0.5f ) {
			_type = TYPE_SHAKE;
		} else {
			_type = TYPE_BLOW;
		}
	}
	
	public int getType() {
		return _type;
	}*/
	public abstract int getType();
	public abstract int getColor();
	
	public int getStatus() {
		return _status;
	}
	
	public void setStatus(int status) {
		_status = status;
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
