package com.g7;

import android.content.Context;
import android.graphics.Color;

public class BlowPowerup extends Powerup {
	private Sound _sound;
	
	public BlowPowerup(Context context, float x, float y) {
		super(Powerup.TYPE_BLOW, x, y);
		_sound = new Sound(context);
	}
	
	public int getType() {
		return TYPE_BLOW;
	}
	
	public int getColor() {
		return Color.CYAN;
	}
	
	public void playSound() {
		_sound.playSound(R.raw.windpowerup);
	}

}
