package com.shinetech.android;

import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

public class Preferences {

	private static final String TAG = Preferences.class.toString();

	private Location location = null;
	private int vicinityRadius;
	private int sampleInterval;
	private int sampleDistance;

	private SharedPreferences sharedPreferences;

	private Preferences() {
		// Private CTOR: Can't use this
	}

	public Preferences(SharedPreferences sharedPreferences) {
		this.sharedPreferences = sharedPreferences;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public int getVicinityRadius() {
		return vicinityRadius;
	}

	public void setVicinityRadius(int vicinityRadius) {
		this.vicinityRadius = vicinityRadius;
	}

	public int getSampleInterval() {
		return sampleInterval;
	}

	public void setSampleInterval(int sampleInterval) {
		this.sampleInterval = sampleInterval;
	}

	public int getSampleDistance() {
		return sampleDistance;
	}

	public void setSampleDistance(int sampleDistance) {
		this.sampleDistance = sampleDistance;
	}

	public void load() {
		Log.i(TAG, "load()");
		// Only fetch preferences if they exist
		this.location = new Location(sharedPreferences.getString("name", ""));
		// TODO: These are actually doubles not floats
		this.location.setLatitude(sharedPreferences.getFloat("latitude", 0f));
		this.location.setLongitude(sharedPreferences.getFloat("longitude", 0f));
		this.location.setAccuracy(sharedPreferences.getFloat("accuracy", 0f));
		this.location.setTime(sharedPreferences.getLong("time", 0l));

		this.vicinityRadius = sharedPreferences.getInt("vicinityradius", 0);
		this.sampleInterval = sharedPreferences.getInt("sampleinterval", 0);
		this.sampleDistance = sharedPreferences.getInt("sampledistance", 0);
	}

	public void store() {
		Log.i(TAG, "store()");
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("name", location.getProvider());
		editor.putFloat("latitude", (float) location.getLatitude());
		editor.putFloat("longitude", (float) location.getLongitude());
		editor.putFloat("accuracy", location.getAccuracy());
		editor.putLong("time", location.getTime());
		editor.putInt("vicinityradius", vicinityRadius);
		editor.putInt("sampleinterval", sampleInterval);
		editor.putInt("sampledistance", sampleDistance);

		editor.commit();
	}
}
