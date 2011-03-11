package com.g7;

import android.graphics.Canvas;
import android.graphics.Paint;

public class TextDisplay {
	
	private float _x, _y;
	private int _prntCtr = 0;
	private String _text;

	public TextDisplay(int prntCtr, String text, float x, float y) {
		_x = x;
		_y = y;
		_text = text;
		_prntCtr = prntCtr;
	}
	
	public int getPrintCounter() {
		return _prntCtr;
	}
	
	public void setPrintCounter(int prntCtr) {
		_prntCtr = prntCtr;
	}
	
	public void setText(String text) {
		_text = text;
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
	
	public void draw(Canvas canvas, Paint paint) {
		canvas.drawText(_text, getX(), getY(), paint);
	}

}
