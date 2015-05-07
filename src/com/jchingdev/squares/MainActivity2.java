package com.jchingdev.squares;

import java.text.DecimalFormat;
import java.util.Random;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity2 extends BaseGameActivity {
	
	//dimension variables
	private float density;
	private float dpWidth;
	private double squareSize;
	
	//game views
	private Button square1;
	private Button square2;
	private Button square3;
	private Button square4;
	private Button square5;
	private Button square6;
	private Button square7;
	private Button square8;
	private Button square9;
	private TextView scoreView;
	private TextView timerView;
	private TextView preGameTimerView;
	private Button answerButton;
	private RelativeLayout gameOverView;
	private TextView leaderboardsMessage;
	private TextView endScoreView;
	private TextView bestScoreView;
	
	//game variables, 0 = red, 1 = orange, 2 = yellow, 3 = blue, 4 = turquoise, 5 = green, 6 = purple, 7 = pink, 8 = brown
	private int[] squares = {0,1,2,3,4,5,6,7,8}; //squares that user see, will be shuffled
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
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);
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
		square5 = (Button)findViewById(R.id.square5);
		square6 = (Button)findViewById(R.id.square6);
		square7 = (Button)findViewById(R.id.square7);
		square8 = (Button)findViewById(R.id.square8);
		square9 = (Button)findViewById(R.id.square9);
		answerButton = (Button)findViewById(R.id.answer);
		gameOverView = (RelativeLayout)findViewById(R.id.gameOverView);
		leaderboardsMessage = (TextView)findViewById(R.id.leaderboards_message);
	    //calculate and set square dimensions
	    squareSize = (dpWidth-80)/3.0;
		setSquareSize(square1, (int)squareSize, (int)squareSize);
		setSquareSize(square2, (int)squareSize, (int)squareSize);
		setSquareSize(square3, (int)squareSize, (int)squareSize);
		setSquareSize(square4, (int)squareSize, (int)squareSize);
		setSquareSize(square5, (int)squareSize, (int)squareSize);
		setSquareSize(square6, (int)squareSize, (int)squareSize);
		setSquareSize(square7, (int)squareSize, (int)squareSize);
		setSquareSize(square8, (int)squareSize, (int)squareSize);
		setSquareSize(square9, (int)squareSize, (int)squareSize);
		setSquareSize(answerButton,(int)squareSize*3,(int)((dpWidth-80)/4.0));
		//disable buttons
		square1.setEnabled(false);
		square2.setEnabled(false);
		square3.setEnabled(false);
		square4.setEnabled(false);
		square5.setEnabled(false);
		square6.setEnabled(false);
		square7.setEnabled(false);
		square8.setEnabled(false);
		square9.setEnabled(false);
		//set up sounds
		clickSound = MediaPlayer.create(getBaseContext(), R.raw.click);
		wrongSound = MediaPlayer.create(getBaseContext(), R.raw.wrong);
		//game variables
		storage = getSharedPreferences("STORAGE", MODE_PRIVATE);
		storageEdit= storage.edit();
		volume = storage.getBoolean("volume", true);
		random = new Random();
		answer = random.nextInt(9);
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
	
	public void square5Clicked(View view){
		checkAnswer(4);
	}
	
	public void square6Clicked(View view){
		checkAnswer(5);
	}
	
	public void square7Clicked(View view){
		checkAnswer(6);
	}
	
	public void square8Clicked(View view){
		checkAnswer(7);
	}
	
	public void square9Clicked(View view){
		checkAnswer(8);
	}
	
	public void setSquareColour(int i,Button b){
		switch (i){
			case 0:
				b.setBackgroundResource(R.drawable.red_square);
				break;
			case 1:
				b.setBackgroundResource(R.drawable.orange_square);
				break;
			case 2:
				b.setBackgroundResource(R.drawable.yellow_square);
				break;
			case 3:
				b.setBackgroundResource(R.drawable.blue_square);
				break;
			case 4:
				b.setBackgroundResource(R.drawable.turquoise_square);
				break;
			case 5:
				b.setBackgroundResource(R.drawable.green_square);
				break;
			case 6:
				b.setBackgroundResource(R.drawable.purple_square);
				break;
			case 7:
				b.setBackgroundResource(R.drawable.pink_square);
				break;
			case 8:
				b.setBackgroundResource(R.drawable.brown_square);
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
				b.setBackgroundResource(R.drawable.tl_orange);
				break;
			case 2:
				b.setBackgroundResource(R.drawable.tl_yellow);
				break;
			case 3:
				b.setBackgroundResource(R.drawable.tl_blue);
				break;
			case 4:
				b.setBackgroundResource(R.drawable.tl_turquoise);
				break;
			case 5:
				b.setBackgroundResource(R.drawable.tl_green);
				break;
			case 6:
				b.setBackgroundResource(R.drawable.tl_purple);
				break;
			case 7:
				b.setBackgroundResource(R.drawable.tl_pink);
				break;
			case 8:
				b.setBackgroundResource(R.drawable.tl_brown);
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
				b.setBackgroundResource(R.drawable.tr_orange);
				break;
			case 2:
				b.setBackgroundResource(R.drawable.tr_yellow);
				break;
			case 3:
				b.setBackgroundResource(R.drawable.tr_blue);
				break;
			case 4:
				b.setBackgroundResource(R.drawable.tr_turquoise);
				break;
			case 5:
				b.setBackgroundResource(R.drawable.tr_green);
				break;
			case 6:
				b.setBackgroundResource(R.drawable.tr_purple);
				break;
			case 7:
				b.setBackgroundResource(R.drawable.tr_pink);
				break;
			case 8:
				b.setBackgroundResource(R.drawable.tr_brown);
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
				b.setBackgroundResource(R.drawable.bl_orange);
				break;
			case 2:
				b.setBackgroundResource(R.drawable.bl_yellow);
				break;
			case 3:
				b.setBackgroundResource(R.drawable.bl_blue);
				break;
			case 4:
				b.setBackgroundResource(R.drawable.bl_turquoise);
				break;
			case 5:
				b.setBackgroundResource(R.drawable.bl_green);
				break;
			case 6:
				b.setBackgroundResource(R.drawable.bl_purple);
				break;
			case 7:
				b.setBackgroundResource(R.drawable.bl_pink);
				break;
			case 8:
				b.setBackgroundResource(R.drawable.bl_brown);
				break;
			default:
				b.setBackgroundResource(R.drawable.bl_red);
		}
	}
	
	//MID set square colour
	public void setSquareColourM(int i,Button b){
		switch (i){
			case 0:
				b.setBackgroundResource(R.drawable.m_red);
				break;
			case 1:
				b.setBackgroundResource(R.drawable.m_orange);
				break;
			case 2:
				b.setBackgroundResource(R.drawable.m_yellow);
				break;
			case 3:
				b.setBackgroundResource(R.drawable.m_blue);
				break;
			case 4:
				b.setBackgroundResource(R.drawable.m_turquoise);
				break;
			case 5:
				b.setBackgroundResource(R.drawable.m_green);
				break;
			case 6:
				b.setBackgroundResource(R.drawable.m_purple);
				break;
			case 7:
				b.setBackgroundResource(R.drawable.m_pink);
				break;
			case 8:
				b.setBackgroundResource(R.drawable.m_brown);
				break;
			default:
				b.setBackgroundResource(R.drawable.m_red);
		}
	}
	
	//BOTTOM RIGHT set square colour
	public void setSquareColourBR(int i,Button b){
		switch (i){
			case 0:
				b.setBackgroundResource(R.drawable.br_red);
				break;
			case 1:
				b.setBackgroundResource(R.drawable.br_orange);
				break;
			case 2:
				b.setBackgroundResource(R.drawable.br_yellow);
				break;
			case 3:
				b.setBackgroundResource(R.drawable.br_blue);
				break;
			case 4:
				b.setBackgroundResource(R.drawable.br_turquoise);
				break;
			case 5:
				b.setBackgroundResource(R.drawable.br_green);
				break;
			case 6:
				b.setBackgroundResource(R.drawable.br_purple);
				break;
			case 7:
				b.setBackgroundResource(R.drawable.br_pink);
				break;
			case 8:
				b.setBackgroundResource(R.drawable.br_brown);
				break;
			default:
				b.setBackgroundResource(R.drawable.br_red);
		}
	}	
		
	//called when square is clicked
	//where answer is the correct answer for the current round
	//where input is the square the user clicked
	//0 = red, 1 = orange, 2 = yellow, 3 = blue, 4 = turquoise, 5 = green, 6 = purple, 7 = pink, 8 = brown
	private void checkAnswer(int input){
		//if user gets the correct answer
		if (squares[input] == answer){
	        playClickSound();
			
			//set new answer
			answer = random.nextInt(9);		//new answer
			setSquareColour(answer,answerButton);
			
			//change new colours of squares
			shuffleArray(squares);
			setSquareColourTL(squares[0],square1);
			setSquareColourM(squares[1],square2);
			setSquareColourTR(squares[2],square3);
			setSquareColourM(squares[3],square4);
			setSquareColourM(squares[4],square5);
			setSquareColourM(squares[5],square6);
			setSquareColourBL(squares[6],square7);
			setSquareColourM(squares[7],square8);
			setSquareColourBR(squares[8],square9);
			
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
		 		 square5.setEnabled(true);
		 		 square6.setEnabled(true);
		 		 square7.setEnabled(true);
		 		 square8.setEnabled(true);
		 		 square9.setEnabled(true);
		    	 preGameTimerView.setVisibility(View.GONE);
		    	 startTimer();
		     }
		  }.start();
	}
	
	//function called when game is over
	private void gameOver(){
		square1.setEnabled(false);
		square2.setEnabled(false);
		square3.setEnabled(false);
		square4.setEnabled(false);
		square5.setEnabled(false);
		square6.setEnabled(false);
		square7.setEnabled(false);
		square8.setEnabled(false);
		square9.setEnabled(false);
		answerButton.setEnabled(false);
		bestScore = storage.getInt("bestScore3by3",0);
		if (score > bestScore){
			bestScore = score;
			storageEdit.putInt("bestScore3by3",bestScore);
			storageEdit.commit();
		}
		bestScoreView.setText("BEST: "+String.valueOf(bestScore));
		endScoreView.setText("SCORE: "+String.valueOf(score));
		gameOverView.setVisibility(View.VISIBLE);
		//submit score to google play service
		if(getApiClient().isConnected()){
			leaderboardsMessage.setVisibility(View.GONE);
			Games.Leaderboards.submitScore(getApiClient(), getString(R.string.challenge_leaderboard), score);
		}else{
			leaderboardsMessage.setVisibility(View.VISIBLE);
		}
	}
	
	//retry button clicked
	public void retryClicked(View view){
		playClickSound();
		//get new answer
		answer = random.nextInt(9);
		setSquareColour(answer,answerButton);
		//score and time views reset
		score = 0;
		timerView.setText("30.00");
		scoreView.setText("0");
		//reset squares
		for (int i = 0; i < 9; i++){
			squares[i] = i;
		}
		setSquareColourTL(squares[0],square1);
		setSquareColourM(squares[1],square2);
		setSquareColourTR(squares[2],square3);
		setSquareColourM(squares[3],square4);
		setSquareColourM(squares[4],square5);
		setSquareColourM(squares[5],square6);
		setSquareColourBL(squares[6],square7);
		setSquareColourM(squares[7],square8);
		setSquareColourBR(squares[8],square9);
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
	
	//back button pressed
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, GameModeMenu.class);
		finish();
		startActivity(intent);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
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
	
	
	/*AD MOB FRAGMENT*/
	public static class AdFragment extends Fragment {

        private AdView mAdView;

        public AdFragment() {
        }

        @Override
        public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);

            // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
            // values/strings.xml.
            mAdView = (AdView) getView().findViewById(R.id.adView);

            // Create an ad request. Check logcat output for the hashed device ID to
            // get test ads on a physical device. e.g.
            // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
            AdRequest adRequest = new AdRequest.Builder()
                    //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();

            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_ad, container, false);
        }

        /** Called when leaving the activity */
        @Override
        public void onPause() {
            if (mAdView != null) {
                mAdView.pause();
            }
            super.onPause();
        }

        /** Called when returning to the activity */
        @Override
        public void onResume() {
            super.onResume();
            if (mAdView != null) {
                mAdView.resume();
            }
        }

        /** Called before the activity is destroyed */
        @Override
        public void onDestroy() {
            if (mAdView != null) {
                mAdView.destroy();
            }
            super.onDestroy();
        }

    }
	
	/*GOOGLE PLAY SERVICE (BASEGAMEACTIVITY)*/

	@Override
	public void onSignInFailed() {}

	@Override
	public void onSignInSucceeded(){}
	
}