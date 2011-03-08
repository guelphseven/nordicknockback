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

public class ActionDefense extends Activity {
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
    
    class DrawableView extends View {
    	Context mContext;
        Bitmap backgroundImg;
        Bitmap icon;
        Paint paint;
        Rect widthHeight, iconWH;
        
        int fps =0, frameCount = 0;
        long lastFrameTime, lastBaddieTime;
        
        int numBaddies = 0, numTowers = 0;
        Baddie[] baddies = new Baddie[25];
        Tower[] towers = new Tower[25];
        
        float[] points = {
        		0.0f, 250.0f, 500.0f, 250.0f,
        		0.0f, 300.0f, 500.0f, 300.0f,
        		0.0f, 350.0f, 500.0f, 350.0f,
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
            //tower = new Tower( 200.0f, 300.0f);
            
    	}

    	protected void onDraw(Canvas canvas){
    		//canvas.scale(0.5f, 0.5f, 0.5f, 0.5f);
    		canvas.drawBitmap(backgroundImg, widthHeight, widthHeight, null);	

    		addBaddies();
    		drawBaddies(canvas);
    		
    		canvas.drawLines(points, paint);
    		
    		drawTowers(canvas);
    		fireTowers(canvas);

            canvas.drawText("fps:"+fps, 50.0f, 50.0f, paint);
    		invalidate();
    		fps();
    	}
    	
        @Override
        public boolean onTouchEvent(MotionEvent event) {
        	float x = event.getX();
        	float y = event.getY();
        	
        	addTower(x,y);
        	
        	//tower.setFiring(true);
            return true;
        }
        
    	private void addTower(float x, float y) {
    		towers[numTowers] = new Tower(x, y);
    		numTowers++;
    		if(numTowers >= 25) {
    			numTowers = 24;
    		}
    	}
    	
    	private void addBaddies() {
    		if ((System.currentTimeMillis() - lastBaddieTime) > 5000) {
                if(numBaddies >= 25 ) {
                	numBaddies = 24;
                } else {
                	baddies[numBaddies] = new Baddie( 0.0f, 200.0f);
                }
                numBaddies++;
                lastBaddieTime = System.currentTimeMillis();
    		}
    	}
    	
    	private void drawBaddies(Canvas canvas) {
    		for(int i=0; i < numBaddies; i++ ) {
    			Baddie baddie = baddies[i];
    			canvas.drawBitmap(icon, baddie.getX(), baddie.getY(), null);
    			baddie.setX( baddie.getX() + 1.0f );
    		}
    	}
    	
    	private void drawTowers(Canvas canvas) {
    		for(int i=0; i< numTowers; i++) {
    			canvas.drawCircle(towers[i].getX(), towers[i].getY(), 20.0f, paint);
    		}
    	}
    	
    	private void fireTowers(Canvas canvas) {
    		for(int i=0; i< numTowers; i++) {
				if( towers[i].firing() ) {
					canvas.drawCircle(towers[i].fireX(), towers[i].fireY(), 5.0f, paint);
					towers[i].setFireX(towers[i].fireX()-1.0f);
					towers[i].setFireY(towers[i].fireY()-1.0f);
					
					if( towers[i].fireY() <= 0 ) {
						towers[i].setFiring(false);
						towers[i].setFireX(towers[i].getX());
						towers[i].setFireY(towers[i].getY());
					}
				}
    		}
    	}
    	
    	private void fps() {
            frameCount++;
            if ((System.currentTimeMillis() - lastFrameTime) > 1000) {
                fps = frameCount;
                frameCount = 0;
                lastFrameTime = System.currentTimeMillis();
            }
    	}

    }
    
}