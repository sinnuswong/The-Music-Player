package com.Project100Pi.themusicplayer;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class songInfoObj {
	static String songName,playPath,artist,album,albumArtPath;
	static Long songId;
	static int playerPostion,duration;
	static Bitmap bitmap;
	static ArrayList<String> nowPlayingList = new ArrayList<String>();
	static ArrayList<String> initialPlayingList = new ArrayList<String>();
	static int currPlayPos;
	static boolean shuffled;
	static int isRepeat;
	
}
