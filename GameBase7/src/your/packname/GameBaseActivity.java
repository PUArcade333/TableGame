package your.packname;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.*;


public class GameBaseActivity extends Activity {
    /** Called when the activity is first created. */
	String s = "testing";
	GameState g;
	boolean netOn = false;
	boolean netStatePicked = false;
	boolean isServer = false;
	boolean isClient = false;
	Connect connect; 
	String ip;
	boolean isYourTurn = false;
	int playernum;
	int p1pic = R.drawable.p1button;
	int p2pic = R.drawable.p2button;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.introlayout);
       
        g = new GameState();
        connect = new Connect();
        ip = "client";
        
        
        
        final EditText edittext = (EditText) findViewById(R.id.edittext);
        edittext.setOnKeyListener(new EditText.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER && !netStatePicked)) {//and !netStatePicked
                  // Perform action on key press
                  Toast.makeText(GameBaseActivity.this, edittext.getText(), Toast.LENGTH_SHORT).show();
                  ip = edittext.getText().toString();
                  return true;
                }
                return false;
            }
        });
    }
    
    public void onButtonClicked(View v) {
        
    	System.out.println(g.string());
    	
    	if(g.board.isGameOver()) {
    		
    		//doesnt handle if you make the other guy win
			Toast.makeText(GameBaseActivity.this, g.board.winner() + " is the winner" ,Toast.LENGTH_SHORT).show();
			return;
		}
    	
    	if(!isYourTurn) {
    		Toast.makeText(GameBaseActivity.this, "its not your turn" ,Toast.LENGTH_SHORT).show();
    		if(isServer){
    			String state = connect.getMsgToServer();
    			if(state != null) {
    				g = new GameState(state);
    				g.playerturn = playernum;
    				Toast.makeText(GameBaseActivity.this, "Now it is" ,Toast.LENGTH_SHORT).show();
    				isYourTurn = true;
    			}
    		}
    		if(isClient){
    			String state = connect.getMsgToClient();
    			if(state != null) {
    				g = new GameState(state);
    				g.playerturn = playernum;
    				Toast.makeText(GameBaseActivity.this, "Now it is" ,Toast.LENGTH_SHORT).show();
    				isYourTurn = true;
    			}
    		}
    			
    		if(isYourTurn) {
    			GridView gridview = (GridView) findViewById(R.id.gridview);
    	    	for(int i = 0; i < 16; i++) {
    	    		GameTile tile = (GameTile) gridview.findViewById(i);
    	    		if(g.board.tables[i/4].table[i%4] == 1) tile.setImageResource(p1pic);
    	    		if(g.board.tables[i/4].table[i%4] == 2) tile.setImageResource(p2pic);
    	    	}
    		}
    		return;
    	}
    	
    	
    	
		
    	if(g.board.isGameOver()) Toast.makeText(GameBaseActivity.this, "clicked" ,Toast.LENGTH_SHORT).show();
    	
		int choiceA = -1;
		int choiceB = -1;
		int validMove = 0;
		
		if(g.choice1 != -1) choiceA = g.choice1;
		else if(g.choice2 != -1) choiceA = g.choice2;
		else Toast.makeText(GameBaseActivity.this, "pick something" ,Toast.LENGTH_SHORT).show();
		
		if(g.choice1 != -1 && g.choice2 != -1) choiceB = g.choice2;
		       		
		//add
		if(g.nUnused == 1) {
			g.board.add(choiceA, g.playerturn);
			System.out.println(g.board.tables[choiceA/4].table[choiceA%4]);
			g.used[choiceA/4][choiceA%4] = true;
			validMove = 1;
		}
		     
		//capture
		if(g.nUsed == 1 && g.numKicks[g.playerturn] < 3
				&& g.playerturn != g.board.tables[choiceA/4].table[choiceA%4]) {
			g.board.capture(choiceA);
			validMove = 1;
			g.numKicks[g.playerturn]++;
		}
		
		//swap
		if(g.nUsed == 2 && (g.board.tables[choiceA/4].table[choiceA%4] 
		                  != g.board.tables[choiceB/4].table[choiceB%4])
		                  && !samePair(g, choiceA, choiceB)) {
			g.board.swap(choiceA, choiceB);
			validMove = 1;
			g.lastPair[0] = choiceA;
			g.lastPair[1] = choiceB;
		}
		
		if(validMove == 0) Toast.makeText(GameBaseActivity.this, "not a valid move " + choiceA + " " + choiceB ,Toast.LENGTH_SHORT).show();
		else {
			Toast.makeText(GameBaseActivity.this, (g.numTurns+1) + " Turn Comleted by " + g.playerturn, Toast.LENGTH_SHORT).show();
			if(g.playerturn == 1) g.playerturn = 2;
			else if(g.playerturn == 2) g.playerturn = 1;
			g.choice1 = -1;
			g.choice2 = -1;
			g.kickmove = false;
			g.nUnused = 0;
			g.nUsed = 0;
			g.numTurns++;
			if(choiceA != -1) g.selected[choiceA] = false;
			if(choiceB != -1) g.selected[choiceB] = false;
			//Toast.makeText(GameBaseActivity.this, g.numTurns + "Turn Comleted by " + g.playerturn, Toast.LENGTH_SHORT).show();
		}
		
		//System.out.println(g.getString());
		if(netOn) {
			if(isServer) {
				connect.sendMsgFromServer(g.string());
				isYourTurn = false;
			}
			if(isClient) {
				connect.sendMsgFromClient(g.string());
				isYourTurn = false;
			}
		}
		
		//doesnt handle if you make the other guy win
		if(g.board.isGameOver()) Toast.makeText(GameBaseActivity.this, g.board.winner() + " is the winner" ,Toast.LENGTH_SHORT).show();
		
    }
    
    public boolean samePair(GameState game, int a, int b) {
		return ((a == g.lastPair[0] || a == g.lastPair[1]) && (b == g.lastPair[0] || b == g.lastPair[1]));
    }
    
    public void onButtonClickeda(View v) {
    	Toast.makeText(GameBaseActivity.this, "a pressed", Toast.LENGTH_SHORT).show();
    	if(!netStatePicked) {
    		v.setBackgroundResource(R.drawable.android_pressed);
        	netStatePicked = true;
        	isServer = true;
        	netOn = true;
        	playernum = 1;
        	isYourTurn = true;
        	connect.initServer();
        	ip = connect.getServerIP();
        	setContentView(R.layout.mainlayout);
        	GridView gridview = (GridView) findViewById(R.id.gridview);
            gridview.setAdapter(new TileAdapter(this));

            gridview.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
             
                	GameTile gt = (GameTile) v;
                	gt.setState(g);
                	gt.setIndex(position);
                	g = gt.clickLogic();
                }
            });
    	}
    	else { Toast.makeText(GameBaseActivity.this, "server state picked: " + ip, Toast.LENGTH_SHORT).show();}
    	
    }
    
    public void onButtonClickedb(View v) {
    	Toast.makeText(GameBaseActivity.this, "b pressed", Toast.LENGTH_SHORT).show();
    	if(!netStatePicked) {
    		v.setBackgroundResource(R.drawable.android_pressed);
        	netStatePicked = true;
        	isClient = true;
        	netOn = true;
        	playernum = 2;
        	//g.playerTurn = playernum
        	connect.initClient(ip);
        	setContentView(R.layout.mainlayout);
        	GridView gridview = (GridView) findViewById(R.id.gridview);
            gridview.setAdapter(new TileAdapter(this));

            gridview.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
             
                	GameTile gt = (GameTile) v;
                	gt.setState(g);
                	gt.setIndex(position);
                	g = gt.clickLogic();
                }
            });
    	}
    	else { Toast.makeText(GameBaseActivity.this, "server state picked", Toast.LENGTH_SHORT).show();}
    	
    }
    
    public void onButtonClickedc(View v) {
    	netStatePicked = true;
    	ip = "no net";
    	isYourTurn = true;
    	setContentView(R.layout.mainlayout);
    	GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new TileAdapter(this));

        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
         
            	GameTile gt = (GameTile) v;
            	gt.setState(g);
            	gt.setIndex(position);
            	g = gt.clickLogic();
            }
        });
    }
    
}