package com.Project100Pi.themusicplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.michaelevans.colorart.library.ColorArt;
import android.app.Activity;
import android.app.Dialog;
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
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
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

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

public class PlayActivity extends AppCompatActivity {


    long nowPlayingId;
    int albumArtSize = 0;
    TextView runningTime = null;
    int screenWidth;
    int screenHeight;
    Context mcontext;
    ActionBar actionBar;
    Toolbar mToolbar;
    ImageView albumArtView;
    boolean shouldReveal = true;
    private float x1,x2;
    static final int MIN_DISTANCE  = 200;
    static boolean isTimerOn = false;
    static CountDownTimer myCountDownTimer;
    PlayPauseView playPauseViewview;
    EditText hoursEdit,minsEdit,secsEdit;
    Dialog editDialog;


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        //Here you can get the size!


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        runningTime = (TextView) findViewById(R.id.runningTime);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        actionBar = getSupportActionBar();
        setActionBarUIData();


        mcontext = getApplicationContext();

        PlayHelperFunctions.seekbar = (SeekBar) findViewById(R.id.seekBar); // make PlayHelperFunctions.seekbar object
        PlayHelperFunctions.seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                //Toast.makeText(getApplicationContext(), "Started tracking PlayHelperFunctions.seekbar", Toast.LENGTH_SHORT).show();
                PlayHelperFunctions.handler.removeCallbacks(PlayHelperFunctions.moveSeekBarThread);

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                PlayHelperFunctions.handler.removeCallbacks(PlayHelperFunctions.moveSeekBarThread);
                int totalDuration = PlayHelperFunctions.mp.getDuration();
                //  int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

                // forward or backward to certain seconds
                PlayHelperFunctions.mp.seekTo(seekBar.getProgress());

                // update timer progress again

