/* Taken from http://android.themind-lab.com/post/2010/05/14/Dont-shake-my-phone!-How-to-detect-shake-motion-on-Android-phone.aspx
*/
package com.g7;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.Context;

public class Shake implements SensorEventListener{
	private SensorManager sensorMgr;
	private ShakeListener s;
	private long lastUpdate = -1;
	private float x, y, z;
	private float last_x, last_y, last_z;
	private int threshold = 800;
	private static Shake shake;
	public TextView tv;

	private Shake(Context a,ShakeListener s){
		this.s = s;
		sensorMgr = (SensorManager) a.getSystemService(a.SENSOR_SERVICE);
		boolean accelSupported = sensorMgr.registerListener(this,sensorMgr.getDefaultSensor(SensorManager.SENSOR_ACCELEROMETER),SensorManager.SENSOR_DELAY_GAME);
		if (!accelSupported) {
			sensorMgr.unregisterListener(this);
		}
	}

	public static Shake getShake(Context a,ShakeListener s){
		if(shake == null){
			shake = new Shake(a,s);
		}
	//	else{
			//_s = s;
		//}
		return shake;

	}


	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// AUTO GENERATED METHOD STUB
	}

	public void setThreshold(int i){
		threshold = i;
	}

	public void onSensorChanged(SensorEvent event) {
		Sensor mySensor = event.sensor;
		if (mySensor.getType() == SensorManager.SENSOR_ACCELEROMETER) {
			long curTime = System.currentTimeMillis();
			long diffTime = curTime - lastUpdate;
			if(diffTime > 100) {
				lastUpdate = curTime;

				x = event.values[SensorManager.DATA_X];
				y = event.values[SensorManager.DATA_Y];
				z = event.values[SensorManager.DATA_Z];

				float speed = Math.abs(x+y+z - last_x - last_y - last_z) / diffTime * 10000;
				if(speed > threshold){
					s.onShake();
				}
				last_x = x;
				last_y = y;
				last_z = z;
			}
		}
	}
}
