package com.jchingdev.squares;

//import com.chartboost.sdk.*;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.games.Games;

import com.google.example.games.basegameutils.BaseGameActivity;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class MainMenu extends BaseGameActivity implements View.OnClickListener{

	private SharedPreferences storage;
	private SharedPreferences.Editor storageEdit;
	private MediaPlayer clickSound;
	private ImageButton volumeButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//initialize and cache chartboost
		/*Chartboost.startWithAppId(this, getResources().getString(R.string.chartboost_app_id),
			getResources().getString(R.string.chartboost_app_signature));
		Chartboost.onCreate(this);*/
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		clickSound = MediaPlayer.create(getBaseContext(), R.raw.click);
		storage = getSharedPreferences("STORAGE", MODE_PRIVATE);
		storageEdit= storage.edit();
		volumeButton = (ImageButton)findViewById(R.id.volume);
		displayVolumeImage();
		//google play service buttons
		findViewById(R.id.sign_in_button).setOnClickListener(this);
		findViewById(R.id.sign_out_button).setOnClickListener(this);
		findViewById(R.id.leaderboards).setOnClickListener(this);
	}
	
	@Override
	public void onRestart(){
		super.onRestart();
	}
	
	@Override
	public void onResume(){
		super.onResume();
	    //Chartboost.onResume(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
	
	//play button clicked
	public void playClicked(View view){
		if (storage.getBoolean("volume",true)){
			clickSound.start();
		}
		Intent intent = new Intent(this, GameModeMenu.class);
		startActivity(intent);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
	
	//multiplayer play button clicked
	public void multiplayerPlayClicked(View view){
		if (storage.getBoolean("volume",true)){
			clickSound.start();
		}
		Intent intent = new Intent(this, MultiplayerMenu.class);
		startActivity(intent);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
	
	//display correct volume button image
	private void displayVolumeImage(){
		if (storage.getBoolean("volume",true)){
			volumeButton.setImageResource(R.drawable.ic_action_volume_on);
		}else{
			volumeButton.setImageResource(R.drawable.ic_action_volume_muted);
		}
	}
	
	//volume button clicked
	public void volumeClicked(View view){
		//if volume settings is on
		if (storage.getBoolean("volume",true)){
			storageEdit.putBoolean("volume",false);
			volumeButton.setImageResource(R.drawable.ic_action_volume_muted);
		}else{
			storageEdit.putBoolean("volume",true);
			clickSound.start();
			volumeButton.setImageResource(R.drawable.ic_action_volume_on);
		}
		storageEdit.commit();
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
	public void onSignInFailed() {
		findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
	    findViewById(R.id.sign_out_button).setVisibility(View.GONE);
	}

	@Override
	public void onSignInSucceeded() {
		findViewById(R.id.sign_in_button).setVisibility(View.GONE);
	    findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.sign_in_button) { //sign in google+
	        beginUserInitiatedSignIn();
	    }
	    else if (view.getId() == R.id.sign_out_button) { //sign out google+
	        signOut();
	        findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
	        findViewById(R.id.sign_out_button).setVisibility(View.GONE);
	    }
	    else if (view.getId() == R.id.leaderboards){ //leaderboards if logged in
	    	if (storage.getBoolean("volume",true)){
				clickSound.start();
			}
	    	if(getApiClient().isConnected()){
	    		startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(getApiClient()), 1);
	    	}else{
	    		new AlertDialog.Builder(this)
				.setTitle("Error")
				.setMessage("Please sign into Google Play to access the leaderboards!")
				.setIcon(R.drawable.ic_launcher)
				.setPositiveButton("OK", new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int which){
							//place alert dialog functions here
						}
				})
				.show();
	    	}
	    }
	}
	
	/*CHART BOOST EXTEND*/
	/*@Override
	public void onStart() {
	    super.onStart();
	    Chartboost.onStart(this);
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
	
	@Override
	public void onBackPressed() {
	    // If an interstitial is on screen, close it.
	    if (Chartboost.onBackPressed())
	        return;
	    else
	        super.onBackPressed();
	}*/
}
