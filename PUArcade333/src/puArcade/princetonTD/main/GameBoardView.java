/*
 * Game Display
 * Bridges the UI to the Game Logic.
 * Handles player input passed down from UI.
 * Provides methods for UI to get game data from game thread.
 */

package puArcade.princetonTD.main;

import com.link.R;
import puArcade.princetonTD.towers.*;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class GameBoardView extends SurfaceView implements
OnTouchListener, SurfaceHolder.Callback {
	
	// Game display
	private SurfaceHolder surfaceHolder;

	public Context context;

	/*
	 * Reference to the thread which controls graphics/game control
	 * Will communicate to View through use of Handlers in UI
	 */
	private GameThread gameThread;

	
	// Constructors
	
	public GameBoardView(Context context) {
		super(context);
		this.context = context;
		setFocusable(true);
		setFocusableInTouchMode(true);
		this.setOnTouchListener(this);
	}

	public GameBoardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		setFocusable(true);
		setFocusableInTouchMode(true);
		this.setOnTouchListener(this);
	}

	public GameBoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		setFocusable(true);
		setFocusableInTouchMode(true);
		this.setOnTouchListener(this);
	}
	
	// when screen size is changed (landscape)
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		gameThread = new GameThread(holder, w, h, context);

		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	//-----------------------------------------------------
	// inherited methods
	//-----------------------------------------------------
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// not implemented
	}
	
	public void surfaceCreated(SurfaceHolder holder) {
		gameThread.setRunning(true);
		gameThread.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		gameThread.setRunning(false);
		while (retry) {
			try {
				gameThread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}

	public SurfaceHolder getSurfaceHolder() {
		return surfaceHolder;
	}
	
	//-----------------------------------------------------
	// handles user input passed from the UI
	//-----------------------------------------------------
	
	// when tower buttons are clicked (set active tower to be placed)
	public void onClick(View v) {
		if (v.getId() == R.id.tdbutton1)
		{
			gameThread.setActiveTower(new TowerArcher());
		}
		else if (v.getId() == R.id.tdbutton2)
		{
			gameThread.setActiveTower(new TowerCanon());
		}
		else if (v.getId() == R.id.tdbutton3)
		{
			gameThread.setActiveTower(new TowerAA());
		}
		else if (v.getId() == R.id.tdbutton4)
		{
			gameThread.setActiveTower(new TowerIce());
		}
		else if (v.getId() == R.id.tdbutton5)
		{
			gameThread.setActiveTower(new TowerElectric());
		}
		else if (v.getId() == R.id.tdbutton6)
		{
			gameThread.setActiveTower(new TowerFire());
		}
		else if (v.getId() == R.id.tdbutton7)
		{
			gameThread.setActiveTower(new TowerAir());
		}
		else if (v.getId() == R.id.tdbutton8)
		{
			gameThread.setActiveTower(new TowerEarth());
		}
	}
	
	// when game display is touched with a motion event
	// (reposition screen || place tower)
	public boolean onTouch(View view, MotionEvent event) {
		gameThread.onTouch(event);
		return true;
	}
	
	// when center dpad is pressed (launch wave)
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER){
			return gameThread.doOnKeyDown(keyCode, event);
		}
		else{
			return super.onKeyDown(keyCode, event);
		}
	}

	//-----------------------------------------------------
	// return data to UI when requested
	//-----------------------------------------------------

	public int getScore() {
		if (gameThread != null)
			return gameThread.getScore();
		else
			return 0;
	}
	public String getWave() {
		if (gameThread != null)
			return gameThread.getWave();
		else
			return null;
	}
	public int getLives() {
		if (gameThread != null)
			return gameThread.getLives();
		else
			return 0;
	}
	public int getGold() {
		if (gameThread != null)
			return gameThread.getGold();
		else
			return 0;
	}
	public boolean canPlaceTowers() {
		if (gameThread != null)
			return gameThread.canPlaceTowers();
		else
			return false;
	}

	// end game when requested
	public void finish()
	{
		if (gameThread != null)
			gameThread.setRunning(false);
	}

}