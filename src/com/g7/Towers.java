package com.g7;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.WindowManager.LayoutParams;
import android.view.*;
import android.content.pm.ActivityInfo;
import android.graphics.*;
import android.graphics.drawable.*;
import android.content.Context;

public class Towers extends Activity {
    LinearLayout mLinearLayout;
    long mLastTouchTime;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );

        setContentView(new DrawableView(this));
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        final long time = System.currentTimeMillis();
        if (event.getAction() == MotionEvent.ACTION_MOVE && time - mLastTouchTime < 30) {
                // Sleep so that the main thread doesn't get flooded with UI events.
                try {
                    Thread.sleep(32);
                } catch (InterruptedException e) {
                }
                //invalidate();
        }
        mLastTouchTime = time;

        return true;
    }
    
    class DrawableView extends View{
    	Context mContext;
        Bitmap backgroundImg; 
        Paint paint;
        Rect widthHeight;
        
    	public DrawableView(Context context) {
    		super(context);
    		mContext = context;
            backgroundImg = BitmapFactory.decodeResource(getResources(), R.drawable.castle_sunset);
            paint = new Paint();
            widthHeight = new Rect(0,0,backgroundImg.getWidth(),backgroundImg.getHeight());
    	}

    	protected void onDraw(Canvas canvas){
    		canvas.scale(0.5f, 0.5f, 0.5f, 0.5f);
    		canvas.drawBitmap(backgroundImg, widthHeight, widthHeight, paint);
    		invalidate();
    	}
    }
    
}