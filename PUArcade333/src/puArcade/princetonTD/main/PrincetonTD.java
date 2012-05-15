/*
 * Starting screen for Tower Defense game.
 * Player can either start a new solo game or go to instructions page.
 */

package puArcade.princetonTD.main;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.link.R;

public class PrincetonTD extends Activity {

	private int score = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set layout
		initWindow();
		setContentView(R.layout.tdmenu);

		// set click listeners
		Button solo = (Button)findViewById(R.id.solo);
		solo.setOnClickListener(click);
		Button instruct = (Button)findViewById(R.id.tdinstruct);
		instruct.setOnClickListener(click);
	}
	
	// initialize window properties
	private void initWindow() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	private OnClickListener click = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// start new solo game
			if (v.getId() == R.id.solo) {
				Intent newGame = new Intent(PrincetonTD.this, puArcade.princetonTD.main.GameSoloActivity.class);
				startActivityForResult(newGame, 0);
			}
			// go to instructions page
			else if (v.getId() == R.id.tdinstruct) {
				Intent getInstruct = new Intent(PrincetonTD.this, puArcade.princetonTD.main.GameInstructions.class);
				startActivityForResult(getInstruct, 0);
			}
        }
	};
	
	// receive score returned by game
	protected void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				TextView scoreText = (TextView)findViewById(R.id.tdscore);
				score = data.getIntExtra("score", 0);
				scoreText.setText("Score: " + score);
			}
		}
	}

	// to send score back to linker
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent resultIntent = new Intent();
			resultIntent.putExtra("score", score);
			setResult(Activity.RESULT_OK, resultIntent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
