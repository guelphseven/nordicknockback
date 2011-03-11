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
import android.content.Intent;

import java.math.*;
import java.util.Random;

public class ActionDefense extends Activity {
    LinearLayout mLinearLayout;
    long mLastTouchTime;
    Intent game, help;
    Button startButton;
    Button helpButton;
    
    private final static int MAX_SPRITES = 50;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );

        setContentView(R.layout.main);
        
        startButton = (Button) findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		game = new Intent( ActionDefense.this, com.g7.Game.class );
    			startActivity(game);
        	}
        });
        
        helpButton = (Button) findViewById(R.id.help_button);
        helpButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		help = new Intent( ActionDefense.this, com.g7.Help.class );
    			startActivity(help);
        	}
        });
       
    }
    
}