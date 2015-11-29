package com.Project100Pi.themusicplayer;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.SeekBar;

import java.io.IOException;
import java.util.List;

/**
 * Created by BalachandranAR on 9/25/2015.
 */
public class PlayHelperFunctions extends Service {

    static Context mContext;
    static SeekBar seekbar;
    static MediaPlayer mp = new MediaPlayer();
    static Handler handler = new Handler();
    static Boolean isSongPlaying =  false;
    static int mediaPos,mediaMax;
    static AudioManager audioManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mContext==null){
            Log.d("CONTEXT STATUS","Context is null");
            mContext=this;
        }
        CursorClass.mContext=this; // COntext references are throwing too much NPEs
        return START_STICKY;
    }

    public static String getPlaySongInfo(Long songId){
        Cursor cursor = CursorClass.playSongCursor(songId);
        setMainActivityCurrsongInfoFromCursor(songId, cursor);
        return songInfoObj.playPath;

    }

    public static Bitmap getBitmapFromSongId(Long songId){
        Cursor cursor = CursorClass.playSongCursor(songId);
        cursor.moveToFirst();
        Bitmap returnBitmap = CursorClass.albumArtCursor(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
        if(returnBitmap == null){
            returnBitmap = BitmapFactory.decodeResource(mContext.getResources(),
                    R.drawable.music_default);
        }
        return returnBitmap;
    }
    //This method will also be used in the MainActivity for the same purpose
    public static void setMainActivityCurrsongInfoFromCursor(Long songId, Cursor cursor) {
        cursor.moveToFirst();
        songInfoObj.songName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
        songInfoObj.playPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
        songInfoObj.artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
        songInfoObj.album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
        songInfoObj.duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
        songInfoObj.songId = songId;
        songInfoObj.bitmap = CursorClass.albumArtCursor(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
        if(songInfoObj.bitmap == null){
            songInfoObj.bitmap = BitmapFactory.decodeResource(mContext.getResources(),
                    R.drawable.music_default);
        }
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
            /* seekbar throwing exception when only widget is running.NPE..Should make it loosely decoupled*/
            if(seekbar!=null) {
                seekbar.setMax(mediaMax_new);
                seekbar.setProgress(mediaPos_new);
            }
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



        audioManager= (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        int result = audioManager.requestAudioFocus(mAudioFocusListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

        Log.d("AF REsult",String.valueOf(result));



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
                    audioPlayer((String) setPlaySongInfo(Long.parseLong(songInfoObj.nowPlayingList.get(songInfoObj.currPlayPos))), 1);
                    PlayActivity.shouldUpdateLayout = true;  // so,that now playing activity will update its UI
                    MainActivity.shouldUpdateLayout = true;
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

        /*
        If only widget Service is there, we should disable all the seekbar related code
                */
        if(seekbar!=null) {
            seekbar.setMax(mediaMax); // Set the Maximum range of the song  ..Note..What to do when there is no seekbar reference.
            seekbar.setProgress(mediaPos);// set current progress to song's
        }

        handler.removeCallbacks(moveSeekBarThread);
        handler.postDelayed(moveSeekBarThread, 100); //cal the thread after 100 milliseconds
        UtilFunctions.savePreference(mContext);
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
        bundle.putLong("duration",songInfoObj.duration); // 4:05

        // put the song's current position
        bundle.putLong("position", currDuration); // beginning of the song

        // put the playback status
        bundle.putBoolean("playing", isPlaying); // currently playing

        // put your application's package
        bundle.putString("scrobbling_source", "com.Project100Pi.themusicplayer");

        intent.putExtras(bundle);
        context.sendBroadcast(intent);
    }


    public static void pauseMusicPlayer()
    {
        PlayHelperFunctions.mp.pause();
        PlayHelperFunctions.isSongPlaying = false;
        PlayHelperFunctions.floatingLyricIntent(mContext, (long) PlayHelperFunctions.mp.getCurrentPosition(), false);
    }

    public static void startMusicPlayer()
    {
        PlayHelperFunctions.mp.start();
        PlayHelperFunctions.isSongPlaying = true;
        PlayHelperFunctions.floatingLyricIntent(mContext, (long) PlayHelperFunctions.mp.getCurrentPosition(), true);
    }


    public static void nextAction(){
        Log.d("Voila", "I am here in NextAction()");
        PlayHelperFunctions.mp.reset();
        songInfoObj.currPlayPos = (songInfoObj.currPlayPos+1) % songInfoObj.nowPlayingList.size();
        Log.d("   CURRPLAYPOSITION ","curr position album is "+songInfoObj.songName);
        try {
            PlayHelperFunctions.audioPlayer((String) PlayHelperFunctions.setPlaySongInfo(Long.parseLong(songInfoObj.nowPlayingList.get(songInfoObj.currPlayPos))),1);
            PlayHelperFunctions.handler.postDelayed(PlayHelperFunctions.moveSeekBarThread, 100);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e("EXCEPTION!!!!!","Exception is "+e);
            e.printStackTrace();
        }
    }

    public static boolean prevAction(){
        boolean shouldReveal = false;
        if(PlayHelperFunctions.mp.getCurrentPosition()>5000){
            PlayHelperFunctions.mp.reset();
        }
        else if(songInfoObj.currPlayPos <=0 ){
            songInfoObj.currPlayPos = songInfoObj.nowPlayingList.size()-1;
            shouldReveal = true;
        }else{
            songInfoObj.currPlayPos = songInfoObj.currPlayPos-1;
            shouldReveal = true;
        }
        try {
            PlayHelperFunctions.mp.reset();
            PlayHelperFunctions.audioPlayer((String) PlayHelperFunctions.setPlaySongInfo(Long.parseLong(songInfoObj.nowPlayingList.get(songInfoObj.currPlayPos))), 1);

            PlayHelperFunctions.handler.postDelayed(PlayHelperFunctions.moveSeekBarThread, 100);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return shouldReveal;
    }

    private  static AudioManager.OnAudioFocusChangeListener mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            // AudioFocus is a new feature: focus updates are made verbose on
            // purpose
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    mp.stop();
                    audioManager.abandonAudioFocus(mAudioFocusListener);


                    //stop();
                    Log.d("MUSIC PLAYER", "AudioFocus: received AUDIOFOCUS_LOSS");
//              mp.release();
//              mp = null;
                    break;

                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
//              if (mp.isPlaying())
                    mp.pause();

                    Log.d("MUSIC PLAYER", "AudioFocus: received AUDIOFOCUS_LOSS_TRANSIENT");

                    break;

                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                   // mp.setVolume(0.5f, 0.5f);
                    Log.d("MUSIC PLAYER", "AudioFocus: received AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                    break;

                case AudioManager.AUDIOFOCUS_GAIN:
                    mp.start();
                   // mp.setVolume(1.0f, 1.0f);
                    Log.d("MUSIC PLAYER", "AudioFocus: received AUDIOFOCUS_GAIN");
                    break;

                default:
                    Log.e("MUSIC PLAYER", "Unknown audio focus change code");
            }

        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static boolean isAppRunning(){
    ActivityManager activityManager = (ActivityManager) mContext.getSystemService( ACTIVITY_SERVICE );
    List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        final String packageName = mContext.getPackageName();
        Log.d("Package Name", "Name is "+packageName);
    for(int i = 0; i < procInfos.size(); i++){

        if(procInfos.get(i).processName.equals(packageName))
        {
            Log.e("Result APP RUNNING", "App is running - Doesn't need to reload");
            return true;
        }
        else
        {
            Log.e(" result APP RUNNING", "App is not running - Needs to reload");
        }
      }
        return false;
    }

    public static void loadFromPreferencesOnAppNotRunning(){
        if(isAppRunning()==false) {
            UtilFunctions.loadPreference(mContext);
            Log.d("PREFERENCES", "preferences loaded from shared preferences");
        }
    }


}