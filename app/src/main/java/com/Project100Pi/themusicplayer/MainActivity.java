	 package com.Project100Pi.themusicplayer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import android.widget.SeekBar.OnSeekBarChangeListener;

import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.listener.SwipeOnScrollListener;

     public class MainActivity extends AppCompatActivity {

         int mediaPos,mediaMax;
	String titles[] = {"Tracks","Albums","Artists","Genres","Playlists","Folders"};
         public static ArrayList<String> idList = null;
         public static HashMap<String, String> pathToIdInfo  = null;
         public static HashMap<Long,TrackObject>idToTrackObj = null;
         /*
         public static ArrayList<String > artistIdList = null;

         public static ArrayList<String>  titleList = null;
         public static ArrayList<String>  pathList = null;
         public static ArrayList<String>  albumList = null;
         public static ArrayList<String> albumIdList = null;
         */


	static songInfoObj currSongInfo = null;
	static String albumViewOption = "List";
	static MediaPlayer mp = new MediaPlayer();
	static Boolean isNowPlayEmpty = true;
	static Boolean isSongPlaying =  false;
	static Handler handler = new Handler();
         static boolean isLongClickOn = false;
         static RelativeLayout mainNowPlaying = null;
         static int viewPagerHeight;
         SeekBar frontSeekbar;
         static Toolbar mToolbar;
         static int mActionBarSize;

         ImageView frontAlbumArt;
         TextView frontTitle,frontAlbum;
          PlayPauseView frontPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);
       mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitle("My Music");
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        mToolbar.inflateMenu(R.menu.main);
        final TypedArray styledAttributes = getApplicationContext().getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize });
        mActionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        frontAlbumArt= (ImageView) findViewById(R.id.front_album_art);
        frontTitle = (TextView) findViewById(R.id.front_title);
        frontAlbum = (TextView) findViewById(R.id.front_album);
        frontPlay = (PlayPauseView) findViewById(R.id.front_play_Pause);
        frontSeekbar = (SeekBar) findViewById(R.id.front_seekbar);
        frontPlay.setPauseBackgroundColor(Color.parseColor("#00FFFFFF"));
        frontPlay.setPlayBackgroundColor(Color.parseColor("#00FFFFFF"));
        frontPlay.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        frontPlay.setNeedShadow(false);
        frontPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (MainActivity.isSongPlaying) {
                    MainActivity.mp.pause();
                    MainActivity.isSongPlaying = false;
                    PlayActivity.floatingLyricIntent(getApplicationContext(), (long) MainActivity.mp.getCurrentPosition(), false);
                } else {
                    MainActivity.mp.start();
                    MainActivity.isSongPlaying = true;
                    PlayActivity.floatingLyricIntent(getApplicationContext(), (long) MainActivity.mp.getCurrentPosition(), true);
                }
                frontPlay.toggle();
            }
        });
        idToTrackObj = new HashMap<Long,TrackObject>();
        if(currSongInfo==null){
        currSongInfo = new songInfoObj();
        try{
        UtilFunctions.loadPreference(getApplicationContext());
        isNowPlayEmpty = false;
        }
        catch(Exception e){
        	Log.i("shared preference", "load failed");
        	isNowPlayEmpty = true;
        }
        }

      //  frontSeekbar.getThumb().setColorFilter(0x22BE4D56, PorterDuff.Mode.MULTIPLY);
        frontSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {


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
               // runningTime.setText(UtilFunctions.convertSecondsToHMmSs(seekBar.getProgress()));


            }
        });
        LinearLayout frontNowPlayingContainer = (LinearLayout) findViewById(R.id.front_now_playing_container);
        frontNowPlayingContainer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click


                Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
                intent.putExtra("do", "watch");

                startActivity(intent);
            }
        });
        /*
        Button nowPlaying  = (Button) findViewById(R.id.nowPlay);
        nowPlaying.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click   


				 Intent intent=new Intent(getApplicationContext(), PlayActivity.class);
				 	intent.putExtra("do", "watch");
		          
		            startActivity(intent);   
            }
        });
       */
       ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(pager);

        //updateFrontNowPlaying();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabButton);
        fab.setRippleColor(Color.parseColor("#be4d56"));

        //seekbar = (SeekBar) findViewById(R.id.front_seekbar);
        //seekbar.setProgress(50);


        /*
       SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout)findViewById(R.id.pager_tab_strip);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(pager);
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {

            @Override
            public int getIndicatorColor(int position) {
            return 0xff9e60ae;    //define any color in xml resources and set it here, I have used white
            }

       
        });
        */
        
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        
        return true;
    }
    
    @Override

    public boolean onOptionsItemSelected(MenuItem item) {
switch (item.getItemId()) {
case R.id.action_search:
	Intent intent=new Intent(MainActivity.this,SearchResultTestActivity.class);
 startActivity(intent);
  break;

default:
  break;
}

return true;
}

         private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

            case 0: return FirstFragmentTest.newInstance("FirstFragment Instance 1");
            case 1: return SecondFragmentTest.newInstance("SecondFragment, Instance 1");
            case 2: return ThirdFragmentTest.newInstance("ThirdFragment, Instance 1");
            case 3: return FourthFragmentTest.newInstance("ThirdFragment, Instance 2");
            case 4: return FifthFragmentTest.newInstance("ThirdFragment, Instance 3");
            case 5: return SixthFragmentTest.newInstance("ThirdFragment, Instance 3");
            default: return ThirdFragment.newInstance("ThirdFragment, Default");
            }
            
        }

        @Override
        public int getCount() {
            return 6;
        }       
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
          
    }
  @Override
