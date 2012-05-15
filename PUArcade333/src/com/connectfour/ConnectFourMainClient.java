package com.connectfour;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.link.*;

public class ConnectFourMainClient extends Activity implements OnGestureListener {

	Connect connect = new Connect();

	ConnectFourView myConnectFour;

	//	private int testCount = 0;

	TextView text;

	private Button serverButton;
	private Button clientButton;
	private Button connectButton;
	private Button cancelButton;
	private boolean serverSide = false;
	private boolean clientSide = false;
	private EditText serverIp;
	private boolean connected = false;

	private String opponentip = "";

	private boolean gameStart = false;
	private boolean ready = false;
	private boolean ready2 = false;

	private boolean gameover = false;

	//	private Button checkButton;


	private GestureDetector gestureScanner;

	/**
	 * Create a simple handler that we can use to cause animation to happen. We
	 * set ourselves as a target and we can use the sleep() function to cause an
	 * update/invalidate to occur at a later date.
	 */
	private RefreshHandler mRefreshHandler = new RefreshHandler();

	class RefreshHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			myConnectFour.update();
			if(serverSide)
			{
				connect.sendMsgFromServer(" ");
			}
			if(clientSide)
			{
				connect.sendMsgFromClient(" ");
			}

			checker();

		}

		public void sleep(long delayMillis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent me) {
		return gestureScanner.onTouchEvent(me);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		return;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		return;
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {

		if(myConnectFour != null)
		{
			//			Log.d("turn", "" + myConnectFour.getTurn());

			if (myConnectFour.getTurn())
			{
				myConnectFour.update();
				if(myConnectFour.touch(e.getX(), e.getY()))
				{
					myConnectFour.setText("Waiting for opponent's move.");

					if (serverSide)
					{
						connect.sendMsgFromServer("" + myConnectFour.getLastX());
						connect.sendMsgFromServer("" + myConnectFour.getLastY());
					}
					if (clientSide)
					{
						connect.sendMsgFromClient("" + myConnectFour.getLastX());
						connect.sendMsgFromClient("" + myConnectFour.getLastY());
					}

					if(gameover = myConnectFour.getGameOver())
					{
						if (myConnectFour.getWinner() == myConnectFour.RED)
							myConnectFour.setText("Game Over: You win");
						else if (myConnectFour.getWinner() == myConnectFour.YELLOW)
							myConnectFour.setText("Game Over: You lose");
					}

					mRefreshHandler.sleep(50);

				}				
			}
			//			mRefreshHandler.sleep(50);

			//			else
			//				checker();
		}

		return true;
	}

	public void test(String input) {
		if (gameStart)
		{
			if (input != null)
			{
				if (serverSide)
				{
					int x = Integer.parseInt(input);
					String yString = " ";
					while (yString.equals(" "))
						yString = connect.getMsgToServer();

					int y = Integer.parseInt(yString);

					if (!myConnectFour.checkTile(x, y))
					{
						myConnectFour.setTileMarked(3, x, y);
						myConnectFour.setText("Your move.");
					}
				}
				else if (clientSide)
				{
					int x = Integer.parseInt(input);
					String yString = " ";
					while (yString.equals(" "))
						yString = connect.getMsgToClient();

					int y = Integer.parseInt(yString);

					if (!myConnectFour.checkTile(x, y))
					{
						myConnectFour.setTileMarked(3, x, y);
						myConnectFour.setText("Your move.");
					}
				}
			}
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		gestureScanner = new GestureDetector(this);

		// remove title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.client2);
		text = (TextView) findViewById(R.id.client_text);
		cancelButton = (Button) findViewById(R.id.client_cancel);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent resultIntent = new Intent();
		    	setResult(Activity.RESULT_OK, resultIntent);
		        finish();
			}
		});
		// get server ip to connect to
		Bundle extras = getIntent().getExtras();
		if(extras !=null) {
			opponentip = extras.getString("opponentip");
		}
		if (!connected) {
			text.setText("Attempting to connect...");

			if (!opponentip.equals(""))
			{
				if(connect.initClient(opponentip))
				{
					connected = true;
					clientSide = true;
					text.setText("Connected to: " + connect.getServerIP());

					//send challenge
					connect.sendMsgFromClient("" + com.link.Linker.CONNECT_ID);

					text.setText("Waiting for response to invitation");

					clientButton = (Button) findViewById(R.id.client_start);
					clientButton.setOnClickListener(check);

					
					// TODO: set onclick listener
				}
			}
			else
				text.setText("Client Initialization failed");
		}

		/*
		setContentView(R.layout.start);
		text = (TextView) findViewById(R.id.start_text);

		serverButton = (Button) findViewById(R.id.server);
		serverButton.setOnClickListener(serverClick);
		clientButton = (Button) findViewById(R.id.client);
		clientButton.setOnClickListener(clientClick);
		 */
	}




	private OnClickListener check = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!ready)
			{
				ready = true;

				if (serverSide)
					connect.sendMsgFromServer("ready");
				else if (clientSide)
					connect.sendMsgFromClient("ready");
			}

			if (!ready2)
			{
				String input = "";

				if (serverSide)
					input = connect.getMsgToServer();
				else if (clientSide)
					input = connect.getMsgToClient();

				if (input == null)
					Log.d("Error", "Null");
				else if (input.equals("ready"))
					ready2 = true;
			}

			if (ready && ready2 && !gameStart)
			{
				gameStart = true;

				setContentView(R.layout.game);

				myConnectFour = (ConnectFourView) (findViewById(R.id.game));
				myConnectFour.setTextView((TextView) findViewById(R.id.status));
				//				checkButton = (Button) findViewById(R.id.check_button);
				//				checkButton.setOnClickListener(click);

				if(serverSide)
				{
					myConnectFour.setTurn(true);
					myConnectFour.setText("Game Start. Your move.");
				}
				else
				{
					myConnectFour.setTurn(true);
					myConnectFour.update();
					myConnectFour.setTurn(false);
					myConnectFour.setText("Game Start. Waiting for opponent's move.");
					//					checker();
				}

				//				checker();
				mRefreshHandler.sleep(50);
			}
		}
	};

	//	private OnClickListener click = new OnClickListener() {
	//		@Override
	//		public void onClick(View v) {
	//			
	//			checker();
	//		}
	//	};

	private void checker()
	{
		if (gameStart && !myConnectFour.getTurn() && !gameover) {
			text.setText("Waiting for move...");

			String input = "";

			if (serverSide && connect != null)
			{
				if((input = connect.getMsgToServer()) != null)
				{
					if(!input.equals(" "))
					{
						test(input);
						myConnectFour.setTurn(true);
						myConnectFour.setText("Your move.");
						if(gameover = myConnectFour.getGameOver())
						{
							if (myConnectFour.getWinner() == myConnectFour.RED)
								myConnectFour.setText("Game Over: You win");
							else if (myConnectFour.getWinner() == myConnectFour.YELLOW)
								myConnectFour.setText("Game Over: You lose");
						}
					}
				}
			}
			else if (clientSide && connect != null)
			{
				if((input = connect.getMsgToClient()) != null)
				{
					if(!input.equals(" "))
					{
						test(input);
						myConnectFour.setTurn(true);
						myConnectFour.setText("Your move.");
						if(gameover = myConnectFour.getGameOver())
						{
							if (myConnectFour.getWinner() == myConnectFour.RED)
								myConnectFour.setText("Game Over: You win");
							else if (myConnectFour.getWinner() == myConnectFour.YELLOW)
								myConnectFour.setText("Game Over: You lose");
						}
					}
				}
			}
			mRefreshHandler.sleep(50);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		if (connect != null)
			connect.close();
		//System.exit(0);
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (connect != null)
			connect.close();
		//System.exit(0);
	}

}
