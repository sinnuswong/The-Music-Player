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
	public int getStatusBarHeight() {
	    int result = 0;
	    int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
	    if (resourceId > 0) {
	        result = getResources().getDimensionPixelSize(resourceId);
	    }
	    return result;
	}
	
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
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33000000")));
		actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#55000000")));

		mcontext = getApplicationContext();
		
		//Toast.makeText(getApplicationContext(), width+" "+height, Toast.LENGTH_LONG).show();
		// albumArtSize = width;
		 boolean hasMenuKey = ViewConfiguration.get(getApplicationContext()).hasPermanentMenuKey();
		// Toast.makeText(getApplicationContext(), height+" ", Toast.LENGTH_LONG).show();
		
		//   Toast.makeText(getApplicationContext(), thisLayout.getLayoutParams().height+" ", Toast.LENGTH_LONG).show();
	
		 Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			 screenWidth = size.x;
			 screenHeight = size.y-getStatusBarHeight();
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
		Intent i = getIntent();
		if(i.getExtras().getString("do").equals("Play")){
		
		MainActivity.currSongInfo.currPlayPos = i.getExtras().getInt("position");
		MainActivity.currSongInfo.nowPlayingList = new ArrayList<String>();
		MainActivity.currSongInfo.initialPlayingList = new ArrayList<String>();
	    MainActivity.currSongInfo.nowPlayingList = i.getStringArrayListExtra("playingList");
	    MainActivity.currSongInfo.initialPlayingList.addAll(MainActivity.currSongInfo.nowPlayingList);
	    MainActivity.currSongInfo.shuffled = i.getExtras().getBoolean("shuffle");
	    if(MainActivity.currSongInfo.shuffled){
	    	long seed = System.nanoTime();
      	  Collections.shuffle(MainActivity.currSongInfo.nowPlayingList, new Random(seed));
	    }
	    
	    nowPlayingId=Long.parseLong(MainActivity.currSongInfo.nowPlayingList.get(MainActivity.currSongInfo.currPlayPos));
	   
	    try{
	    	MainActivity.isSongPlaying = true;
	    	audioPlayer((String)setPlaySongInfo(nowPlayingId),1);
	    	}catch(IOException e){
	    	e.printStackTrace();
	    }
	    
	  
		
		}else if(i.getExtras().getString("do").equals("watch")){
			
			setPlaySongInfo(MainActivity.currSongInfo.songId);
			if(!MainActivity.isSongPlaying && (MainActivity.mp.getDuration()!=Integer.parseInt(MainActivity.currSongInfo.duration))){
				try {
					audioPlayer(MainActivity.currSongInfo.playPath, MainActivity.currSongInfo.playerPostion);
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
        		MainActivity.mp.pause();
        		MainActivity.isSongPlaying = false;
        	    floatingLyricIntent(getApplicationContext(), (long)MainActivity.mp.getCurrentPosition(), false);
        	} else {
        		MainActivity.mp.start();
        		MainActivity.isSongPlaying = true;
        	    floatingLyricIntent(getApplicationContext(), (long)MainActivity.mp.getCurrentPosition(), true);
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
            	else if(MainActivity.currSongInfo.currPlayPos <=0 ){
            		MainActivity.currSongInfo.currPlayPos = MainActivity.currSongInfo.nowPlayingList.size()-1;
            	}else{
            	MainActivity.currSongInfo.currPlayPos = MainActivity.currSongInfo.currPlayPos-1;
            	}
                try {
                	MainActivity.mp.reset();
					audioPlayer((String)setPlaySongInfo(Long.parseLong(MainActivity.currSongInfo.nowPlayingList.get(MainActivity.currSongInfo.currPlayPos))),1);
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
	    if(MainActivity.currSongInfo.isRepeat == 0){
			  repeatIcon.setColorFilter(Color.parseColor("#2F5D53"), PorterDuff.Mode.SRC_ATOP); 
          	repeat.setImageDrawable(repeatIcon);
		}else if(MainActivity.currSongInfo.isRepeat == 1){
			 repeatIcon.setColorFilter(Color.MAGENTA, PorterDuff.Mode.SRC_ATOP); 
	          	repeat.setImageDrawable(repeatIcon);
		}else{
			repeatIcon_1.setColorFilter(Color.MAGENTA, PorterDuff.Mode.SRC_ATOP); 
	      	repeat.setImageDrawable(repeatIcon_1);
		}
	    repeat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click   
            		if(MainActivity.currSongInfo.isRepeat == 0){
            			  repeatIcon.setColorFilter(Color.MAGENTA, PorterDuff.Mode.SRC_ATOP); 
                        	repeat.setImageDrawable(repeatIcon);
            			MainActivity.currSongInfo.isRepeat = 1;
            			Toast.makeText(getApplicationContext(), "Repeat ON", Toast.LENGTH_LONG).show();
            		}else if(MainActivity.currSongInfo.isRepeat == 1){
            			repeatIcon_1.setColorFilter(Color.MAGENTA, PorterDuff.Mode.SRC_ATOP); 
                    	repeat.setImageDrawable(repeatIcon_1);
            			MainActivity.currSongInfo.isRepeat =2;
            			Toast.makeText(getApplicationContext(), "Single Repeat ON", Toast.LENGTH_LONG).show();
            		}else{
            			 repeatIcon.setColorFilter(Color.parseColor("#2F5D53"), PorterDuff.Mode.SRC_ATOP); 
                     	repeat.setImageDrawable(repeatIcon);
            			MainActivity.currSongInfo.isRepeat=0;
            			Toast.makeText(getApplicationContext(), "Repeat OFF", Toast.LENGTH_LONG).show();
            		}
            		     
            }
        });
	    
	   
	   
	    final ImageView shuffle=(ImageView)findViewById(R.id.shuffleImage);
	    final Drawable shuffleIcon = getResources().getDrawable(R.drawable.shuffle); 
	    if(MainActivity.currSongInfo.shuffled){
      	  shuffleIcon.setColorFilter(Color.MAGENTA, PorterDuff.Mode.SRC_ATOP); 
        	shuffle.setImageDrawable(shuffleIcon);
      	} else{
      		 shuffleIcon.setColorFilter(Color.parseColor("#2F5D53"), PorterDuff.Mode.SRC_ATOP); 
             	shuffle.setImageDrawable(shuffleIcon);
      	}
	    shuffle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	if(!MainActivity.currSongInfo.shuffled){
            	  Toast.makeText(getApplicationContext(), "Shuffle ON ", Toast.LENGTH_LONG).show();
            	  long seed = System.nanoTime();
            	  Collections.shuffle(MainActivity.currSongInfo.nowPlayingList, new Random(seed));
            	  MainActivity.currSongInfo.currPlayPos = MainActivity.currSongInfo.nowPlayingList.indexOf(MainActivity.currSongInfo.songId.toString());
            	  MainActivity.currSongInfo.shuffled = true;
            	  shuffleIcon.setColorFilter(Color.MAGENTA, PorterDuff.Mode.SRC_ATOP); 
              	shuffle.setImageDrawable(shuffleIcon);
            	} else{
            		Toast.makeText(getApplicationContext(), "Shuffle OFF ", Toast.LENGTH_LONG).show();
            		MainActivity.currSongInfo.nowPlayingList.clear();
            		MainActivity.currSongInfo.nowPlayingList.addAll(MainActivity.currSongInfo.initialPlayingList);
            		MainActivity.currSongInfo.currPlayPos = MainActivity.currSongInfo.nowPlayingList.indexOf(MainActivity.currSongInfo.songId.toString());
            		MainActivity.currSongInfo.shuffled = false;
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
                  MainActivity.currSongInfo.playerPostion = mediaPos_new;
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
            // intent.putExtra("art",(Serializable)MainActivity.currSongInfo.nowPlayingList);
             intent.putExtra("position", MainActivity.currSongInfo.currPlayPos);
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
            // intent.putExtra("art",(Serializable)MainActivity.currSongInfo.nowPlayingList);
             intent.putExtra("position", MainActivity.currSongInfo.currPlayPos);
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
	            	if(MainActivity.currSongInfo.isRepeat ==0){
	                   		MainActivity.currSongInfo.currPlayPos = (MainActivity.currSongInfo.currPlayPos+1);
	                   		if(MainActivity.currSongInfo.currPlayPos == MainActivity.currSongInfo.nowPlayingList.size()){
	                   			
	                   			MainActivity.currSongInfo.currPlayPos--;
	                   			return;
	                   		}
	            	}else if(MainActivity.currSongInfo.isRepeat == 1){
	            		MainActivity.currSongInfo.currPlayPos = (MainActivity.currSongInfo.currPlayPos+1)%MainActivity.currSongInfo.nowPlayingList.size();
		               
	            	}
	            	
	            	
	            	 try {
	            		 mp.reset();
							audioPlayer((String)setPlaySongInfo(Long.parseLong(MainActivity.currSongInfo.nowPlayingList.get(MainActivity.currSongInfo.currPlayPos))),1);
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
		 
		 String playPath,artist,album,duration,songName;
		long albumId;
		 
		 
		 Cursor cursor = CursorClass.playSongCursor(context,songId);
		 cursor.moveToFirst();
		    playPath = cursor.getString(0);
		    artist=cursor.getString(1);
		    album=cursor.getString(2);
		    albumId=cursor.getLong(3);
		    duration=cursor.getString(4);
		    songName=cursor.getString(5);
		    MainActivity.currSongInfo.songName = songName;
		    MainActivity.currSongInfo.playPath = playPath;
		    MainActivity.currSongInfo.artist = artist;
		    MainActivity.currSongInfo.album = album;
		    MainActivity.currSongInfo.duration = duration;
		    MainActivity.currSongInfo.songId = songId;
		    MainActivity.currSongInfo.bitmap = CursorClass.albumArtCursor(context,albumId);
		 return playPath;

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
		   
		 
		return MainActivity.currSongInfo.playPath;
		 
		 
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
	 };
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
		setPlaySongInfo(MainActivity.currSongInfo.songId);
	}
	
	private void setPlayingLayout(){
		
		 ImageView albumArtView =(ImageView) findViewById(R.id.albumArt);
			albumArtView.requestLayout();
			albumArtView.getLayoutParams().width = albumArtSize;
			albumArtView.getLayoutParams().height = albumArtSize;
	        albumArtView.setImageBitmap(MainActivity.currSongInfo.bitmap);
	      
	        TextView playAlbumName = (TextView) findViewById(R.id.playAlbumName);
			playAlbumName.setText(MainActivity.currSongInfo.album);
			TextView playSongName = (TextView) findViewById(R.id.playSongName);
			playSongName.setText(MainActivity.currSongInfo.songName);
			playSongName.setSelected(true);
			TextView playArtistName = (TextView) findViewById(R.id.playArtistName);
			playArtistName.setText(MainActivity.currSongInfo.artist);
			playArtistName.setSelected(true);
			TextView fullPlayingTime = (TextView) findViewById(R.id.fullPlayTIme);
			fullPlayingTime.setText(UtilFunctions.convertSecondsToHMmSs(Long.parseLong(MainActivity.currSongInfo.duration)));
	}
	
	public static void floatingLyricIntent(Context context,long currDuration,Boolean isPlaying){
		
		Intent intent = new Intent();
	    intent.setAction("com.android.music.metachanged");
	    Bundle bundle = new Bundle();

	    // put the song's metadata
	    bundle.putString("track", MainActivity.currSongInfo.songName);
	    bundle.putString("artist", MainActivity.currSongInfo.artist);
	    bundle.putString("album", MainActivity.currSongInfo.album);

	    // put the song's total duration (in ms)
	    bundle.putLong("duration", Long.parseLong(MainActivity.currSongInfo.duration)); // 4:05

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
		MainActivity.currSongInfo.currPlayPos = (MainActivity.currSongInfo.currPlayPos+1) % MainActivity.currSongInfo.nowPlayingList.size();
		try {
			audioPlayer((String)setPlaySongInfo(Long.parseLong(MainActivity.currSongInfo.nowPlayingList.get(MainActivity.currSongInfo.currPlayPos))),1);
			MainActivity.handler.postDelayed(moveSeekBarThread, 100);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void swipeCurrPlayAction(){
		MainActivity.mp.reset();
		MainActivity.currSongInfo.currPlayPos = (MainActivity.currSongInfo.currPlayPos) % MainActivity.currSongInfo.nowPlayingList.size();
		try {
			audioPlayer((String)setPlaySongInfo(Long.parseLong(MainActivity.currSongInfo.nowPlayingList.get(MainActivity.currSongInfo.currPlayPos))),1);
			MainActivity.handler.postDelayed(moveSeekBarThread, 100);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
