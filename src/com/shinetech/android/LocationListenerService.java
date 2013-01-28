package com.shinetech.android;

import java.util.List;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LocationListenerService extends Service implements
		LocationListener {

	public static final int UPDATE_MESSAGE = 1;
	public static final String PROXIMTY_ALERT_INTENT = "com.shinetech.android.PROXIMTY_ALERT";
	public static final String PREFERNCES_CHANGED_INTENT = "com.shinetech.android.PREFERENCES_CHANGED";

	private static final String TAG = LocationListenerService.class.toString();
	private LocationManager locationManager;
	private LocationDbAdapter dbAdapter;

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "Received intent " + intent.toString());
			if (PREFERNCES_CHANGED_INTENT.equals(intent.getAction())) {
				requestLocationUpdates();
				addProximityAlert();
			} else if (PROXIMTY_ALERT_INTENT.equals(intent.getAction())) {
				handleProximityAlert(intent);
			}
		}

		private void handleProximityAlert(Intent intent) {
			Location location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location == null) {
				location = locationManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if (location == null) {
					// We'll just write an empty location
					location = new Location("prx");
				}
			}

			if (intent.hasExtra(LocationManager.KEY_PROXIMITY_ENTERING)) {
				if (intent.getBooleanExtra(
						LocationManager.KEY_PROXIMITY_ENTERING, true) == true) {
					location.setProvider("prx_enter");
				} else {
					location.setProvider("prx_exit");
				}
			}
			addLocationToDB(location);
		}
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "onStartCommand()");
		super.onStartCommand(intent, flags, startId);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate()");
		dbAdapter = new LocationDbAdapter(this);
		dbAdapter.open();

		requestLocationUpdates();
		addProximityAlert();

		// TODO Normally this is declared in the Manifest
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.shinetech.android.PREFERENCES_CHANGED");
		this.registerReceiver(this.broadcastReceiver, intentFilter);

	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");
		super.onDestroy();
		dbAdapter.close();
		locationManager.removeUpdates(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onLocationChanged(Location location) {
		addLocationToDB(location);
	}

	private void addLocationToDB(Location location) {
		Log.i(TAG, "Location changed: " + location.toString());
		dbAdapter.addLocation(location);
		Intent intent = new Intent("com.shinetech.android.UPDATE_UI");
		sendBroadcast(intent);
		Log.i(TAG, "UPDATE_UI intent broadcasted");
	}

	public void onProviderEnabled(String provider) {
		Log.i(TAG, "Provider " + provider + " is now enabled.");
	}

	public void onProviderDisabled(String provider) {
		Log.i(TAG, "Provider " + provider + " is now disabled.");
	}

	public void onStatusChanged(String provider, int i, Bundle b) {
		Log.i(TAG, "Provider " + provider + " has changed status to " + i);
	}

	private void requestLocationUpdates() {
		int sampleDistance = ((LocationMapperApplication) getApplication())
				.getPreferences().getSampleDistance();
		int sampleInterval = ((LocationMapperApplication) getApplication())
				.getPreferences().getSampleInterval() * 1000 * 60;
		Log.i(TAG, "Setting up location updates with sample distance "
				+ sampleDistance + " m and sample interval " + sampleInterval
				+ " ms.");

		this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.removeUpdates(this);
		List<String> enabledProviders = this.locationManager.getProviders(true);
		for (String provider : enabledProviders) {
			Log.i(TAG, "Requesting location updates from provider " + provider);
			this.locationManager.requestLocationUpdates(provider,
					sampleInterval, sampleDistance, this);
		}
	}

	private void addProximityAlert() {
		Log.i(TAG, "addProximityAlert()");
		int vicinityRadius = ((LocationMapperApplication) getApplication())
				.getPreferences().getVicinityRadius();
		double latitude = ((LocationMapperApplication) getApplication())
				.getPreferences().getLocation().getLatitude();
		double longitude = ((LocationMapperApplication) getApplication())
				.getPreferences().getLocation().getLongitude();

		long expiration = -1;

		Intent intent = new Intent(PROXIMTY_ALERT_INTENT);
		PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 0,
				intent, 0);

		locationManager.addProximityAlert(latitude, longitude, vicinityRadius,
				expiration, proximityIntent);

		IntentFilter filter = new IntentFilter(PROXIMTY_ALERT_INTENT);
		registerReceiver(this.broadcastReceiver, filter);
	}

}
