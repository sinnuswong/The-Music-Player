package com.Project100Pi.themusicplayer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.SeekBar;

import java.io.IOException;

/**
 * Created by BalachandranAR on 9/25/2015.
 */
public class PlayHelperFunctions {

    static Context mContext;
    static SeekBar seekbar;
    static MediaPlayer mp = new MediaPlayer();
    static Handler handler = new Handler();
    static Boolean isSongPlaying =  false;
    static int mediaPos,mediaMax;

    public static String getPlaySongInfo(Long songId){
        Cursor cursor = CursorClass.playSongCursor(songId);
        setMainActivityCurrsongInfoFromCursor(songId, cursor);
        return songInfoObj.playPath;

    }

    //This method will also be used in the MainActivity for the same purpose
    public static void setMainActivityCurrsongInfoFromCursor(Long songId, Cursor cursor) {
        cursor.moveToFirst();
        songInfoObj.songName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
        songInfoObj.playPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
        songInfoObj.artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
        songInfoObj.album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
        songInfoObj.duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
        songInfoObj.songId = songId;
        songInfoObj.bitmap = CursorClass.albumArtCursor(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
    }

    public static String setPlaySongInfo(Long songId){
        getPlaySongInfo(songId);
      //  setPlayingLayout();
			/*
			try{
			 ColorArt colorArt = new ColorArt(bitmap);
			playSongName.setTextColor( colorArt.getPrimaryColor());
			playArtistName.setTextColor(colorArt.getSecondaryColor());
			} catch(Exception e){}
			*/
        handler.postDelayed(moveSeekBarThread, 100);
        //  Toast.makeText(getApplicationContext(), playPath+"  "+artist+"  "+album+"  "+duration,
        //		   Toast.LENGTH_LONG).show();


        return songInfoObj.playPath;


    }

    static Runnable moveSeekBarThread = new Runnable() {
        public void run() {

            Integer mediaPos_new = mp.getCurrentPosition();
            int mediaMax_new = mp.getDuration();
            songInfoObj.playerPostion = mediaPos_new;
            seekbar.setMax(mediaMax_new);
            seekbar.setProgress(mediaPos_new);
            if(mp.isPlaying()){
                //  runningTime.setText(UtilFunctions.convertSecondsToHMmSs(mediaPos_new));
            }
            handler.postDelayed(this, 1000); //Looping the thread after 1 second
            // seconds


        }
    };

    public static void audioPlayer(String path,final int playerPos) throws IOException {
        if (mp != null) {
            mp.reset();
        }

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                //mp.reset();
                handler.postDelayed(moveSeekBarThread, 100);
                if(songInfoObj.isRepeat ==0){
                    songInfoObj.currPlayPos = (songInfoObj.currPlayPos+1);
                    if(songInfoObj.currPlayPos == songInfoObj.nowPlayingList.size()){

                        songInfoObj.currPlayPos--;
                        return;
                    }
                }else if(songInfoObj.isRepeat == 1){
                    songInfoObj.currPlayPos = (songInfoObj.currPlayPos+1)%songInfoObj.nowPlayingList.size();

                }


                try {
                    mp.reset();
                    audioPlayer((String)setPlaySongInfo(Long.parseLong(songInfoObj.nowPlayingList.get(songInfoObj.currPlayPos))),1);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                }


            }

        });

        try {
            // mp.setDataSource();
            mp.setDataSource(path);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            //mp.reset();
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            mp.prepare();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.seekTo(playerPos);
                if(isSongPlaying){
                    mp.start();
                    floatingLyricIntent(mContext, (long)playerPos, true);
                }


            }
        });


        mediaPos =mp.getCurrentPosition();
        mediaMax = mp.getDuration();

        seekbar.setMax(mediaMax); // Set the Maximum range of the
        seekbar.setProgress(mediaPos);// set current progress to song's

        handler.removeCallbacks(moveSeekBarThread);
        handler.postDelayed(moveSeekBarThread, 100); //cal the thread after 100 milliseconds

    }

    public static void floatingLyricIntent(Context context,long currDuration,Boolean isPlaying){

        Intent intent = new Intent();
        intent.setAction("com.android.music.metachanged");
        Bundle bundle = new Bundle();

        // put the song's metadata
        bundle.putString("track", songInfoObj.songName);
        bundle.putString("artist", songInfoObj.artist);
        bundle.putString("album", songInfoObj.album);

        // put the song's total duration (in ms)
        bundle.putLong("duration", Long.parseLong(songInfoObj.duration)); // 4:05

        // put the song's current position
        bundle.putLong("position", currDuration); // beginning of the song

        // put the playback status
        bundle.putBoolean("playing", isPlaying); // currently playing

        // put your application's package
        bundle.putString("scrobbling_source", "com.Project100Pi.themusicplayer");

        intent.putExtras(bundle);
        context.sendBroadcast(intent);
    }

}