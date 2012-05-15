/*
 * Instructions page.
 * Has button to return player to start screen.
 */

package puArcade.princetonTD.main;

import com.link.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GameInstructions extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set layout
		initWindow();
		setContentView(R.layout.tdinstructions);

		// set click listeners
		Button back = (Button)findViewById(R.id.tdinstructback);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	// initialize window properties
	private void initWindow() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
}
