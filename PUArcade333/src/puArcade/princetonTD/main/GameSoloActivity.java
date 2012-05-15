/*
 * Game UI
 * Contains the game display, has a panel of buttons for player input,
 * and has a panel of text fields to display game data to player.
 */

package puArcade.princetonTD.main;

import puArcade.princetonTD.towers.TowerAA;
import puArcade.princetonTD.towers.TowerAir;
import puArcade.princetonTD.towers.TowerArcher;
import puArcade.princetonTD.towers.TowerCanon;
import puArcade.princetonTD.towers.TowerEarth;
import puArcade.princetonTD.towers.TowerElectric;
import puArcade.princetonTD.towers.TowerFire;
import puArcade.princetonTD.towers.TowerIce;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ImageButton;

import com.link.R;

public class GameSoloActivity extends Activity implements OnClickListener {
	
	// View to hold game display
	private GameBoardView gbv;
	// Handler to refresh game data
	private RefreshHandler mRedrawHandler = new RefreshHandler();
	
	// Game data visible to user
	private TextView scoreText;
	private TextView waveText;
	private TextView lifeText;
	private TextView goldText;

	// Tower buttons
	private ImageButton arrow;
	private ImageButton canon;
	private ImageButton aa;
	private ImageButton ice;
	private ImageButton elect;
	private ImageButton fire;
	private ImageButton air;
	private ImageButton earth;

	// refresh UI
	class RefreshHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			GameSoloActivity.this.updateUI();
		}

		public void sleep(long delayMillis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	};
	
	// update UI
	private void updateUI(){
		mRedrawHandler.sleep(1000);
		scoreText.setText("Score:\n" + gbv.getScore());
		waveText.setText("Next wave:\n" + gbv.getWave());
		lifeText.setText("Lives:\n" + gbv.getLives());
		int gold = gbv.getGold();
		goldText.setText("Gold:\n" + gold);

		// enable/disable tower buttons
		if (gbv.canPlaceTowers())
		{
			if (gold < (new TowerArcher()).getPrice())
				arrow.setEnabled(false);
			else
				arrow.setEnabled(true);
			if (gold < (new TowerCanon()).getPrice())
				canon.setEnabled(false);
			else
				canon.setEnabled(true);
			if (gold < (new TowerAA()).getPrice())
				aa.setEnabled(false);
			else
				aa.setEnabled(true);
			if (gold < (new TowerIce()).getPrice())
				ice.setEnabled(false);
			else
				ice.setEnabled(true);
			if (gold < (new TowerElectric()).getPrice())
				elect.setEnabled(false);
			else
				elect.setEnabled(true);
			if (gold < (new TowerFire()).getPrice())
				fire.setEnabled(false);
			else
				fire.setEnabled(true);
			if (gold < (new TowerAir()).getPrice())
				air.setEnabled(false);
			else
				air.setEnabled(true);
			if (gold < (new TowerEarth()).getPrice())
				earth.setEnabled(false);
			else
				earth.setEnabled(true);
		}
		else
		{
			arrow.setEnabled(false);
			canon.setEnabled(false);
			aa.setEnabled(false);
			ice.setEnabled(false);
			elect.setEnabled(false);
			fire.setEnabled(false);
			air.setEnabled(false);
			earth.setEnabled(false);
		}
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initWindow();
		
		// initialize UI
		setContentView(R.layout.tdmain);
		// initialize game display
		gbv = (GameBoardView)findViewById(R.id.surfaceView1);
		gbv.requestFocusFromTouch();

		// Tower buttons
		arrow = (ImageButton)findViewById(R.id.tdbutton1);
		canon = (ImageButton)findViewById(R.id.tdbutton2);
		aa = (ImageButton)findViewById(R.id.tdbutton3);
		ice = (ImageButton)findViewById(R.id.tdbutton4);
		elect = (ImageButton)findViewById(R.id.tdbutton5);
		fire = (ImageButton)findViewById(R.id.tdbutton6);
		air = (ImageButton)findViewById(R.id.tdbutton7);
		earth = (ImageButton)findViewById(R.id.tdbutton8);
		
		// set tower button listeners
		arrow.setOnClickListener(this);
		canon.setOnClickListener(this);
		aa.setOnClickListener(this);
		ice.setOnClickListener(this);
		elect.setOnClickListener(this);
		fire.setOnClickListener(this);
		air.setOnClickListener(this);
		earth.setOnClickListener(this);

		// get text fields
		scoreText = (TextView)findViewById(R.id.text1);
		waveText = (TextView)findViewById(R.id.text2);
		lifeText = (TextView)findViewById(R.id.text3);
		goldText = (TextView)findViewById(R.id.text4);

		updateUI();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	// initialize window properties
	private void initWindow() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}
	
	// pass user click input to game display to handle
	public void onClick(View v) {
		gbv.onClick(v);
	}
	
	// return score to start screen when finished
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent resultIntent = new Intent();
			resultIntent.putExtra("score", gbv.getScore());
			setResult(Activity.RESULT_OK, resultIntent);
			gbv.finish();
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
