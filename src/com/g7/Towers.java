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
    
    private final static int MAX_SPRITES = 50;
    
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
        Bitmap icon;
        Paint paint;
        Rect widthHeight, iconWH;
        int fps =0, frameCount = 0;
        long lastFrameTime;
        
        float[] points = {
        		10.0f, 450.0f, 250.0f, 450.0f,
        		100.0f, 500.0f, 500.0f, 500.f,
        		200.0f, 550.0f, 300.0f, 550.f,
        		250.0f, 400.0f, 500.0f, 400.0f,
        };
        
    	public DrawableView(Context context) {
    		super(context);
    		mContext = context;
            backgroundImg = BitmapFactory.decodeResource(getResources(), R.drawable.castle_sunset);
            icon = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
            paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(5.0f);
            paint.setTextSize(10.0f);
            widthHeight = new Rect(0,0,backgroundImg.getWidth(),backgroundImg.getHeight());
           // iconWH = new Rect(0,0,backgroundImg.getWidth(),backgroundImg.getHeight());
    	}

    	protected void onDraw(Canvas canvas){
    		//canvas.scale(0.5f, 0.5f, 0.5f, 0.5f);
    		canvas.drawBitmap(backgroundImg, widthHeight, widthHeight, null);
    		for(int i=0; i < MAX_SPRITES; i++ ) {
    			canvas.drawBitmap(icon, i + 50.0f, i + 50.0f, null);
    		}

    		canvas.drawLines(points, paint);
            frameCount++;
            if ((System.currentTimeMillis() - lastFrameTime) > 1000)
            {
                fps = frameCount;
                frameCount = 0;
                lastFrameTime = System.currentTimeMillis();
            }
            canvas.drawText("fps:"+fps, 50.0f, 50.0f, paint);
    		invalidate();
    	}
    }
    
}