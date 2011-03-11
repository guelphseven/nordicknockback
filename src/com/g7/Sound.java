package com.g7;

import android.media.MediaPlayer;
import android.media.AudioManager;
import java.io.IOException;
import android.content.Context;

public class Sound{
	private Context c;
	private MediaPlayer.OnCompletionListener _additionalListener;

	/**
	*Easist just to pass in the Runnable class
	*/
	public Sound(Context c){
		this.c = c;
	}
	
	public void addListener(MediaPlayer.OnCompletionListener listener) {
		_additionalListener = listener;
	}

	/**
	* Just pass in the R.raw.* file you want to play
	*/
	public void playSound(int sound){
		MediaPlayer mp = MediaPlayer.create(c, sound);
		mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
			public void onCompletion(MediaPlayer mp){
				if( _additionalListener != null ) {
					_additionalListener.onCompletion(mp);
				}
				mp.release();
			}
		});
	   	mp.start();
	}
}