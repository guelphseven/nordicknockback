package com.g7;

import android.graphics.Color;

public class ShakePowerup extends Powerup {
	
	public ShakePowerup(float x, float y) {
		super(TYPE_SHAKE, x, y);
	}
	
	public int getType() {
		return TYPE_SHAKE;
	}
	
	public int getColor() {
		return Color.DKGRAY;
	}

}
