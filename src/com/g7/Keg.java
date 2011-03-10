package com.g7;

public class Keg {
    int _row;
    float _x, _y;
	boolean _pickedUp;
	boolean _removed;
	
	public Keg( int row, float x, float y ) {
		_row = row;
		_x = x;
		_y = y;
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
	
	public int getRow() {
		return _row;
	}
	
	//If the keg is moved offscreen
	public void remove(boolean remove) {
		_removed = remove;
	}
	
	//If the keg is moved offscreen
	public boolean removed() {
		return _removed;
	}
	
	public boolean pickedUp() {
		return _pickedUp;
	}
	
	public void pickUp(boolean kegPicked) {
		_pickedUp = kegPicked;
	}

}
