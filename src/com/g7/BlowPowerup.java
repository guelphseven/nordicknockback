package com.g7;

import android.graphics.Color;

public class BlowPowerup extends Powerup {
	
	public BlowPowerup(float x, float y) {
		super(Powerup.TYPE_BLOW, x, y);
	}
	
	public int getType() {
		return TYPE_BLOW;
	}
	
	public int getColor() {
		return Color.CYAN;
	}

}