protected void onStop() {
	// TODO Auto-generated method stub
	super.onStop();
	//UtilFunctions.savePreference(getApplicationContext());
}  
 @Override
protected void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();
	try{
	UtilFunctions.savePreference(getApplicationContext());
	}catch(Exception e){
		Log.i("shared preference", "savefailed");
	}
}

         public void updateFrontNowPlaying(){

             if(frontPlay.isShowPlay){
                 frontPlay.toggle();
             }
             if(!MainActivity.isSongPlaying){
                frontPlay.toggle();
             }

             try {
             Cursor cursor = CursorClass.playSongCursor(getApplicationContext(),currSongInfo.songId);
             cursor.moveToFirst();
            Long albumId = cursor.getLong(3);
             MainActivity.currSongInfo.songName = cursor.getString(5);
             MainActivity.currSongInfo.playPath =cursor.getString(0);
             MainActivity.currSongInfo.artist = cursor.getString(1);
             MainActivity.currSongInfo.album = cursor.getString(2);
             MainActivity.currSongInfo.duration =cursor.getString(4);
             MainActivity.currSongInfo.bitmap = CursorClass.albumArtCursor(getApplicationContext(), albumId);

                    frontAlbumArt.setImageBitmap(MainActivity.currSongInfo.bitmap);
                    frontTitle.setText(MainActivity.currSongInfo.songName);
                    frontAlbum.setText(MainActivity.currSongInfo.album);
                }catch (Exception e){
                    //Load the first song in tracks info
                    return;
                }

             try {
                 audioPlayer(MainActivity.currSongInfo.playPath, currSongInfo.playerPostion);
             } catch (IOException e) {
                 e.printStackTrace();
             }

         }

         public static void overflowReaction(View v, final Activity act, final TrackObject selTrack){
             PopupMenu popupMenu = new PopupMenu(act,v);
             popupMenu.inflate(R.menu.long_click_actions);
             final String songName=selTrack.getTrackName();
             final Long selectedId=Long.parseLong(selTrack.getTrackId());
             final int currPosition = selTrack.getsNo();
             final ArrayList<String> selectedIdList=new ArrayList<String>();
             selectedIdList.add(selectedId.toString()); // created to send the ArrayList to UtilFunctions.deletePopUp
             final ArrayList<String> selSongNameList = new ArrayList<String>();
             selSongNameList.add(songName);
             popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                 @Override
                 public boolean onMenuItemClick(MenuItem item) {
                     switch (item.getItemId()) {
                         case R.id.cnt_menu_play:

                             UtilFunctions.playSelectedSongs(act, idList, currPosition, false);

                             break;
                         case R.id.cnt_menu_play_next:

                             UtilFunctions.playSongsNext(act, selectedIdList);

                             break;

                         case R.id.cnt_menu_add_queue:
                             UtilFunctions.addToQueueSongs(act, selectedIdList);
                             break;
                         case R.id.addToPlaylist:

                             Intent intent=new Intent(act,PlayListSelectionTest.class);
                             intent.putExtra("selectedIdList",selectedIdList);
                             act.startActivity(intent);


                             break;
                         case R.id.cnt_mnu_edit:

                             UtilFunctions.changeSongInfo(act, selectedId);


                             //Toast.makeText(this, "Edit :" , Toast.LENGTH_SHORT).show();

                             break;
                         case R.id.cnt_mnu_delete:

                             UtilFunctions.deletePopUp(act,act, selectedIdList, "Are you sure you want to delete the selected song?", "Song deleted");


                             break;
                         case R.id.cnt_mnu_share:
                             Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                             String path = selTrack.getTrackPath();
                             path = "file://" + path;
                             Toast.makeText(act, "Path is " + path, Toast.LENGTH_SHORT).show();
                             Uri uri = Uri.parse(path);
                             sharingIntent.setType("audio/*");
                             sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                             sharingIntent.putExtra(Intent.EXTRA_TITLE, songName);
                             act.startActivity(Intent.createChooser(sharingIntent, "Share Using"));

                             break;

                     }

                     return true;

                 }
             });
            popupMenu.show();

         }

         public static void secondOverflowReaction(View v, final Activity act,AlbumInfo selAlbum){

             PopupMenu popupMenu = new PopupMenu(act,v);
             popupMenu.inflate(R.menu.long_click_actions);
             int currPosition = selAlbum.getsNo();


             // final String songName=titleList.get(currPosition);
             final Long selectedAlbumId=selAlbum.getAlbumId();
             final String selectedAlbumName = selAlbum.getAlbumName();
             //  Toast.makeText(getActivity(), songName +"and position is" + currPosition, Toast.LENGTH_LONG).show();

             popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                 @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                 @Override
                 public boolean onMenuItemClick(MenuItem item) {


                     switch(item.getItemId()){
                         case R.id.cnt_menu_play:

                             UtilFunctions.playSelectedSongsfromChoice(act, selectedAlbumId, "album",false);

                             break;

                         case R.id.cnt_menu_play_next:

                             UtilFunctions.playSongsNextfromChoice(act, selectedAlbumId, "album");

                             break;

                         case R.id.cnt_menu_add_queue:
                             UtilFunctions.addToQueuefromChoice(act,selectedAlbumId, "album");
                             break;
                         case R.id.addToPlaylist:

                             act.startActivity(UtilFunctions.addSongstoPlaylist(act,selectedAlbumId,"album"));

                             break;
                         case R.id.cnt_mnu_edit:

                             //editAlbumInfo(selectedAlbumName, selectedAlbumId); // NOT WORKING

                             break;
                         case R.id.cnt_mnu_delete:

                             UtilFunctions.deleteSinglePopUp(act, act, selectedAlbumId.toString(),"Are you sure you want to delete the selected Album?"," 1 Album Deleted","album");


                             break;
                         case R.id.cnt_mnu_share:


                             act.startActivity(UtilFunctions.shareSingle(act, selectedAlbumId, "album"));

                             break;

                     }

                     return true;

                 }
             });
             popupMenu.show();
         }

         public static void thirdOverflowReaction(View v, final Activity act,ArtistInfo selArtist){

             PopupMenu popupMenu = new PopupMenu(act,v);
             popupMenu.inflate(R.menu.long_click_actions);
             int currPosition = selArtist.getsNo();


             // final String songName=titleList.get(currPosition);
             final Long selectedArtistId=selArtist.getArtistId();
             final String selectedAlbumName = selArtist.getArtistName();
             //  Toast.makeText(getActivity(), songName +"and position is" + currPosition, Toast.LENGTH_LONG).show();

             popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                 @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                 @Override
                 public boolean onMenuItemClick(MenuItem item) {


                     switch(item.getItemId()){
                         case R.id.cnt_menu_play:

                             UtilFunctions.playSelectedSongsfromChoice(act, selectedArtistId, "artist",false);

                             break;
                         case R.id.cnt_menu_play_next:

                             UtilFunctions.playSongsNextfromChoice(act, selectedArtistId, "artist");

                             break;

                         case R.id.cnt_menu_add_queue:
                             UtilFunctions.addToQueuefromChoice(act,selectedArtistId, "artist");
                             break;
                         case R.id.addToPlaylist:
                             act.startActivity(UtilFunctions.addSongstoPlaylist(act, selectedArtistId, "artist"));


                             break;
                         case R.id.cnt_mnu_edit:

                             break;
                         case R.id.cnt_mnu_delete:

                             UtilFunctions.deleteSinglePopUp(act, act, selectedArtistId.toString(),"Are you sure you want to delete the selected Artist?"," 1 Artist Deleted","artist");
                             break;
                         case R.id.cnt_mnu_share:


                             act.startActivity(UtilFunctions.shareSingle(act, selectedArtistId, "artist"));


                             break;

                     }

                     return true;

                 }
             });
             popupMenu.show();
         }

         public static void fourthOverflowReaction(View v, final Activity act,GenreInfo selGenre){

             PopupMenu popupMenu = new PopupMenu(act,v);
             popupMenu.inflate(R.menu.long_click_actions);
             int currPosition = selGenre.getsNo();


             // final String songName=titleList.get(currPosition);
             final Long selectedGenreId=selGenre.getGenreId();
             //  Toast.makeText(getActivity(), songName +"and position is" + currPosition, Toast.LENGTH_LONG).show();

             popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                 @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                 @Override
                 public boolean onMenuItemClick(MenuItem item) {


                     switch(item.getItemId()){
                         case R.id.cnt_menu_play:

                             UtilFunctions.playSelectedSongsfromChoice(act, selectedGenreId, "genre",false);

                             break;
                         case R.id.cnt_menu_play_next:

                             UtilFunctions.playSongsNextfromChoice(act, selectedGenreId, "genre");

                             break;

                         case R.id.cnt_menu_add_queue:
                             UtilFunctions.addToQueuefromChoice(act, selectedGenreId, "genre");
                             break;
                         case R.id.addToPlaylist:
                             act.startActivity(UtilFunctions.addSongstoPlaylist(act, selectedGenreId, "genre"));




                             break;

                         case R.id.cnt_mnu_edit:


                             break;
                         case R.id.cnt_mnu_delete:
                             UtilFunctions.deleteSinglePopUp(act, act, selectedGenreId.toString(),"Are you sure you want to delete the selected Genre?"," 1 Genre Deleted","genre");

                             break;
                         case R.id.cnt_mnu_share:

                             act.startActivity(UtilFunctions.shareSingle(act, selectedGenreId, "genre"));


                             break;

                     }

                     return true;

                 }
             });
             popupMenu.show();
         }


         public static void fifthOverflowReaction(View v, final Activity act, final PlaylistInfo selPlaylist){

             PopupMenu popupMenu = new PopupMenu(act,v);
             popupMenu.inflate(R.menu.long_click_actions);
             int currPosition = selPlaylist.getsNo();


             // final String songName=titleList.get(currPosition);
             final Long selectedId=selPlaylist.getPlaylistId();
             //  Toast.makeText(getActivity(), songName +"and position is" + currPosition, Toast.LENGTH_LONG).show();

             popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                 @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                 @Override
                 public boolean onMenuItemClick(MenuItem item) {


                     switch(item.getItemId()){

                         case R.id.cnt_menu_play:

                             UtilFunctions.playSelectedSongsfromChoice(act, selectedId, "playlist",false);

                             break;
                         case R.id.cnt_menu_play_next:

                             UtilFunctions.playSongsNextfromChoice(act, selectedId, "playlist");

                             break;

                         case R.id.cnt_menu_add_queue:
                             UtilFunctions.addToQueuefromChoice(act, selectedId, "playlist");
                             break;
                         case R.id.cnt_mnu_edit:


                             LayoutInflater layoutInflater = LayoutInflater.from(act);
                             View promptView = layoutInflater.inflate(R.layout.dialog_box, null);
                             AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(act);
                             alertDialogBuilder.setView(promptView);
                             //	Toast.makeText(activity,"Id is "+id,Toast.LENGTH_SHORT).show();
                             TextView textView = (TextView) promptView.findViewById(R.id.textView);
                             textView.setText("Edit PlayList Name");
                             final EditText editTitleText = (EditText) promptView.findViewById(R.id.edittext);
                             editTitleText.setText(selPlaylist.getPlaylistName());

                             //final EditText editAlbumText=(EditText) promptView.findViewById(R.id.edittext);
                             // setup a dialog window
                             alertDialogBuilder.setCancelable(false)
                                     .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                         public void onClick(DialogInterface dialog, int id) {

                                             ContentResolver thisContentResolver = act.getContentResolver();
                                             UtilFunctions.renamePlaylist(thisContentResolver, selectedId, editTitleText.getText().toString());
                                             Toast.makeText(act, "PlayList Renamed", Toast.LENGTH_LONG).show();

                                             // populateCards();
                                         }
                                     })
                                     .setNegativeButton("Cancel",
                                             new DialogInterface.OnClickListener() {
                                                 public void onClick(DialogInterface dialog, int id) {
                                                     dialog.cancel();
                                                 }
                                             });

                             // create an alert dialog
                             AlertDialog alert = alertDialogBuilder.create();
                             alert.show();


                             //Toast.makeText(this, "Edit :" , Toast.LENGTH_SHORT).show();

                             break;
                         case R.id.cnt_mnu_delete:

                             AlertDialog.Builder builder=new AlertDialog.Builder(act);
                             builder.setTitle("Confirm Delete");
                             builder.setMessage("Are you sure you want to delete the Playlist?");
                             builder.setCancelable(true);
                             builder.setPositiveButton("Yes",
                                     new DialogInterface.OnClickListener() {
                                         public void onClick(DialogInterface dialog, int thisid) {
                                             ContentResolver resolver=act.getApplicationContext().getContentResolver();
                                             UtilFunctions.deletePlaylist(resolver, selectedId);
                                             dialog.cancel();
                                         }
                                     });
                             builder.setNegativeButton("No",
                                     new DialogInterface.OnClickListener() {
                                         public void onClick(DialogInterface dialog, int id) {
                                             dialog.cancel();
                                         }
                                     });

                             AlertDialog alert11 = builder.create();
                             alert11.show();


                             break;
                         case R.id.cnt_mnu_share:

                             act.startActivity(UtilFunctions.shareSingle(act, selectedId, "playlist"));
                             break;

                     }

                     return true;

                 }
             });
             popupMenu.show();
         }


         @Override
         protected void onResume() {
             super.onResume();
             updateFrontNowPlaying();
         }

         public void audioPlayer(String path,final int playerPos) throws IOException {
             if (MainActivity.mp != null) {
                 MainActivity.mp.reset();
             }

             MainActivity.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                 @Override
                 public void onCompletion(MediaPlayer mp) {
                     //mp.reset();
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
                         audioPlayer((String)PlayActivity.getPlaySongInfo(getApplicationContext(), Long.parseLong(MainActivity.currSongInfo.nowPlayingList.get(MainActivity.currSongInfo.currPlayPos))),1);
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
             MainActivity.mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                 @Override
                 public void onPrepared(MediaPlayer mp) {
                     MainActivity.mp.seekTo(playerPos);
                     if (MainActivity.isSongPlaying) {
                         MainActivity.mp.start();
                         PlayActivity.floatingLyricIntent(getApplicationContext(), (long) playerPos, true);
                     }


                 }
             });
             mediaPos =MainActivity.mp.getCurrentPosition();
             mediaMax = MainActivity.mp.getDuration();
             MainActivity.handler.removeCallbacks(moveSeekBarThread);
             MainActivity.handler.postDelayed(moveSeekBarThread, 100); //cal the thread after 100 milliseconds

         }

         Runnable moveSeekBarThread = new Runnable() {
             public void run() {

                 Integer mediaPos_new = MainActivity.mp.getCurrentPosition();
                 int mediaMax_new = MainActivity.mp.getDuration();
                 MainActivity.currSongInfo.playerPostion = mediaPos_new;
                 frontSeekbar.setMax(mediaMax_new);
                 frontSeekbar.setProgress(mediaPos_new);
                 if(MainActivity.mp.isPlaying()){
                   //  runningTime.setText(UtilFunctions.convertSecondsToHMmSs(mediaPos_new));
                 }
                 MainActivity.handler.postDelayed(this, 1000); //Looping the thread after 1 second
                 // seconds


             }
         };
     }
