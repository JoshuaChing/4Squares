package com.jchingdev.squares.singleplayer;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.jchingdev.squares.GameModeMenu;
import com.jchingdev.squares.R;
import com.chartboost.sdk.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivityNormal extends BaseGameActivity {
	
	//dimension variables
	private float density;
	private float dpWidth;
	private double squareSize;
	
	//game views
	private Button square1;
	private Button square2;
	private Button square3;
	private Button square4;
	private TextView scoreView;
	private TextView timerView;
	private TextView preGameTimerView;
	private Button answerButton;
	private RelativeLayout gameOverView;
	private TextView leaderboardsMessage;
	private TextView endScoreView;
	private TextView bestScoreView;
	//game variables, 0 = red, 1 = yellow, 2 = green, 3 = blue
	private int[] squares = {0,1,2,3}; //squares that user see, will be shuffled
	private Random random;
	private int answer;
	private int score;
	private CountDownTimer timer;
	private SharedPreferences storage;
	private SharedPreferences.Editor storageEdit;
	private int bestScore;
	private MediaPlayer clickSound;
	private MediaPlayer wrongSound;
	private boolean volume;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//initialize and cache chartboost
		Chartboost.startWithAppId(this, getResources().getString(R.string.chartboost_app_id),
				getResources().getString(R.string.chartboost_app_signature));
		Chartboost.onCreate(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_normal);
		Chartboost.cacheInterstitial(CBLocation.LOCATION_DEFAULT);
		//disable automatic signup to google play
		getGameHelper().setMaxAutoSignInAttempts(0);
		//get display dimensions in dp
		WindowManager wm = ((WindowManager)this.getSystemService(Context.WINDOW_SERVICE));
	    Display display = wm.getDefaultDisplay();
	    DisplayMetrics outMetrics = new DisplayMetrics();
	    display.getMetrics(outMetrics);
	    density  = getResources().getDisplayMetrics().density;
	    dpWidth  = outMetrics.widthPixels / density;
	    //find game views
	    scoreView = (TextView)findViewById(R.id.score);
	    timerView = (TextView)findViewById(R.id.timer);
	    endScoreView = (TextView)findViewById(R.id.endScore);
	    bestScoreView = (TextView)findViewById(R.id.bestScore);
	    preGameTimerView = (TextView)findViewById(R.id.preGameTimer);
	    square1 = (Button)findViewById(R.id.square1);
		square2 = (Button)findViewById(R.id.square2);
		square3 = (Button)findViewById(R.id.square3);
		square4 = (Button)findViewById(R.id.square4);
		answerButton = (Button)findViewById(R.id.answer);
		gameOverView = (RelativeLayout)findViewById(R.id.gameOverView);
		leaderboardsMessage = (TextView)findViewById(R.id.leaderboards_message);
	    //calculate and set square dimensions
	    squareSize = (dpWidth-80)/2.0;
		setSquareSize(square1, (int)squareSize, (int)squareSize);
		setSquareSize(square2, (int)squareSize, (int)squareSize);
		setSquareSize(square3, (int)squareSize, (int)squareSize);
		setSquareSize(square4, (int)squareSize, (int)squareSize);
		setSquareSize(answerButton,(int)squareSize*2,(int)squareSize/2);
		//disable buttons
		square1.setEnabled(false);
		square2.setEnabled(false);
		square3.setEnabled(false);
		square4.setEnabled(false);
		//set up sounds
		clickSound = MediaPlayer.create(getBaseContext(), R.raw.click);
		wrongSound = MediaPlayer.create(getBaseContext(), R.raw.wrong);
		//game variables
		storage = getSharedPreferences("STORAGE", MODE_PRIVATE);
		storageEdit= storage.edit();
		volume = storage.getBoolean("volume", true);
		random = new Random();
		answer = random.nextInt(4);
		setSquareColour(answer,answerButton);
		score = 0;
		startPreGameTimer();
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
	
	//set square dimensions (convert dp back to px)
	private void setSquareSize(Button b, int x, int y){
		Resources r = getResources();
		float pxX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, x, r.getDisplayMetrics());
		float pxY = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, y, r.getDisplayMetrics());
		android.view.ViewGroup.LayoutParams params = null;
		params = b.getLayoutParams();
		params.width=(int) pxX;
		params.height=(int) pxY;
		b.setLayoutParams(params);
	}
	
	//fisher-yates modern shuffle algorithm logic
	private void shuffleArray(int[] i){
		int index = i.length;
		
		while (--index > 0){
			int randomIndex = random.nextInt(index+1);
			swapValues(i,index,randomIndex);
		}
	}
	
	//helper function to swap values of an array based on 2 indexes
	private void swapValues(int[] c, int i, int j){
		int temp = c[i];
		c[i] = c[j];
		c[j] = temp;
	}
	
	public void answerClicked(View view){
		
	}
	
	public void square1Clicked(View view){
		checkAnswer(0);
	}
	
	public void square2Clicked(View view){
		checkAnswer(1);
	}
	
	public void square3Clicked(View view){
		checkAnswer(2);
	}
	
	public void square4Clicked(View view){
		checkAnswer(3);
	}
	
	public void setSquareColour(int i,Button b){
		switch (i){
			case 0:
				b.setBackgroundResource(R.drawable.red_square);
				break;
			case 1:
				b.setBackgroundResource(R.drawable.yellow_square);
				break;
			case 2:
				b.setBackgroundResource(R.drawable.green_square);
				break;
			case 3:
				b.setBackgroundResource(R.drawable.blue_square);
				break;
			default:
				b.setBackgroundResource(R.drawable.red_square);
		}
	}
	
	//TOP LEFT set square colour
	public void setSquareColourTL(int i,Button b){
		switch (i){
			case 0:
				b.setBackgroundResource(R.drawable.tl_red);
				break;
			case 1:
				b.setBackgroundResource(R.drawable.tl_yellow);
				break;
			case 2:
				b.setBackgroundResource(R.drawable.tl_green);
				break;
			case 3:
				b.setBackgroundResource(R.drawable.tl_blue);
				break;
			default:
				b.setBackgroundResource(R.drawable.tl_red);
		}
	}
	
	//TOP RIGHT set square colour
	public void setSquareColourTR(int i,Button b){
		switch (i){
			case 0:
				b.setBackgroundResource(R.drawable.tr_red);
				break;
			case 1:
				b.setBackgroundResource(R.drawable.tr_yellow);
				break;
			case 2:
				b.setBackgroundResource(R.drawable.tr_green);
				break;
			case 3:
				b.setBackgroundResource(R.drawable.tr_blue);
				break;
			default:
				b.setBackgroundResource(R.drawable.tr_red);
		}
	}
	
	//BOTTOM LEFT set square colour
	public void setSquareColourBL(int i,Button b){
		switch (i){
			case 0:
				b.setBackgroundResource(R.drawable.bl_red);
				break;
			case 1:
				b.setBackgroundResource(R.drawable.bl_yellow);
				break;
			case 2:
				b.setBackgroundResource(R.drawable.bl_green);
				break;
			case 3:
				b.setBackgroundResource(R.drawable.bl_blue);
				break;
			default:
				b.setBackgroundResource(R.drawable.bl_red);
		}
	}
	
	//BOTTOM RIGHT set square colour
	public void setSquareColourBR(int i,Button b){
		switch (i){
			case 0:
				b.setBackgroundResource(R.drawable.br_red);
				break;
			case 1:
				b.setBackgroundResource(R.drawable.br_yellow);
				break;
			case 2:
				b.setBackgroundResource(R.drawable.br_green);
				break;
			case 3:
				b.setBackgroundResource(R.drawable.br_blue);
				break;
			default:
				b.setBackgroundResource(R.drawable.br_red);
		}
	}	
		
	//called when square is clicked
	//where answer is the correct answer for the current round
	//where input is the square the user clicked
	//1 = r, 2 = y, 3 = g, 4 = b
	private void checkAnswer(int input){
		//if user gets the correct answer
		if (squares[input] == answer){
	        playClickSound();
			
			//set new answer
			answer = random.nextInt(4);		//new answer
			setSquareColour(answer,answerButton);
			
			//change new colours of squares
			shuffleArray(squares);
			setSquareColourTL(squares[0],square1);
			setSquareColourTR(squares[1],square2);
			setSquareColourBL(squares[2],square3);
			setSquareColourBR(squares[3],square4);
			
			//set new score
			score++;
			scoreView.setText(String.valueOf(score));

		}
		else{
	        playWrongSound();
			
			timer.cancel();
			gameOver();
		}
	}
	
	//start timer object
	private void startTimer(){
		timer = new CountDownTimer(30000, 10) {
		     public void onTick(long millisUntilFinished) {
		         timerView.setText(String.valueOf(new DecimalFormat("##.##").format((millisUntilFinished/1000.0))));
		     }

		     public void onFinish() {
		    	 timerView.setText("0.00");
		    	 gameOver();
		     }
		  }.start();
	}
	
	//start pre game timer object
	private void startPreGameTimer(){
		timer = new CountDownTimer(3000, 100) {
		     public void onTick(long millisUntilFinished) {
		         preGameTimerView.setText(String.valueOf((int)Math.ceil((millisUntilFinished/1000.0))));
		     }

		     public void onFinish() {
		    	 preGameTimerView.setText("0");
		    	 square1.setEnabled(true);
		 		 square2.setEnabled(true);
		 		 square3.setEnabled(true);
		 		 square4.setEnabled(true);
		    	 preGameTimerView.setVisibility(View.GONE);
		    	 startTimer();
		     }
		  }.start();
	}
	
	//function called when game is over
	private void gameOver(){
		if(Chartboost.hasInterstitial(CBLocation.LOCATION_DEFAULT)==false){
			gameOverDisplay();
			Chartboost.cacheInterstitial(CBLocation.LOCATION_DEFAULT);
		}else{
			Chartboost.showInterstitial(CBLocation.LOCATION_DEFAULT); //show chartboost
			gameOverDisplay();
			Chartboost.cacheInterstitial(CBLocation.LOCATION_DEFAULT);
		}
	}
	
	private void gameOverDisplay(){
		square1.setEnabled(false);
		square2.setEnabled(false);
		square3.setEnabled(false);
		square4.setEnabled(false);
		answerButton.setEnabled(false);
		bestScore = storage.getInt("bestScore",0);
		if (score > bestScore){
			bestScore = score;
			storageEdit.putInt("bestScore",bestScore);
			storageEdit.putBoolean("needSyncClassic", true);
			storageEdit.commit();
		}
		bestScoreView.setText("BEST: "+String.valueOf(bestScore));
		endScoreView.setText("SCORE: "+String.valueOf(score));
		gameOverView.setVisibility(View.VISIBLE);
		//submit score to google play service
		if(getApiClient().isConnected()){
			leaderboardsMessage.setVisibility(View.GONE);
			Games.Leaderboards.submitScore(getApiClient(), getString(R.string.classic_leaderboard), score);
		}else{
			leaderboardsMessage.setVisibility(View.VISIBLE);
		}
		//sync local to leaderboards
		syncBestScore();
	}
	
	//method to sync best score
	private void syncBestScore(){
		boolean needSync = storage.getBoolean("needSyncClassic",true);
		if(needSync && getApiClient().isConnected()){
			Games.Leaderboards.submitScore(getApiClient(), getString(R.string.classic_leaderboard), storage.getInt("bestScore",0));
			storageEdit.putBoolean("needSyncClassic", false);
			storageEdit.commit();
		}
	}
	
	//retry button clicked
	public void retryClicked(View view){
		playClickSound();
		//get new answer
		answer = random.nextInt(4);
		setSquareColour(answer,answerButton);
		//score and time views reset
		score = 0;
		timerView.setText("30.00");
		scoreView.setText("0");
		//reset squares
		for (int i = 0; i < 4; i++){
			squares[i] = i;
		}
		setSquareColourTL(squares[0],square1);
		setSquareColourTR(squares[1],square2);
		setSquareColourBL(squares[2],square3);
		setSquareColourBR(squares[3],square4);
		//show proper views and start pre game timer
		gameOverView.setVisibility(View.GONE);
		preGameTimerView.setVisibility(View.VISIBLE);
		startPreGameTimer();
	}
	
	//main menu button clicked
	public void menuClicked(View view){
		playClickSound();
		Intent intent = new Intent(this, GameModeMenu.class);
		finish();
		startActivity(intent);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
	
	//share button clicked
	public void shareClicked(View view){
		String tweetUrl = "https://twitter.com/intent/tweet?text=I just scored " + score + " points in 4 Squares! Think you can beat me? &url="
                + "https://play.google.com/store/apps/details?id=com.jchingdev.squares";
		Uri uri = Uri.parse(tweetUrl);
		startActivity(new Intent(Intent.ACTION_VIEW, uri));
	}
	
	@SuppressLint("DefaultLocale")
	public void facebookShareClicked(View view){
		String url = "https://play.google.com/store/apps/details?id=com.jchingdev.squares";
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, url);

		// See if official Facebook app is found
		boolean facebookAppFound = false;
		List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
		for (ResolveInfo info : matches) {
		    if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana")) {
		        intent.setPackage(info.activityInfo.packageName);
		        facebookAppFound = true;
		        break;
		    }
		}

		// As fallback, launch sharer.php in a browser
		if (!facebookAppFound) {
		    String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + url;
		    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
		}

		startActivity(intent);
	}
	
	//back button pressed
	@Override
	public void onBackPressed() {
		if(Chartboost.onBackPressed()){
			return;
		}else{
			Intent intent = new Intent(this, GameModeMenu.class);
			finish();
			startActivity(intent);
			overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		}
	}
	
	//play click sound
	private void playClickSound(){
		if (volume){
			clickSound.start();
		}
	}
	
	//play wrong sound
	private void playWrongSound(){
		if (volume){
			wrongSound.start();
		}
	}
	
	/*GOOGLE PLAY SERVICE (BASEGAMEACTIVITY)*/

	@Override
	public void onSignInFailed() {}

	@Override
	public void onSignInSucceeded(){}
	
	/*CHART BOOST EXTEND*/
	@Override
	public void onStart() {
	    super.onStart();
	    Chartboost.onStart(this);
	}

	@Override
	public void onResume() {
	    super.onResume();
	    Chartboost.onResume(this);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    Chartboost.onPause(this);
	}
	        
	@Override
	public void onStop() {
	    super.onStop();
	    Chartboost.onStop(this);
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    Chartboost.onDestroy(this);
	}
	
}
