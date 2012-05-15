package com.squirrel;

import com.link.*;
import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * TetrisView: implementation of a simple game of Tetris
 */
public class SquirrelView extends TileView {

	/**
	 * Labels for the drawables that will be loaded into the TileView class
	 */
	public static final int BROWNSQ = 1;
	public static final int REDSQ = 2;
	public static final int SKUNK = 3;
	public static final int TREE = 4;

	private static double currX;
	private static double currY;
	private static int currType = 0;
	private static int range;
	private static boolean gameover = false;
	private static int score = 0;
	private static int counter = 0;
	
	
	/**
	 * mStatusText: text shows to the user in some run states
	 */
	private TextView mStatusText;
	
	/**
	 * mLastMove: tracks the absolute time when the last Squirrel
	 * spawned, and tells whether or not a new one should spawn.
	 */
	private long mLastMove;

	/** Maximum delay between each Squirrel **/
	private int mDelay = 500;
	/** Keeps track of when the game starts **/
	private long mFirst;
	/** Game length: 30 seconds **/
	private int mTotalTime = 30000;

	/**
	 * Create a simple handler that we can use to cause animation to happen. We
	 * set ourselves as a target and we can use the sleep() function to cause an
	 * update/invalidate to occur at a later date.
	 */
	private RefreshHandler mRedrawHandler = new RefreshHandler();

	class RefreshHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			SquirrelView.this.update();
			SquirrelView.this.invalidate();
		}

		public void sleep(long delayMillis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	};	
	/**
	 * Constructs a SquirrelView based on inflation from XML
	 * 
	 * @param context
	 * @param attrs
	 */
	public SquirrelView(Context context)
	{
		super(context);
	}
	public SquirrelView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SquirrelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	private void initView() {
		setFocusable(true);

		Resources r = this.getContext().getResources();

		resetTiles(5);
		loadTile(BROWNSQ, r.getDrawable(R.drawable.brown_squirrel_icon));
		loadTile(REDSQ, r.getDrawable(R.drawable.red_squirrel_icon));
		loadTile(SKUNK, r.getDrawable(R.drawable.skunk));
		loadTile(TREE, r.getDrawable(R.drawable.tree_icon));
		
		updateWalls();
		drawNew();

		score = 0;
		counter = 0;
		mFirst = System.currentTimeMillis();
		range = mTileSize;
//		mLastMove = System.currentTimeMillis();
	}

	/**
	 * Sets the TextView that will be used to give information (such as "Game
	 * Over" to the user.
	 * 
	 * @param newView
	 */
	public void setTextView(TextView newView) {
		mStatusText = newView;
	}

    public void setText(String str) {
        mStatusText.setText(str);
    }	
	
	public boolean touch(double x, double y)
	{
		//if the point touched is within (2 * range), return true and create a new point
		if ((Math.abs(x - currX) < (range * 2.0)) && (Math.abs(y - currY) < (range * 2.0)))
		{
			if (currType == BROWNSQ)
			{
				score += 10;
				counter++;
				setText("Score: " + score);
			
				if (counter > 5)
				{
					score += 10 * (counter - 5);
					setText("Score: " + score + " - " + counter + "in a row!");
				}
			}
			else if (currType == REDSQ)
			{
				score += 100;
				counter++;
				setText("Score: " + score);
				
				if (counter > 5)
				{
//					score += 10 * (counter - 5);
					setText("Score: " + score + " - " + counter + "in a row!");
				}
			}
			else if (currType == SKUNK)
			{
				score -= 100;
				
				if (score < 0)
					score = 0;
				counter = 0;
				setText("Score: " + score + " - Oops!");
			}
			
			updateNew();
			return true;
		}
		else
		{
			setText("Score: " + score + " - Miss!");
			counter = 0;
			return false;
		}
		
	}
	
	/**
	 * Handles the basic update loop, checking to see if we are in the running
	 * state, determining if a move should be made, updating the Tetris's
	 * location.
	 */
	public void update() {
		if (!gameover)
		{
			long now = System.currentTimeMillis();
	
			if (now - mLastMove > mDelay) {
				clearTiles();
				updateWalls();
				drawNew();
				mLastMove = now;
			}
			
			//end game
			if (now > mFirst + mTotalTime)
			{
//				gameover = true;
			}
			
			mRedrawHandler.sleep(0);
		}
		else
		{
			clearTiles();
			updateWalls();
			mStatusText.setText("Game Over!");
		}

	}

	public void updateNew() {
		if (!gameover)
		{
			clearTiles();
			updateWalls();
			drawNew();
			mLastMove = System.currentTimeMillis();
		}
		else
		{
			clearTiles();
			updateWalls();
			mStatusText.setText("Game Over!");
		}
	}
	
	/**
	 * Draws some walls.
	 */
	private void updateWalls() {
		for (int x = 0; x < xTileNum; x++) {
			setTile(TREE, x, 0);
			setTile(TREE, x, yTileNum - 1);
		}
		for (int y = 1; y < xTileNum - 1; y++) {
			setTile(TREE, 0, y);
			setTile(TREE, yTileNum - 1, y);
		}
	}

	/**
	 * Create and draw the next block.
	 */
	public void drawNew() {
		int type = 0;
		
		double rand = Math.random();
		
		if (rand < 0.15)
			type = SKUNK;
		else if (rand < 0.3)
			type = REDSQ;
		else //if (Math.random() < 0.75)
			type = BROWNSQ;

		currType = type;
		
		Random randomGen = new Random();
		
		double oldX = currX;
		double oldY = currY;

		int xRandom = randomGen.nextInt(xTileNum - 2) + 1;
		int yRandom = randomGen.nextInt(yTileNum - 2) + 1;
		
		currX = xOffset + (xRandom * range) + (0.5 * range);
		currY = barOffset + yOffset + (yRandom * range) + (0.5 * range);

		while ((oldX == currX) && (oldY == currY))
		{
			xRandom = randomGen.nextInt(xTileNum - 2) + 1;
			yRandom = randomGen.nextInt(yTileNum - 2) + 1;
			currX = xOffset + (xRandom * range) + (0.5 * range);			
			currY = barOffset + yOffset + (yRandom * range) + (0.5 * range);
		}
				
		Log.d("xyRandom", xRandom + " " + yRandom);
		
		setTile(type, xRandom, yRandom);
	}
	
	public int getType()
	{
		return currType;
	}

	public int getScore()
	{
		return score;
	}

	public int getCounter()
	{
		return counter;
	}

	public boolean getGameOver()
	{
		return gameover;
	}
	public void setGameOver() {
		gameover = true;
	}
}