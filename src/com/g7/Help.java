package com.g7;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.g7.Game.DrawableView;

public class Help extends Activity {
	TextView text;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ); 

    	setContentView(R.layout.help);
    	
    	text = (TextView)findViewById(R.id.help_text);
    	text.setMovementMethod(new ScrollingMovementMethod()); 
    	text.setText("Welcome to Nordic KnockBack Beta - augmented interactive Tower Defense\n\n" +
    			"Objective: Prevent your 3 kegs from being taken off screen\n\n" +
    			"Tower Cost: 5 gold and you are rewarded 1 gold per kill\n\n" +
    			"Max Towers: 5 per row. Use the tower add button in the top right corner and select which row to place on\n\n" +
    			"Powerups: 2 types, Shake and Blow\n\n" +
    			"Shake powerups: shake your phone to kill all enemies and drop the kegs\n\n" +
    			"Blow powerups: blow into your phone's microphone and kegs will return to the middle\n\n\n" +
    			"Please report any problems/comments/concerns/technical difficulties to guelphseven@gmail.com\n\n" +
    			"Remember, this is just a BETA version. The final version will be released soon!"
    			);
    }

}
