package com.link;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.security.*;

import com.connectfour.Connect;

public class HelloAndroid extends Activity {
	private static final String TAG = HelloAndroid.class.getSimpleName();
	private static final String AUTHCODE = "cos333"; // authentication code to send to PHP for verification of source
	private final String loginurl = "http://webscript.princeton.edu/~pcao/cos333/dologin.php";
	private final String registerurl = "http://webscript.princeton.edu/~pcao/cos333/doregister.php";
	private final String updateactivityurl = "http://webscript.princeton.edu/~pcao/cos333/updateactivity.php";
	private final String getlobbyurl = "http://webscript.princeton.edu/~pcao/cos333/getlobby.php";
	
	private static final String md5salt = "cos333-salt";
	
	private String netid = "";
	
	private boolean ready = false;
	private boolean ready2 = false;
	private boolean gameStart = false;
	
	private Connect connect = new Connect();
	private RefreshHandler mRefreshHandler = new RefreshHandler();
	
	private ArrayList<User> userList; // for lobby
	
	// registration parameters
	private int passMinLength = 4, passMaxLength = 20;
	private int netidMinLength = 4, netidMaxLength = 16;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    // encode password using md5
    private String encodePassword(String pass) {
    	byte[] bytesOfMessage;
    	String unencodedpass = md5salt + pass;
    	String encodedpass = "";
    	try {
    		bytesOfMessage = unencodedpass.getBytes("UTF-8");
    		MessageDigest md = MessageDigest.getInstance("MD5");
    		byte[] digest = md.digest(bytesOfMessage);
    		StringBuffer sb = new StringBuffer();
    		for (int i = 0; i < digest.length; ++i) {
    			sb.append(Integer.toHexString((digest[i] & 0xFF) | 0x100).substring(1,3));
    		}
    		encodedpass = sb.toString();
    	} catch (UnsupportedEncodingException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	} catch (NoSuchAlgorithmException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	return encodedpass;
    }
    
    // called when login button is clicked
    public void doLogin(View v) {
    	
    	final EditText netidEdit = (EditText) findViewById(R.id.netidEntry);
        final EditText passwordEdit = (EditText) findViewById(R.id.passwordEntry);
        String netidIn = netidEdit.getText().toString();
        String pwordIn = passwordEdit.getText().toString();
        
        LoginViaPHP task = new LoginViaPHP();
		task.execute(new String[] { loginurl, netidIn, pwordIn });
    }
    public void backtologin(View v) {
    	setContentView(R.layout.main);
    }
    private class LoginViaPHP extends AsyncTask<String, String, String[]> {
    	@Override
    	// check login credentials and returns true if login successful
    	protected String[] doInBackground(String... params) {
    		String loginurl;
    		String netidIn;
    		String pwordIn;
    		
    		String[] result = new String[2];
    		// get url/login/password from params
    		try {
	    		loginurl = params[0];
	    		netidIn = params[1];
	    		pwordIn = params[2];
	    		PasswordChecker checkpass = new PasswordChecker(passMinLength, passMaxLength);
	    		PasswordChecker checknetid = new PasswordChecker(netidMinLength, netidMaxLength);
	            if(!checkpass.check(pwordIn) || !checknetid.check(netidIn)) {
	                //Toast.makeText(HelloAndroid.this, "invalid netID or password", Toast.LENGTH_SHORT).show();
	                result[0] = "invalid netid/password";
	    			return result;
	            }
	            pwordIn = encodePassword(pwordIn);
	    		result[1] = netidIn; 
    		} catch (Exception e) {
    			e.printStackTrace();
    			result[0] = "error";
    			return result;
    		}
    		//System.out.println("attempting to login with: " + netidIn + ", " + pwordIn);
    		
    		// set up login/password to be posted to PHP
    		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    		nameValuePairs.add(new BasicNameValuePair("netid", netidIn));
    		nameValuePairs.add(new BasicNameValuePair("pword", pwordIn));
    		nameValuePairs.add(new BasicNameValuePair("auth", AUTHCODE));
    		
    		System.out.println("attempting to login with: " + netidIn + ", " + pwordIn);
    		
    		InputStream content;
    		
    		// try getting http response
    		try {
    			// TODO: check for https functionality
    	        HttpClient httpclient = new DefaultHttpClient();
    	        HttpPost httppost = new HttpPost(loginurl);	        
    	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    	        HttpResponse response = httpclient.execute(httppost);
    	        HttpEntity entity = response.getEntity();
    	        content = entity.getContent();
    	    } catch(Exception e){
    	        Log.e("log_tag","Error in internet connection " + e.toString());
    	        result[0] = "error";
    			return result;
    	    }
    		System.out.println("post successful");
    		
    		// try reading http response
    		String output = "";
    		try {
    			BufferedReader reader = new BufferedReader(new InputStreamReader(content,"iso-8859-1"), 8);
    	        String line;
    	        while((line = reader.readLine()) != null){
    	            output += line;
    	        }
    	        content.close();
    		} catch(Exception e){
    	        Log.e("log_tag", "Error converting result " + e.toString());
    	        result[0] = "error";
    			return result;
    	    }
    		System.out.println(output);
    		result[0] = output;
			return result;
    	}
    	@Override
    	// process result of login query (true or false)
    	protected void onPostExecute(String results[]) {
    		final String loginsuccess = "yes"; // output from PHP to match
    		//final String loginfailure = "error";
    		String loginresult = results[0];
    		String netid = results[1];
    		if (loginresult.equals(loginsuccess)) {
    			setNetid(netid);
    			loggedIn();
    		} else {
    			final TextView loginstatustxt = (TextView) findViewById(R.id.loginstatustxt);
    			loginstatustxt.setText("Login failed! Please try again or register.");
    			loginstatustxt.setTextColor(Color.RED);
    		}
    	}
    }

    private class RegisterViaPHP extends AsyncTask<String, String, String[]> {
    	protected String[] doInBackground(String... params) {
    		String registerurl;
    		String netidIn;
    		String pwordIn;
    		String emailIn;
    		
    		String[] result = new String[2];    		
    		
    		// get url/login/password from params
    		try {
	    		registerurl = params[0];
	    		netidIn = params[1];
	    		pwordIn = encodePassword(params[2]);
	    		emailIn = params[3];
	    		
	    		result[1] = netidIn;
    		} catch (Exception e) {
    			e.printStackTrace();
    			result[0] = "error";
    			return result;
    		}
    		
    		
    		// set up login/password to be posted to PHP
    		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    		nameValuePairs.add(new BasicNameValuePair("netid", netidIn));
    		nameValuePairs.add(new BasicNameValuePair("pword", pwordIn));
    		nameValuePairs.add(new BasicNameValuePair("email", emailIn));
    		nameValuePairs.add(new BasicNameValuePair("auth", AUTHCODE));
    		System.out.println("attempting to register with: " + netidIn + ", " + pwordIn + ", " + emailIn);
    		InputStream content;
    		
    		// try getting http response
    		try {
    			// TODO: check for https functionality
    	        HttpClient httpclient = new DefaultHttpClient();
    	        HttpPost httppost = new HttpPost(registerurl);	        
    	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    	        HttpResponse response = httpclient.execute(httppost);
    	        HttpEntity entity = response.getEntity();
    	        content = entity.getContent();
    	    } catch(Exception e){
    	        Log.e("log_tag","Error in internet connection " + e.toString());
    	        result[0] = "error";
    			return result;
    	    }
    		//System.out.println("post successful");
    		
    		// try reading http response
    		String output = "";
    		try {
    			BufferedReader reader = new BufferedReader(new InputStreamReader(content,"iso-8859-1"), 8);
    	        String line;
    	        while((line = reader.readLine()) != null){
    	            output += line;
    	        }
    	        content.close();
    		} catch(Exception e){
    	        Log.e("log_tag", "Error converting result " + e.toString());
    	        result[0] = "error";
    			return result;
    	    }
    		//Log.e("log_tag", "output: " + output);
    		result[0] = output;
			return result;
    	}
    	protected void onPostExecute(String results[]) {
    		final String registersuccess = "yes";
    		//final String registerfailure = "error";
    		String registerresult = results[0];
    		String netid = results[1];
    		if (registerresult.equals(registersuccess)) {
    			setNetid(netid);
    			loggedIn();
    		} else {
    			final TextView registerstatustxt = (TextView) findViewById(R.id.registerstatustxt);
    			registerstatustxt.setText("Registration failed! Username already exists.");
    			registerstatustxt.setTextColor(Color.RED);
    		}
    	}
    }
    
    private void setActivity(String newActivity) {
		UpdateActivityViaPHP task = new UpdateActivityViaPHP();
		task.execute(new String[] { updateactivityurl, netid, newActivity, getLocalIpAddress() }); // set activity to in lobby
	}
    private class UpdateActivityViaPHP extends AsyncTask<String, String, String[]> {
    	protected String[] doInBackground(String... params) {
    		String updateactivityurl;
    		String netidIn;
    		String activityIn;
    		String phoneipIn;
    		
    		String[] result = new String[2];
    		
    		
    		// get url/login/password from params
    		try {
	    		updateactivityurl = params[0];
	    		netidIn = params[1];
	    		activityIn = params[2];
	    		phoneipIn = params[3];
	    		result[1] = activityIn;
    		} catch (Exception e) {
    			e.printStackTrace();
    			result[0] = "error";
    			return result;
    		}
    		System.out.println("attempting to update activity with: " + netidIn + ", " + activityIn + ", " + phoneipIn);
    		
    		// set up login/password to be posted to PHP
    		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    		nameValuePairs.add(new BasicNameValuePair("netid", netidIn));
    		nameValuePairs.add(new BasicNameValuePair("activity", activityIn));
    		nameValuePairs.add(new BasicNameValuePair("phoneip", phoneipIn));
    		nameValuePairs.add(new BasicNameValuePair("auth", AUTHCODE));
    		
    		InputStream content;
    		
    		// try getting http response
    		try {
    			// TODO: check for https functionality
    	        HttpClient httpclient = new DefaultHttpClient();
    	        HttpPost httppost = new HttpPost(updateactivityurl);	        
    	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    	        HttpResponse response = httpclient.execute(httppost);
    	        HttpEntity entity = response.getEntity();
    	        content = entity.getContent();
    	    } catch(Exception e){
    	        Log.e("log_tag","Error in internet connection " + e.toString());
    	        result[0] = "error";
    			return result;
    	    }
    		System.out.println("post successful");
    		
    		// try reading http response
    		String output = "";
    		try {
    			BufferedReader reader = new BufferedReader(new InputStreamReader(content,"iso-8859-1"), 8);
    	        String line;
    	        while((line = reader.readLine()) != null){
    	            output += line;
    	        }
    	        content.close();
    		} catch(Exception e){
    	        Log.e("log_tag", "Error converting result " + e.toString());
    	        result[0] = "error";
    			return result;
    	    }
    		Log.e("log_tag", "output: " + output);
    		result[0] = output;
			return result;
    	}
    	protected void onPostExecute(String results[]) {
    		final String updatesuccess = "yes";
    		//final String updatefailure = "error";
    		String updateresult = results[0];
    		String activity = results[1];
    		if (updateresult.equals(updatesuccess)) {
    			// success
    			System.out.println("updated activity: " + activity);
    		} else {
    			// failure
    			System.out.println("failed to update activity: " + activity);
    		}
    	}
    }
    
    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("get ip error", ex.toString());
        }
        return null;
    }
    public String getMyNetid() {
    	return netid;
    }

    
    private class GetLobbyViaPHP extends AsyncTask<String, String, String[]> {
    	protected String[] doInBackground(String... params) {
    		String getlobbyurl;
    		
    		String[] result = new String[] { "error", "" }; // to be returned
    		
    		try {
    			getlobbyurl = params[0];
    			
    		} catch (Exception e) {
    			e.printStackTrace();
    			result[0] = "error";
    			return result;
    		}

    		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    		nameValuePairs.add(new BasicNameValuePair("auth", AUTHCODE));
    		nameValuePairs.add(new BasicNameValuePair("netid", netid));
    		
    		InputStream content;
    		
    		// try getting http response
    		try {
    			// TODO: check for https functionality
    	        HttpClient httpclient = new DefaultHttpClient();
    	        HttpPost httppost = new HttpPost(getlobbyurl);	        
    	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    	        HttpResponse response = httpclient.execute(httppost);
    	        HttpEntity entity = response.getEntity();
    	        content = entity.getContent();
    	    } catch(Exception e){
    	        Log.e("log_tag","Error in internet connection " + e.toString());
    	        result[0] = "error";
    			return result;
    	    }
    		
    		// try reading http response
    		String output = "";
    		try {
    			BufferedReader reader = new BufferedReader(new InputStreamReader(content,"utf-8"), 8);
    	        String line;
    	        while((line = reader.readLine()) != null){
    	            output += line + "\n";
    	        }
    	        content.close();
    		} catch(Exception e){
    	        Log.e("log_tag", "Error converting result " + e.toString());
    	        result[0] = "error";
    			return result;
    	    }
    		Log.e("log_tag", "output: " + output);
    		result[0] = "yes";
    		result[1] = output;
    		return result;
    	}
    	protected void onPostExecute(String results[]) { // print lobby results
    		final String lobbyfailure = "error";
    		String lobbyresult = results[0];
    		String lobbytext = results[1];
    		System.out.println("got lobby: " + lobbytext);
    		
    		String[] lobbydata = lobbytext.split(";");
    		String[] netid = new String[lobbydata.length/3];
    		String[] status = new String[lobbydata.length/3];
    		String[] ip = new String[lobbydata.length/3];
    		for (int i = 0; i < lobbydata.length/3; i++)
    		{
    			netid[i] = lobbydata[i*3];
    			status[i] = lobbydata[i*3+1];
    			ip[i] = lobbydata[i*3+2];
    		}
    		userList = new ArrayList<User>();

            for (int i = 0; i < netid.length; i++) {
                userList.add(new User(netid[i], status[i], ip[i]));
            }
    		
    		
    		if (!lobbyresult.equals(lobbyfailure)) {
    			// success    			
    			ListView lv_lobby = (ListView) findViewById(R.id.lv_lobby);
    			lv_lobby.setAdapter(new UserAdapter(getApplicationContext(), R.layout.lobby_item, userList));
    			lv_lobby.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                        final int position, long id) {
                       
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                       
                        View pview = inflater.inflate(R.layout.lobby_connect, null, false);
                        final PopupWindow pw = new PopupWindow(pview,200,200, true);
                        pw.setBackgroundDrawable(new BitmapDrawable());
                        pw.showAtLocation(findViewById(R.id.lv_lobby), Gravity.CENTER, 0, 0);
                     
                        Button challenge = (Button) pview.findViewById(R.id.lobby_challenge);
                        challenge.setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                System.out.println("position: " + position);
                                User u = userList.get(position);
                                System.out.println("user: " + u.getName() + " status: " + u.getStatus() + " ip: " + u.getIP());
                                // TODO: fix challenge
                		        Intent myIntent = new Intent(HelloAndroid.this, MultiplayerLinker.class);
                		        myIntent.putExtra("netid", getMyNetid());
                		        myIntent.putExtra("opponentip", ""); // TODO: fix this
                		        HelloAndroid.this.startActivityForResult(myIntent, -2);
                		        pw.dismiss();
                            }
                        });
                       
                        Button cancel = (Button) pview.findViewById(R.id.lobby_cancel);
                        cancel.setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                pw.dismiss();
                            }
                        });
                    }
                  });
    		} else {
    			// failure
    			final TextView tv_lobby = (TextView) findViewById(R.id.tv_lobby);
    			tv_lobby.setText("Unable to connect to lobby.");
    			tv_lobby.setTextColor(Color.RED);
    		}
    		
    	}
    }
    
    // set up the lobby and start screen after logging in
    private void loggedIn() {
    	// enables you to receive challenges
		connect.initServer();
		ready = false; ready2 = false;
		gameStart = false;
		mRefreshHandler.sleep(50);
		
        setActivity("In Lobby");
		
		setContentView(R.layout.loggedin);
    	final TextView welcomeuser = (TextView) findViewById(R.id.welcomeuser);
        welcomeuser.setText("Welcome, " + netid + "!");
        
        GetLobbyViaPHP getlobby = new GetLobbyViaPHP();
		getlobby.execute(new String[] { getlobbyurl });
        
        final Button startgames = (Button) findViewById(R.id.startgames);
        startgames.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
		        Intent myIntent = new Intent(HelloAndroid.this, Linker.class);
		        myIntent.putExtra("netid", netid);
		        HelloAndroid.this.startActivityForResult(myIntent, -1);
		    }
		});
        
        final Button refreshlobby = (Button) findViewById(R.id.refreshlobby);
        refreshlobby.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				GetLobbyViaPHP getlobby = new GetLobbyViaPHP();
				getlobby.execute(new String[] { getlobbyurl });
			}
		});
        
        final Button logoutbtn = (Button) findViewById(R.id.logout);
        logoutbtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				loggedOut();		
			}
		});
    }
    // return to lobby when linker activity ends
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			loggedIn();
       }
	}
    
    // called when register button is clicked
    public void doRegister(View v) {
    	final EditText netidEdit = (EditText) findViewById(R.id.netidEntry);
        final EditText passwordEdit = (EditText) findViewById(R.id.passwordEntry);
        final EditText confirmEdit = (EditText) findViewById(R.id.confirmEntry);
        final EditText emailEdit = (EditText) findViewById(R.id.emailEntry);
        String netidIn = netidEdit.getText().toString();
        String pwordIn = passwordEdit.getText().toString();
        String confirmIn = confirmEdit.getText().toString();
        String emailIn = emailEdit.getText().toString();
        
        final TextView registerstatustxt = (TextView) findViewById(R.id.registerstatustxt);
        
        if (!pwordIn.equals(confirmIn)) { // check that passwords match
        	registerstatustxt.setText("Passwords do not match!");
        	registerstatustxt.setTextColor(Color.RED);
        	return;
        }
        EmailChecker ec = new EmailChecker();
        PasswordChecker checkpass = new PasswordChecker(passMinLength, passMaxLength);
		PasswordChecker checknetid = new PasswordChecker(netidMinLength, netidMaxLength);
        if(!checknetid.check(netidIn)) {
        	registerstatustxt.setText("Invalid netid.");
        	registerstatustxt.setTextColor(Color.RED);
        	return;
        } else if (!checkpass.check(pwordIn)) {
        	registerstatustxt.setText("Invalid password.");
        	registerstatustxt.setTextColor(Color.RED);
        	return;
        } else if (!ec.check(emailIn)) {
            registerstatustxt.setText("Invalid e-mail address.");
        	registerstatustxt.setTextColor(Color.RED);
            return;
        }
        RegisterViaPHP task = new RegisterViaPHP();
		task.execute(new String[] { registerurl, netidIn, pwordIn, emailIn });
    }
    
    public void gotoregister(View v) {
    	final EditText netidFromLogin = (EditText) findViewById(R.id.netidEntry);
        final EditText passwordFromLogin = (EditText) findViewById(R.id.passwordEntry);
        String netidIn = netidFromLogin.getText().toString();
        String pwordIn = passwordFromLogin.getText().toString();
    	setContentView(R.layout.register);
    	final EditText netidFromRegister = (EditText) findViewById(R.id.netidEntry);
        final EditText passwordFromRegister = (EditText) findViewById(R.id.passwordEntry);
        netidFromRegister.setText(netidIn);
        passwordFromRegister.setText(pwordIn);
    }
    
    public void setNetid(String netid) { // set this upon successful login/registration
		this.netid = netid;
	}
    public void loggedOut() {
    	netid = "";
    	setActivity("Offline");
    	setContentView(R.layout.main);
    }
    protected void onDestroy() {
    	Log.d(TAG, "destroying...");
    	super.onDestroy();
    }
    protected void onStop() {
    	Log.d(TAG, "stopping...");
    	super.onStop();
    }
    
    class RefreshHandler extends Handler {

    	@Override
    	public void handleMessage(Message msg) {
//    		connect.sendMsgFromServer(" ");
  
			if (ready && ready2 && !gameStart)
			{
				gameStart = true;

				//start activity for server side of the game (challenged person)
				
//				setContentView(R.layout.game);
//
//				myConnectFour = (ConnectFourView) (findViewById(R.id.game));
//				myConnectFour.setTextView((TextView) findViewById(R.id.status));
////				checkButton = (Button) findViewById(R.id.check_button);
////				checkButton.setOnClickListener(click);
//				
//				myConnectFour.setTurn(true);
//				myConnectFour.setText("Game Start. Your move.");
				
//				checker();
//				mRefreshHandler.sleep(50);
			}

    	}

    	public void sleep(long delayMillis) {
    		this.removeMessages(0);
    		sendMessageDelayed(obtainMessage(0), delayMillis);
    	}
    };
    
    private void accept ()
    {
		if (!ready)
		{
			ready = true;
			connect.sendMsgFromServer("ready");
		}
		
		if (!ready2)
		{
			String input = "";
			input = connect.getMsgToServer();

			if (input == null) {
				Log.d("Error", "Null");
			} else if (input.equals("" + com.link.Linker.CONNECT_ID)) {
				ready2 = true;
				setActivity("Playing Connect Four");
		        Intent myIntent = new Intent(HelloAndroid.this, com.connectfour.ConnectFourMainServer.class);
		        HelloAndroid.this.startActivityForResult(myIntent, com.link.Linker.CONNECT_ID);
			}
		}

    }
    class User {
        private String username;
        private String status;
        private String ipaddress;

        public String getName() {
            return username;
        }
        public String getIP() {
            return ipaddress;
        }
        public void setIP(String newip) {
            ipaddress = newip;
        }
        public void setName(String name) {
            username = name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public User(String name, String status, String newip) {
            username = name;
            this.status = status;
            ipaddress = newip;
        }
    }
    
    public class UserAdapter extends ArrayAdapter<User> {
        private ArrayList<User> items;
        private UserViewHolder userHolder;

        private class UserViewHolder {
            TextView name;
            TextView status; 
        }

        public UserAdapter(Context context, int tvResId, ArrayList<User> items) {
            super(context, tvResId, items);
            this.items = items;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.lobby_item, null);
                userHolder = new UserViewHolder();
                userHolder.name = (TextView)v.findViewById(R.id.lobby_name);
                userHolder.status = (TextView)v.findViewById(R.id.lobby_status);
                v.setTag(userHolder);
            } else userHolder = (UserViewHolder)v.getTag(); 

            User user = items.get(pos);

            if (user != null) {
                userHolder.name.setText(user.getName());
                userHolder.status.setText(user.getStatus());
            }

            return v;
        }
    }
}