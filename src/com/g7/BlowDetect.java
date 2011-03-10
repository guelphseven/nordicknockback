package com.g7;

import java.util.ArrayList;
import java.lang.Thread;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;


public class BlowDetect extends Thread{

	private boolean running=false;
	private ArrayList<BlowListener> listeners;
	private int rate=8000, bufferSize=4096;
	private AudioRecord recorder;
	short[] buffer;
		
	public BlowDetect(BlowListener firstListener)
	{
		listeners=new ArrayList<BlowListener>();
		if (firstListener!=null)
				this.addListener(firstListener);
		int[] possibleRates={1000,8000,11025,16000,22050,32000,48000,44100};
		for (int i=0; i<8; i++)
		{
			try {
				rate=possibleRates[i];
				bufferSize=AudioRecord.getMinBufferSize(rate,
			        AudioFormat.CHANNEL_CONFIGURATION_MONO,
			        AudioFormat.ENCODING_PCM_16BIT);
				
				if (bufferSize>0) {

					recorder=new AudioRecord(AudioSource.MIC, rate,
										 AudioFormat.CHANNEL_CONFIGURATION_MONO,
										 AudioFormat.ENCODING_PCM_16BIT, bufferSize);
					
					buffer = new short[bufferSize];
					
					break;
				}

			} catch( Exception e ) {
				Log.v(""+Log.WARN, "Martin exception: " + e.getMessage() );
			}
		}
		
		this.start();
	}
	
	public void addListener(BlowListener toAdd)
	{
		listeners.add(toAdd);
	}
	
	public void removeListener(BlowListener toRemove)
	{
		listeners.remove(toRemove);
	}
	
	private void notifyListeners()
	{
		for (BlowListener l : listeners)
		{
			l.onBlow();
		}
	}
	
	public void kill()
	{
		running=false;
	}
	
	public void begin() {
		running = true;
		try{
			recorder.startRecording();
		}catch(Exception e){}
	}
	
	public void run()
	{	
		while(true)
		{
			if (running) {
				
				if (checkWave(buffer, bufferSize))
				{
					this.notifyListeners();
					try{Thread.sleep(2000);}catch(Exception e){}
				}
			}
			try{Thread.sleep(200);}catch(Exception e){}
		}
	}
	
	public void killForever() throws Throwable {
		try {
			recorder.stop();
			this.finalize();
		} catch( Exception e ) {
			
		}
	}
	
	boolean checkWave(short[] buffer, int bufferSize)
	{
		int count=0;
		int last=buffer[0];
		int numRead=recorder.read(buffer, 0, bufferSize);
		int threshold = numRead/4;
		for (int i=0; i<bufferSize; i++)
		{
			if (buffer[i]-last>=10000 || buffer[i]-last<=-10000)
				count++;
			if (count>threshold)
				return(true);
			last=buffer[i];
		}
		return false;
	}
	
	
	
}
