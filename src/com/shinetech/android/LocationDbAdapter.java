package com.shinetech.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

public class LocationDbAdapter {

    public static final String KEY_NAME = "name";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_ACCURACY = "accuracy";
    public static final String KEY_TIME = "time";
    public static final String KEY_ROWID = "_id";

    private static final String TAG = "LocationDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_CREATE =
        "create table locations (_id integer primary key autoincrement, "
        + "name text not null, latitude real not null, longitude real not null, accuracy integer not null, time integer not null);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "locations";
    private static final int DATABASE_VERSION = 4;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS locations");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public LocationDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public LocationDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }
 

    /**
     * Add new location. If the location is
     * successfully created return the new rowId, otherwise return
     * a -1 to indicate failure.
     * 
     * @param location
     * @return rowId or -1 if failed
     */
    public long addLocation(Location location) {
    	Log.i(TAG, "Writing new location:"+location.toString());
    	ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NAME, location.getProvider());
        contentValues.put(KEY_LATITUDE, location.getLatitude());
        contentValues.put(KEY_LONGITUDE, location.getLongitude());
        contentValues.put(KEY_ACCURACY, location.getAccuracy());
        contentValues.put(KEY_TIME, location.getTime());

        return mDb.insert(DATABASE_TABLE, null, contentValues);
    }

    public int clearDatabase() {
    	Log.i(TAG, "clearDatabase()");
    	return mDb.delete(DATABASE_TABLE, null, null);
    }
    
    public Cursor fetchAllLocations() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_LATITUDE, KEY_LONGITUDE, KEY_ACCURACY,
                KEY_TIME}, null, null, null, null, null);
    }
}
