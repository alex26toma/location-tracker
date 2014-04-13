package com.alextoma.locationtracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LocationTracker extends Activity {

	Button bStartService, bStopService;
	EditText etURL, etTimeLapse, etMinDistance, etAuthKey;
	TextView tvServicRunning;
	
	public static String requestURL = "No_Value";
	public static String authKey = "No_Value";
	public static int timeLapse = -1;
	public static int minDistance = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_tracker);
		
		bStartService = (Button) findViewById(R.id.bStartService);
		bStopService = (Button) findViewById(R.id.bStopService);
		
		etURL = (EditText) findViewById(R.id.etURL);
		etTimeLapse = (EditText) findViewById(R.id.etTimeLapse);
		etMinDistance = (EditText) findViewById(R.id.etMinDistance);
		etAuthKey = (EditText) findViewById(R.id.etAuthenticationKey);
		
		tvServicRunning = (TextView) findViewById(R.id.tvServiceRunning);
		
		final Intent serviceIntent = new Intent(this, LocationService.class);
		
		bStartService.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				requestURL = etURL.getText().toString();
				timeLapse = Integer.valueOf(etTimeLapse.getText().toString().isEmpty() ? "-1" : etTimeLapse.getText().toString());
				minDistance = Integer.valueOf(etMinDistance.getText().toString().isEmpty() ? "-1" : etMinDistance.getText().toString());
				authKey = etAuthKey.getText().toString();
				
				String error;
				if((error = checkUserInput(requestURL, timeLapse, minDistance, authKey)).equals("No error")) {
					startService(serviceIntent);
					Toast.makeText(getApplicationContext(), "The service has been started", Toast.LENGTH_SHORT).show();
				}
				else
					Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
			}
		});
		
		bStopService.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				stopService(serviceIntent);
				Toast.makeText(getApplicationContext(), "The service has been stopped", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private String checkUserInput(String url, int time, int dist, String key) {
		StringBuilder error = new StringBuilder();
		boolean noError = true;
		
		if(url.isEmpty()) {
			error.append("Please insert an URL");
			noError = false;
		}
		if(time <= 0) {
			error.append("\nThe Time Lapse must be greater then 0");
			noError = false;
		}
		if(dist <=0) {
			error.append("\nThe Min Distance must be greater then 0");
			noError = false;
		}
		if(key.isEmpty()) {
			error.append("\nPlease insert an Authentication Key");
			noError = false;
		}
			
		if(noError)
			error.append("No error");
		
		return error.toString();
	}

}
