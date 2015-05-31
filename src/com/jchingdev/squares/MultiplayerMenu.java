package com.jchingdev.squares;

import com.google.example.games.basegameutils.BaseGameActivity;

import android.os.Bundle;

public class MultiplayerMenu extends BaseGameActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multiplayer_menu);
	}

	@Override
	public void onSignInFailed() {}

	@Override
	public void onSignInSucceeded() {}
}
