package com.alextoma.locationtracker;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class LocationService extends Service {

	String TAG = "LocationService";

	LocationManager locationManager;

	private final LocationListener locationListener = new LocationListener() {

		@Override
		public void onLocationChanged(final Location location) {
			Log.v(TAG + "Latitude", String.valueOf(location.getLatitude()));
			Log.v(TAG + "Longitude", String.valueOf(location.getLongitude()));

			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					ConnectivityManager network = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo info[] = network.getAllNetworkInfo();
					for (int i = 0; i < info.length; i++) {
						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							try {
								URL url = new URL(LocationTracker.requestURL);
								HttpURLConnection con = (HttpURLConnection) url.openConnection();
								con.setDoOutput(true);
								con.setRequestMethod("POST");
								
								StringBuilder urlParameters = new StringBuilder();
								urlParameters.append("auth_key=" + LocationTracker.authKey + '&');
								urlParameters.append("latitude=" + location.getLatitude() + '&');
								urlParameters.append("longitude=" + location.getLongitude());
								
								DataOutputStream out = new DataOutputStream(con.getOutputStream());
								out.writeBytes(urlParameters.toString());
								out.flush();
								out.close();

								int responseCode = con.getResponseCode();

								BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

								StringBuffer resultBuffer = new StringBuffer();
								String line = "";
								while ((line = in.readLine()) != null) {
									resultBuffer.append(line);
								}
								in.close();

								Log.d(TAG + " responseCode", String.valueOf(responseCode));
								Log.d(TAG + " result", resultBuffer.toString());

							} catch (MalformedURLException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
							// Don't execute this for every active connection
							break;
						} else {
							Log.d(TAG, "No internet connection from " + info[i].getTypeName());
						}
					}
				}
			});
			t.start();
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.d(TAG, provider + " disabled");
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.d(TAG, provider + " enabled");
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}
	};

	public void onStart(Intent intent, int startId) {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LocationTracker.timeLapse, LocationTracker.minDistance, locationListener);
		Log.d(TAG, " Service started");
	}

	@Override
	public void onDestroy() {
		locationManager.removeUpdates(locationListener);
		Log.d(TAG, " Service stopped");
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
