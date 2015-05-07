package com.jchingdev.squares;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GameModeMenu extends FragmentActivity {

	private MediaPlayer clickSound;
	private SharedPreferences storage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_mode_menu);
		clickSound = MediaPlayer.create(getBaseContext(), R.raw.click);
		storage = getSharedPreferences("STORAGE", MODE_PRIVATE);
	}
	
	//classic mode button pressed
	public void classicClicked(View view){
		if (storage.getBoolean("volume",true)){
			clickSound.start();
		}
		Intent intent = new Intent(this, MainActivity.class);
		finish();
		startActivity(intent);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
	
	//challenge mode button pressed
	public void challengeClicked(View view){
		if (storage.getBoolean("volume",true)){
			clickSound.start();
		}
		Intent intent = new Intent(this, MainActivity2.class);
		finish();
		startActivity(intent);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
	
	//back button pressed (UI)
	public void backClicked(View view){
		if (storage.getBoolean("volume",true)){
			clickSound.start();
		}
		finish();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
	
	//back button pressed
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
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
}
