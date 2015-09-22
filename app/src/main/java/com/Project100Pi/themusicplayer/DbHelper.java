package com.Project100Pi.themusicplayer;

import com.Project100Pi.themusicplayer.DbHelperContract.DbColumns;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
	   // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "AllSongsInfo";
 
    private static final String CREATE_MY_CLIPS_TABLE = "CREATE TABLE IF NOT EXISTS " + DbColumns.TABLE_NAME + "("+DbColumns.TITLE+" TEXT PRIMARY KEY ,"+DbColumns.PLAY_COUNT+" INTEGER DEFAULT 0)";

	
	public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        
}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	        db.execSQL(CREATE_MY_CLIPS_TABLE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public void insertSongTitle(String title) {
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(DbColumns.TITLE, title);
	    // Inserting Row
	    db.insert(DbColumns.TABLE_NAME, null, values);
	    db.close(); // Closing database connection
	}

}

