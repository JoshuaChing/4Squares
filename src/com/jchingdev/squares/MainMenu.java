package com.jchingdev.squares;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainMenu extends Activity{

	private SharedPreferences storage;
	private int bestScore;
	private TextView bestScoreView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		storage = getSharedPreferences("STORAGE", MODE_PRIVATE);
		bestScoreView = (TextView)findViewById(R.id.bestScore);
		displayBestScore();
	}
	
	@Override
	public void onRestart(){
		super.onRestart();
		displayBestScore();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		displayBestScore();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	public void playClicked(View view){
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
	
	private void displayBestScore(){
		bestScore = storage.getInt("bestScore", 0);
		bestScoreView.setText("BEST: "+String.valueOf(bestScore));
	}
	
}
