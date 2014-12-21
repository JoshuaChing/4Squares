package com.jchingdev.squares;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends Activity {
	
	//dimension variables
	private int displayX;
	private double squareSize;
	
	//square button views
	private Button square1;
	private Button square2;
	private Button square3;
	private Button square4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//get display dimensions
		WindowManager wm = ((WindowManager)this.getSystemService(Context.WINDOW_SERVICE));
	    Display display = wm.getDefaultDisplay();
	    Point point = getDisplaySize(display);
	    displayX = Integer.parseInt(String.valueOf(point.x));
	    //find square views
	    square1 = (Button)findViewById(R.id.square1);
		square2 = (Button)findViewById(R.id.square2);
		square3 = (Button)findViewById(R.id.square3);
		square4 = (Button)findViewById(R.id.square4);
	    //calculate and set square dimensions
	    squareSize = (displayX-40)/2.0;
		setSquareSize(square1, (int)squareSize);
		setSquareSize(square2, (int)squareSize);
		setSquareSize(square3, (int)squareSize);
		setSquareSize(square4, (int)squareSize);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	//returns point of screen size
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private static Point getDisplaySize(final Display display) {
		Point point = new Point();
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){   //API LEVEL 13
		     display.getSize(point);
		}else{    
		    point.x = display.getWidth();
		    point.y = display.getHeight();
		}
		return point;
	}
	
	//set square dimensions
	private void setSquareSize(Button b, int i){
		android.view.ViewGroup.LayoutParams params = null;
		params = b.getLayoutParams();
		params.width=i;
		params.height=i;
		b.setLayoutParams(params);
	}
	
}
