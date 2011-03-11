package com.g7;

import android.content.Context;
import android.graphics.Color;

public class ShakePowerup extends Powerup {
    Sound _sound;
	
	public ShakePowerup(Context context, float x, float y) {
		super(TYPE_SHAKE, x, y);
		_sound = new Sound(context);
	}
	
	public int getType() {
		return TYPE_SHAKE;
	}
	
	public int getColor() {
		return Color.DKGRAY;
	}
	
	public void playSound() {
		_sound.playSound(R.raw.quake);
	}
	
	

}