                PlayHelperFunctions.handler.postDelayed(PlayHelperFunctions.moveSeekBarThread, 100);

            }


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                runningTime.setText(UtilFunctions.convertSecondsToHMmSs(seekBar.getProgress()));


            }
        });
        Intent intent = getIntent();
        if (intent.getExtras().getString("do").equals("Play")) {
            getAndSetCurrPlaySongInfoFromIntent(intent);
            if (songInfoObj.shuffled) {
                long seed = System.nanoTime();
                Collections.shuffle(songInfoObj.nowPlayingList, new Random(seed));
            }

            nowPlayingId = Long.parseLong(songInfoObj.nowPlayingList.get(songInfoObj.currPlayPos));

            try {
                PlayHelperFunctions.isSongPlaying = true;
                PlayHelperFunctions.audioPlayer((String) PlayHelperFunctions.setPlaySongInfo(nowPlayingId), 1);
                setPlayingLayout();
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else if (intent.getExtras().getString("do").equals("watch")) {

            PlayHelperFunctions.setPlaySongInfo(songInfoObj.songId);
            setPlayingLayout();
            if (!PlayHelperFunctions.isSongPlaying && (PlayHelperFunctions.mp.getDuration() != songInfoObj.duration)) {
                try {
                    PlayHelperFunctions.audioPlayer(songInfoObj.playPath, songInfoObj.playerPostion);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            PlayHelperFunctions.handler.postDelayed(PlayHelperFunctions.moveSeekBarThread, 100); //cal the thread after 100 milliseconds

        }


        playPauseViewview = (PlayPauseView) findViewById(R.id.playPauseView);
        /*
        if (!PlayHelperFunctions.isSongPlaying) {
            view.toggle();
        }
        */
        // Toast.makeText(getApplicationContext(),view.isPlay()+"",Toast.LENGTH_LONG).show();
        playPauseViewview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PlayHelperFunctions.isSongPlaying) {
                    PlayHelperFunctions.pauseMusicPlayer();
                } else {
                    PlayHelperFunctions.startMusicPlayer(); // Check this code once..Check the Method implementation once with Bala's Code
                }
                playPauseViewview.toggle();
            }
        });

        ImageView next = (ImageView) findViewById(R.id.nextImage);
        final Drawable nextIcon = getResources().getDrawable(R.drawable.next);
        nextIcon.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        next.setImageDrawable(nextIcon);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                PlayHelperFunctions.nextAction();
                setPlayingLayout();
            }
        });

        final ImageView prev = (ImageView) findViewById(R.id.previousImage);
        final Drawable prevIcon = getResources().getDrawable(R.drawable.previous);
        prevIcon.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        prev.setImageDrawable(prevIcon);
        prev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(PlayHelperFunctions.prevAction()) {
                    setPlayingLayout();
                }

            }
        });

        final ImageView repeat = (ImageView) findViewById(R.id.repeatImage);
        final Drawable repeatIcon = getResources().getDrawable(R.drawable.ic_action_playback_repeat);
        final Drawable repeatIcon_1 = getResources().getDrawable(R.drawable.ic_action_playback_repeat_1);
        if (songInfoObj.isRepeat == 0) {
            repeatIcon.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
            repeat.setImageDrawable(repeatIcon);
        } else if (songInfoObj.isRepeat == 1) {
            repeatIcon.setColorFilter(Color.parseColor("#be4d56"), PorterDuff.Mode.SRC_ATOP);
            repeat.setImageDrawable(repeatIcon);
        } else {
            repeatIcon_1.setColorFilter(Color.parseColor("#be4d56"), PorterDuff.Mode.SRC_ATOP);
            repeat.setImageDrawable(repeatIcon_1);
        }
        repeat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                if (songInfoObj.isRepeat == 0) {
                    repeatIcon.setColorFilter(Color.parseColor("#be4d56"), PorterDuff.Mode.SRC_ATOP);
                    repeat.setImageDrawable(repeatIcon);
                    songInfoObj.isRepeat = 1;
                    Toast.makeText(getApplicationContext(), "Repeat ON", Toast.LENGTH_LONG).show();
                } else if (songInfoObj.isRepeat == 1) {
                    repeatIcon_1.setColorFilter(Color.parseColor("#be4d56"), PorterDuff.Mode.SRC_ATOP);
                    repeat.setImageDrawable(repeatIcon_1);
                    songInfoObj.isRepeat = 2;
                    Toast.makeText(getApplicationContext(), "Single Repeat ON", Toast.LENGTH_LONG).show();
                } else {
                    repeatIcon.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
                    repeat.setImageDrawable(repeatIcon);
                    songInfoObj.isRepeat = 0;
                    Toast.makeText(getApplicationContext(), "Repeat OFF", Toast.LENGTH_LONG).show();
                }

            }
        });


        final ImageView shuffle = (ImageView) findViewById(R.id.shuffleImage);
        final Drawable shuffleIcon = getResources().getDrawable(R.drawable.shuffle);
        if (songInfoObj.shuffled) {
            shuffleIcon.setColorFilter(Color.parseColor("#be4d56"), PorterDuff.Mode.SRC_ATOP);
            shuffle.setImageDrawable(shuffleIcon);
        } else {
            shuffleIcon.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
            shuffle.setImageDrawable(shuffleIcon);
        }
        shuffle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!songInfoObj.shuffled) {
                    Toast.makeText(getApplicationContext(), "Shuffle ON ", Toast.LENGTH_LONG).show();
                    long seed = System.nanoTime();
                    Collections.shuffle(songInfoObj.nowPlayingList, new Random(seed));
                    songInfoObj.currPlayPos = songInfoObj.nowPlayingList.indexOf(songInfoObj.songId.toString());
                    songInfoObj.shuffled = true;
                    shuffleIcon.setColorFilter(Color.parseColor("#be4d56"), PorterDuff.Mode.SRC_ATOP);
                    shuffle.setImageDrawable(shuffleIcon);
                } else {
                    Toast.makeText(getApplicationContext(), "Shuffle OFF ", Toast.LENGTH_LONG).show();
                    songInfoObj.nowPlayingList.clear();
                    songInfoObj.nowPlayingList.addAll(songInfoObj.initialPlayingList);
                    songInfoObj.currPlayPos = songInfoObj.nowPlayingList.indexOf(songInfoObj.songId.toString());
                    songInfoObj.shuffled = false;
                    shuffleIcon.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
                    shuffle.setImageDrawable(shuffleIcon);
                }
            }
        });


    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.play, menu);
        return true;
    }

    public void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
    }

    public void enableEditText(EditText editText){
        editText.setFocusable(true);
        editText.setEnabled(true);
        editText.setCursorVisible(true);
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
                Intent intent = new Intent(PlayActivity.this, NowPlayingListTest.class);
                // intent.putExtra("art",(Serializable)songInfoObj.nowPlayingList);
                intent.putExtra("position", songInfoObj.currPlayPos);
                startActivity(intent);

                break;
            case R.id.timerImage:
                editDialog = new Dialog(this);
                editDialog.setContentView(R.layout.timer_edit);
                editDialog.setTitle("Sleep Timer");
                final Button startStopEdit = (Button)editDialog.findViewById(R.id.start_edit);
                Button resetEdit = (Button)editDialog.findViewById(R.id.reset_edit);
                 hoursEdit = (EditText)editDialog.findViewById(R.id.hours_edit);
                 minsEdit = (EditText)editDialog.findViewById(R.id.mins_edit);
                 secsEdit = (EditText)editDialog.findViewById(R.id.secs_edit);
                if(isTimerOn){
                    disableEditText(hoursEdit);
                    disableEditText(minsEdit);
                    disableEditText(secsEdit);
                    startStopEdit.setText("STOP");
                }
                startStopEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isTimerOn) {
                            long secs, mins, hrs, countDownTime;
                            secs = Long.parseLong(secsEdit.getText().toString());
                            mins = Long.parseLong(minsEdit.getText().toString());
                            hrs = Long.parseLong(hoursEdit.getText().toString());
                            countDownTime = secs * 1000 + mins * 60000 + hrs * 3600000;
                            if (secs >= 0L && secs < 60L && mins >= 0L && mins < 60L && hrs >= 0L && hrs < 24L) {
                                if(countDownTime ==0L){
                                    Toast.makeText(getApplicationContext(), "Please Enter a valid time!", Toast.LENGTH_SHORT).show();
                                }else if (PlayHelperFunctions.isSongPlaying) {
                                    myCountDownTimer = new CountDownTimer(countDownTime, 100) {

                                        public void onTick(long millisUntilFinished) {
                                            //Toa.setText("seconds remaining: " + millisUntilFinished / 1000);
                                            if (isTimerOn) {
                                                int seconds = (int) (millisUntilFinished / 1000) % 60;
                                                int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                                                int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
                                                try {
                                                    secsEdit.setText(Integer.toString(seconds));
                                                    minsEdit.setText(Integer.toString(minutes));
                                                    hoursEdit.setText(Integer.toString(hours));
                                                } catch (Exception e) {

                                                }
                                            }
                                        }

                                        public void onFinish() {
                                            // mTextField.setText("done!");
                                            isTimerOn = false;
                                            if (PlayHelperFunctions.isSongPlaying) {
                                                PlayHelperFunctions.pauseMusicPlayer();
                                                Toast.makeText(getApplicationContext(), "Sleep Timer over", Toast.LENGTH_SHORT).show();
                                                try {
                                                    editDialog.dismiss();
                                                } catch (Exception e) {

                                                }
                                            }
                                        }
                                    }.start();
                                    isTimerOn = true;
                                    editDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Sleep Timer Set!", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                Toast.makeText(getApplicationContext(), "No song is playing!", Toast.LENGTH_SHORT).show();
                                editDialog.dismiss();
                            }
                        }else {
                            Toast.makeText(getApplicationContext(), "Please Enter a valid time!", Toast.LENGTH_SHORT).show();
                            }
                } else {
                //For stop Button
                isTimerOn = false;
                            myCountDownTimer.cancel();
                            startStopEdit.setText("START");
                        }
                    }
                });
                resetEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        enableEditText(hoursEdit);
                        enableEditText(minsEdit);
                        enableEditText(secsEdit);
                        hoursEdit.setText("00");
                        minsEdit.setText("00");
                        secsEdit.setText("00");
                        startStopEdit.setText("START");
                        isTimerOn = false;
                    }
                });
                editDialog.show();
                /*
                Toast.makeText(getApplicationContext(), "Timer ON", Toast.LENGTH_SHORT).show();
                new CountDownTimer(30000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        //Toa.setText("seconds remaining: " + millisUntilFinished / 1000);
                    }

                    public void onFinish() {
                        // mTextField.setText("done!");
                        Toast.makeText(getApplicationContext(), "Timer over", Toast.LENGTH_SHORT).show();
                        PlayHelperFunctions.mp.pause();
                    }
                }.start();
                */
                break;

            case R.id.equalizerImage:
                intent = new Intent(PlayActivity.this, EqualizerSettings.class);
                // intent.putExtra("art",(Serializable)songInfoObj.nowPlayingList);
                intent.putExtra("position", songInfoObj.currPlayPos);
                startActivity(intent);

                break;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                break;
        }

        return true;
    }


    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            UtilFunctions.savePreference(getApplicationContext());
        } catch (Exception e) {
            Log.i("shared preference", "save failed");
        }
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        PlayHelperFunctions.setPlaySongInfo(songInfoObj.songId);
        layoutInit();
        setPlayingLayout();
    }

    private void setPlayingLayout() {
        shouldReveal = true;
        albumArtView = (ImageView) findViewById(R.id.albumArt);
        //albumArtView.requestLayout();
        //albumArtView.getLayoutParams().width = albumArtSize;
        //albumArtView.getLayoutParams().height = albumArtSize;
        albumArtView.setImageBitmap(songInfoObj.bitmap);
        albumArtView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        Log.i("PANACHE DOWN", String.valueOf(x1));
                        break;
                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        Log.i("PANACHE DOWN", String.valueOf(x1));
                        float deltaX = x2 - x1;

                        if (deltaX < 0 && Math.abs(deltaX) > MIN_DISTANCE) {
                            Toast.makeText(PlayActivity.this, "right to left swipe", Toast.LENGTH_SHORT).show();
                            PlayHelperFunctions.nextAction();
                            setPlayingLayout();

                        } else if (deltaX > 0 && Math.abs(deltaX) > MIN_DISTANCE) {
                            Toast.makeText(PlayActivity.this, "left to right swipe", Toast.LENGTH_SHORT).show();
                            PlayHelperFunctions.prevAction();
                            setPlayingLayout();
                        }
                }
                return true;
            }
        });
        albumArtView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // get the final radius for the clipping circle
                if(shouldReveal) {
                    int dx = albumArtView.getWidth();
                    int dy = albumArtView.getHeight();
                    float finalRadius = (float) Math.hypot(dx, dy);

                    SupportAnimator animator =
                            ViewAnimationUtils.createCircularReveal(albumArtView, 0, 0, 0, finalRadius);
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                    animator.setDuration(750);
                    animator.start();
                    shouldReveal = false;
                }
            }
        });

        TextView playAlbumName = (TextView) findViewById(R.id.playAlbumName);
        playAlbumName.setText(songInfoObj.album);
        TextView playSongName = (TextView) findViewById(R.id.playSongName);
        playSongName.setText(songInfoObj.songName);
       // playSongName.setSelected(true);
        TextView playArtistName = (TextView) findViewById(R.id.playArtistName);
        playArtistName.setText(songInfoObj.artist);
       // playArtistName.setSelected(true);
        TextView fullPlayingTime = (TextView) findViewById(R.id.fullPlayTIme);
        fullPlayingTime.setText(UtilFunctions.convertSecondsToHMmSs(songInfoObj.duration));
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void setActionBarUIData() {
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void setScreenWidthAndHeight(Point size) {
        screenWidth = size.x;
        screenHeight = size.y - getStatusBarHeight();
    }

    public void getAndSetCurrPlaySongInfoFromIntent(Intent i) {
        songInfoObj.currPlayPos = i.getExtras().getInt("position");
        songInfoObj.nowPlayingList = new ArrayList<String>();
        songInfoObj.initialPlayingList = new ArrayList<String>();
        songInfoObj.nowPlayingList = i.getStringArrayListExtra("playingList");
        songInfoObj.initialPlayingList.addAll(songInfoObj.nowPlayingList);
        songInfoObj.shuffled = i.getExtras().getBoolean("shuffle");
    }

    public void layoutInit() {
        PlayPauseView view = (PlayPauseView) findViewById(R.id.playPauseView);
        if ((!PlayHelperFunctions.isSongPlaying && !view.isShowPlay) || (PlayHelperFunctions.isSongPlaying && view.isShowPlay)) {
            view.toggle();
        }
        final ImageView repeat = (ImageView) findViewById(R.id.repeatImage);
        final Drawable repeatIcon = getResources().getDrawable(R.drawable.ic_action_playback_repeat);
        final Drawable repeatIcon_1 = getResources().getDrawable(R.drawable.ic_action_playback_repeat_1);
        if(songInfoObj.isRepeat == 0){
            repeatIcon.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
            repeat.setImageDrawable(repeatIcon);
        }else if(songInfoObj.isRepeat == 1){
            repeatIcon.setColorFilter(Color.parseColor("#be4d56"), PorterDuff.Mode.SRC_ATOP);
            repeat.setImageDrawable(repeatIcon);
        }else{
            repeatIcon_1.setColorFilter(Color.parseColor("#be4d56"), PorterDuff.Mode.SRC_ATOP);
            repeat.setImageDrawable(repeatIcon_1);
        }

        final ImageView shuffle = (ImageView) findViewById(R.id.shuffleImage);
        final Drawable shuffleIcon = getResources().getDrawable(R.drawable.shuffle);
        if (songInfoObj.shuffled) {
            shuffleIcon.setColorFilter(Color.parseColor("#be4d56"), PorterDuff.Mode.SRC_ATOP);
            shuffle.setImageDrawable(shuffleIcon);
        } else {
            shuffleIcon.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
            shuffle.setImageDrawable(shuffleIcon);
        }

    }
}
