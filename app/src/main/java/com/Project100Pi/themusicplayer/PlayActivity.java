package com.Project100Pi.themusicplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.michaelevans.colorart.library.ColorArt;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

public class PlayActivity extends Activity implements OnSeekBarChangeListener{
    SeekBar seekbar;

    int mediaPos,mediaMax;
    long nowPlayingId;
    int albumArtSize = 0;
    TextView runningTime = null;
    int screenWidth ;
    int screenHeight ;
    Context mcontext;
    ActionBar actionBar;


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        //Here you can get the size!


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_play);

        runningTime = (TextView) findViewById(R.id.runningTime);
        actionBar = getActionBar();
        setActionBarUIData();


        mcontext = getApplicationContext();

        //Toast.makeText(getApplicationContext(), width+" "+height, Toast.LENGTH_LONG).show();
        // albumArtSize = width;
        boolean hasMenuKey = ViewConfiguration.get(getApplicationContext()).hasPermanentMenuKey();
        // Toast.makeText(getApplicationContext(), height+" ", Toast.LENGTH_LONG).show();

        //   Toast.makeText(getApplicationContext(), thisLayout.getLayoutParams().height+" ", Toast.LENGTH_LONG).show();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        setScreenWidthAndHeight(size);

        final RelativeLayout playLayout = (RelativeLayout) findViewById(R.id.playNamesContainer);
        playLayout.requestLayout();

        final RelativeLayout thisLayout = (RelativeLayout) findViewById(R.id.playOuterContainer);
        thisLayout.requestLayout();



        int bottomPlaySize = thisLayout.getHeight() + playLayout.getHeight();
        albumArtSize = screenHeight - bottomPlaySize;
		   /*
		   if(bottomPlaySize < screenHeight /3){
			   int increment = thisLayout.getMeasuredHeight()/10;
			thisLayout.setPadding(0,increment , 0, increment);
			setPlayingLayout();
		   }
		   */
        seekbar = (SeekBar)findViewById(R.id.seekBar); // make seekbar object
        seekbar.setOnSeekBarChangeListener(this);
        seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {



            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
                MainActivity.handler.removeCallbacks(moveSeekBarThread);

            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                MainActivity.handler.removeCallbacks(moveSeekBarThread);
                int totalDuration = MainActivity.mp.getDuration();
                //  int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

                // forward or backward to certain seconds
                MainActivity.mp.seekTo(seekBar.getProgress());

                // update timer progress again

                MainActivity.handler.postDelayed(moveSeekBarThread, 100);

            }


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                runningTime.setText(UtilFunctions.convertSecondsToHMmSs(seekBar.getProgress()));


            }
        });
        Intent intent = getIntent();
        if(intent.getExtras().getString("do").equals("Play")){
            getAndSetCurrPlaySongInfoFromIntent(intent);
            if(songInfoObj.shuffled){
                long seed = System.nanoTime();
                Collections.shuffle(songInfoObj.nowPlayingList, new Random(seed));
            }

            nowPlayingId=Long.parseLong(songInfoObj.nowPlayingList.get(songInfoObj.currPlayPos));

            try{
                MainActivity.isSongPlaying = true;
                audioPlayer((String)setPlaySongInfo(nowPlayingId),1);
            }catch(IOException e){
                e.printStackTrace();
            }



        }else if(intent.getExtras().getString("do").equals("watch")){

            setPlaySongInfo(songInfoObj.songId);
            if(!MainActivity.isSongPlaying && (MainActivity.mp.getDuration()!=Integer.parseInt(songInfoObj.duration))){
                try {
                    audioPlayer(songInfoObj.playPath, songInfoObj.playerPostion);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            MainActivity.handler.postDelayed(moveSeekBarThread, 100); //cal the thread after 100 milliseconds

        }



        final PlayPauseView view = (PlayPauseView) findViewById(R.id.playPauseView);
        if(!MainActivity.isSongPlaying){
            view.toggle();
        }
        Toast.makeText(getApplicationContext(),view.isPlay()+"",Toast.LENGTH_LONG).show();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.isSongPlaying){
                    pauseMusicPlayer();
                } else {
                    startMusicPlayer(); // Check this code once..Check the Method implementation once with Bala's Code
                }
                view.toggle();
            }
        });

        ImageView next = (ImageView) findViewById(R.id.nextImage);
        final Drawable nextIcon = getResources().getDrawable(R.drawable.next);
        nextIcon.setColorFilter(Color.parseColor("#2F5D53"), PorterDuff.Mode.SRC_ATOP);
        next.setImageDrawable(nextIcon);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                nextAction();
            }
        });

        final ImageView prev = (ImageView) findViewById(R.id.previousImage);
        final Drawable prevIcon = getResources().getDrawable(R.drawable.previous);
        prevIcon.setColorFilter(Color.parseColor("#2F5D53"), PorterDuff.Mode.SRC_ATOP);
        prev.setImageDrawable(prevIcon);
        prev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	/*
            	Drawable myIcon = getResources().getDrawable(R.drawable.previous);
            	myIcon.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
            	prev.setImageDrawable(myIcon);
 				*/

                if(MainActivity.mp.getCurrentPosition()>5000){
                    MainActivity.mp.reset();
                }
                else if(songInfoObj.currPlayPos <=0 ){
                    songInfoObj.currPlayPos = songInfoObj.nowPlayingList.size()-1;
                }else{
                    songInfoObj.currPlayPos = songInfoObj.currPlayPos-1;
                }
                try {
                    MainActivity.mp.reset();
                    audioPlayer((String)setPlaySongInfo(Long.parseLong(songInfoObj.nowPlayingList.get(songInfoObj.currPlayPos))),1);
                    MainActivity.handler.postDelayed(moveSeekBarThread, 100);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });

        final ImageView repeat = (ImageView) findViewById(R.id.repeatImage);
        final Drawable repeatIcon = getResources().getDrawable(R.drawable.ic_action_playback_repeat);
        final Drawable repeatIcon_1 = getResources().getDrawable(R.drawable.ic_action_playback_repeat_1);
        if(songInfoObj.isRepeat == 0){
            repeatIcon.setColorFilter(Color.parseColor("#2F5D53"), PorterDuff.Mode.SRC_ATOP);
            repeat.setImageDrawable(repeatIcon);
        }else if(songInfoObj.isRepeat == 1){
            repeatIcon.setColorFilter(Color.MAGENTA, PorterDuff.Mode.SRC_ATOP);
            repeat.setImageDrawable(repeatIcon);
        }else{
            repeatIcon_1.setColorFilter(Color.MAGENTA, PorterDuff.Mode.SRC_ATOP);
            repeat.setImageDrawable(repeatIcon_1);
        }
        repeat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                if(songInfoObj.isRepeat == 0){
                    repeatIcon.setColorFilter(Color.MAGENTA, PorterDuff.Mode.SRC_ATOP);
                    repeat.setImageDrawable(repeatIcon);
                    songInfoObj.isRepeat = 1;
                    Toast.makeText(getApplicationContext(), "Repeat ON", Toast.LENGTH_LONG).show();
                }else if(songInfoObj.isRepeat == 1){
                    repeatIcon_1.setColorFilter(Color.MAGENTA, PorterDuff.Mode.SRC_ATOP);
                    repeat.setImageDrawable(repeatIcon_1);
                    songInfoObj.isRepeat =2;
                    Toast.makeText(getApplicationContext(), "Single Repeat ON", Toast.LENGTH_LONG).show();
                }else{
                    repeatIcon.setColorFilter(Color.parseColor("#2F5D53"), PorterDuff.Mode.SRC_ATOP);
                    repeat.setImageDrawable(repeatIcon);
                    songInfoObj.isRepeat=0;
                    Toast.makeText(getApplicationContext(), "Repeat OFF", Toast.LENGTH_LONG).show();
                }

            }
        });



        final ImageView shuffle=(ImageView)findViewById(R.id.shuffleImage);
        final Drawable shuffleIcon = getResources().getDrawable(R.drawable.shuffle);
        if(songInfoObj.shuffled){
            shuffleIcon.setColorFilter(Color.MAGENTA, PorterDuff.Mode.SRC_ATOP);
            shuffle.setImageDrawable(shuffleIcon);
        } else{
            shuffleIcon.setColorFilter(Color.parseColor("#2F5D53"), PorterDuff.Mode.SRC_ATOP);
            shuffle.setImageDrawable(shuffleIcon);
        }
        shuffle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(!songInfoObj.shuffled){
                    Toast.makeText(getApplicationContext(), "Shuffle ON ", Toast.LENGTH_LONG).show();
                    long seed = System.nanoTime();
                    Collections.shuffle(songInfoObj.nowPlayingList, new Random(seed));
                    songInfoObj.currPlayPos = songInfoObj.nowPlayingList.indexOf(songInfoObj.songId.toString());
                    songInfoObj.shuffled = true;
                    shuffleIcon.setColorFilter(Color.MAGENTA, PorterDuff.Mode.SRC_ATOP);
                    shuffle.setImageDrawable(shuffleIcon);
                } else{
                    Toast.makeText(getApplicationContext(), "Shuffle OFF ", Toast.LENGTH_LONG).show();
                    songInfoObj.nowPlayingList.clear();
                    songInfoObj.nowPlayingList.addAll(songInfoObj.initialPlayingList);
                    songInfoObj.currPlayPos = songInfoObj.nowPlayingList.indexOf(songInfoObj.songId.toString());
                    songInfoObj.shuffled = false;
                    shuffleIcon.setColorFilter(Color.parseColor("#2F5D53"), PorterDuff.Mode.SRC_ATOP);
                    shuffle.setImageDrawable(shuffleIcon);
                }
            }
        });





    }
    /**
     * The Move seek bar. Thread to move seekbar based on the current position
     * of the song
     */
    Runnable moveSeekBarThread = new Runnable() {
        public void run() {

            Integer mediaPos_new = MainActivity.mp.getCurrentPosition();
            int mediaMax_new = MainActivity.mp.getDuration();
            songInfoObj.playerPostion = mediaPos_new;
            seekbar.setMax(mediaMax_new);
            seekbar.setProgress(mediaPos_new);
            if(MainActivity.mp.isPlaying()){
                runningTime.setText(UtilFunctions.convertSecondsToHMmSs(mediaPos_new));
            }
            MainActivity.handler.postDelayed(this, 1000); //Looping the thread after 1 second
            // seconds


        }
    };



    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        switch (id) {
            case R.id.nowPlayingListImage:
                Intent intent=new Intent(PlayActivity.this,NowPlayingListTest.class);
                // intent.putExtra("art",(Serializable)songInfoObj.nowPlayingList);
                intent.putExtra("position", songInfoObj.currPlayPos);
                startActivity(intent);

                break;
            case R.id.timerImage:
                Toast.makeText(getApplicationContext(), "Timer ON",Toast.LENGTH_SHORT).show();
                new CountDownTimer(30000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        //Toa.setText("seconds remaining: " + millisUntilFinished / 1000);
                    }

                    public void onFinish() {
                        // mTextField.setText("done!");
                        Toast.makeText(getApplicationContext(), "Timer over",Toast.LENGTH_SHORT).show();
                        MainActivity.mp.pause();
                    }
                }.start();
                break;

            case R.id.equalizerImage:
                intent=new Intent(PlayActivity.this,EqualizerSettings.class);
                // intent.putExtra("art",(Serializable)songInfoObj.nowPlayingList);
                intent.putExtra("position", songInfoObj.currPlayPos);
                startActivity(intent);

                break;

            default:
                break;
        }

        return true;
    }

    public void audioPlayer(String path,final int playerPos) throws IOException{
        if (MainActivity.mp != null) {
            MainActivity.mp.reset();
        }

        MainActivity.mp.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                //mp.reset();
                MainActivity.handler.postDelayed(moveSeekBarThread, 100);
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
            MainActivity.mp.setDataSource(path);
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
            MainActivity.mp.prepare();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        MainActivity.mp.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                MainActivity.mp.seekTo(playerPos);
                if(MainActivity.isSongPlaying){
                    MainActivity.mp.start();
                    floatingLyricIntent(getApplicationContext(), (long)playerPos, true);
                }


            }
        });


        mediaPos =MainActivity.mp.getCurrentPosition();
        mediaMax = MainActivity.mp.getDuration();

        seekbar.setMax(mediaMax); // Set the Maximum range of the
        seekbar.setProgress(mediaPos);// set current progress to song's

        MainActivity.handler.removeCallbacks(moveSeekBarThread);
        MainActivity.handler.postDelayed(moveSeekBarThread, 100); //cal the thread after 100 milliseconds

    }


    public static String getPlaySongInfo(Context context,Long songId){
        Cursor cursor = CursorClass.playSongCursor(context,songId);
        setMainActivityCurrsongInfoFromCursor(context, songId, cursor);
        return songInfoObj.playPath;

    }

    //This method will also be used in the MainActivity for the same purpose
   public static void setMainActivityCurrsongInfoFromCursor(Context context, Long songId, Cursor cursor) {
        cursor.moveToFirst();
        songInfoObj.songName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
        songInfoObj.playPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
        songInfoObj.artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
        songInfoObj.album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
        songInfoObj.duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
        songInfoObj.songId = songId;
        songInfoObj.bitmap = CursorClass.albumArtCursor(context, cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
    }

    public String setPlaySongInfo(Long songId){
        getPlaySongInfo(getApplicationContext(),songId);
        setPlayingLayout();
			/*
			try{
			 ColorArt colorArt = new ColorArt(bitmap);
			playSongName.setTextColor( colorArt.getPrimaryColor());
			playArtistName.setTextColor(colorArt.getSecondaryColor());
			} catch(Exception e){}
			*/
        MainActivity.handler.postDelayed(moveSeekBarThread, 100);
        //  Toast.makeText(getApplicationContext(), playPath+"  "+artist+"  "+album+"  "+duration,
        //		   Toast.LENGTH_LONG).show();


        return songInfoObj.playPath;


    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            UtilFunctions.savePreference(getApplicationContext());
        }catch(Exception e){
            Log.i("shared preference", "save failed");
        }
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        setPlaySongInfo(songInfoObj.songId);
    }

    private void setPlayingLayout(){

        ImageView albumArtView =(ImageView) findViewById(R.id.albumArt);
        albumArtView.requestLayout();
        albumArtView.getLayoutParams().width = albumArtSize;
        albumArtView.getLayoutParams().height = albumArtSize;
        albumArtView.setImageBitmap(songInfoObj.bitmap);

        TextView playAlbumName = (TextView) findViewById(R.id.playAlbumName);
        playAlbumName.setText(songInfoObj.album);
        TextView playSongName = (TextView) findViewById(R.id.playSongName);
        playSongName.setText(songInfoObj.songName);
        playSongName.setSelected(true);
        TextView playArtistName = (TextView) findViewById(R.id.playArtistName);
        playArtistName.setText(songInfoObj.artist);
        playArtistName.setSelected(true);
        TextView fullPlayingTime = (TextView) findViewById(R.id.fullPlayTIme);
        fullPlayingTime.setText(UtilFunctions.convertSecondsToHMmSs(Long.parseLong(songInfoObj.duration)));
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

    public void nextAction(){
        MainActivity.mp.reset();
        songInfoObj.currPlayPos = (songInfoObj.currPlayPos+1) % songInfoObj.nowPlayingList.size();
        try {
            audioPlayer((String)setPlaySongInfo(Long.parseLong(songInfoObj.nowPlayingList.get(songInfoObj.currPlayPos))),1);
            MainActivity.handler.postDelayed(moveSeekBarThread, 100);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void swipeCurrPlayAction(){
        MainActivity.mp.reset();
        songInfoObj.currPlayPos = (songInfoObj.currPlayPos) % songInfoObj.nowPlayingList.size();
        try {
            audioPlayer((String)setPlaySongInfo(Long.parseLong(songInfoObj.nowPlayingList.get(songInfoObj.currPlayPos))),1);
            MainActivity.handler.postDelayed(moveSeekBarThread, 100);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void setActionBarUIData()
    {
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33000000")));
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#55000000")));
    }

    public void pauseMusicPlayer()
    {
        MainActivity.mp.pause();
        MainActivity.isSongPlaying = false;
        PlayActivity.floatingLyricIntent(getApplicationContext(), (long) MainActivity.mp.getCurrentPosition(), false);
    }

    public void startMusicPlayer()
    {
        MainActivity.mp.start();
        MainActivity.isSongPlaying = true;
        PlayActivity.floatingLyricIntent(getApplicationContext(), (long) MainActivity.mp.getCurrentPosition(), true);
    }

    public void setScreenWidthAndHeight(Point size) {
        screenWidth = size.x;
        screenHeight = size.y - getStatusBarHeight();
    }

    public void getAndSetCurrPlaySongInfoFromIntent(Intent i)
    {
        songInfoObj.currPlayPos = i.getExtras().getInt("position");
        songInfoObj.nowPlayingList = new ArrayList<String>();
        songInfoObj.initialPlayingList = new ArrayList<String>();
        songInfoObj.nowPlayingList = i.getStringArrayListExtra("playingList");
        songInfoObj.initialPlayingList.addAll(songInfoObj.nowPlayingList);
        songInfoObj.shuffled = i.getExtras().getBoolean("shuffle");
    }
}
