package com.Project100Pi.themusicplayer;

import android.R.string;
import android.provider.BaseColumns;

public class DbHelperContract {
	
	public DbHelperContract(){}
	
	public static abstract class DbColumns implements BaseColumns{
		
		public static final String TABLE_NAME = "AllSongsInfo";
		public static final String TITLE = "title";
		public static final String PLAY_COUNT = "playCount";
	}
}
