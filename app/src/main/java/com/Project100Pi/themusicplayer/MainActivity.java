	 package com.Project100Pi.themusicplayer;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
import android.support.design.widget.AppBarLayout;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
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

import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.listener.SwipeOnScrollListener;

     public class MainActivity extends AppCompatActivity {


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


	static String albumViewOption = "Grid";

	static Boolean isNowPlayEmpty = true;


         static boolean isLongClickOn = false;
         static RelativeLayout mainNowPlaying = null;
         static int viewPagerHeight;
         static Toolbar mToolbar;
         static int mActionBarSize;

         RoundedImageView frontAlbumArt;
         TextView frontTitle,frontAlbum;
          PlayPauseView frontPlay;
         ViewPager pager;
         TabLayout tabLayout;
         FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);
        CursorClass.mContext = getApplicationContext();
        PlayHelperFunctions.mContext = getApplicationContext();
        setUpToolBar();

        final TypedArray styledAttributes = getApplicationContext().getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize });
        mActionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        setUpNowPlayingToolBar();
        frontPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (PlayHelperFunctions.isSongPlaying) {
                    PlayHelperFunctions.pauseMusicPlayer();
                } else {
                    PlayHelperFunctions.startMusicPlayer();

                }
                frontPlay.toggle();
            }
        });
        idToTrackObj = new HashMap<Long,TrackObject>();
        if(songInfoObj.album==null){
        //songInfoObj = new songInfoObj();
        try{
        UtilFunctions.loadPreference(getApplicationContext());
        isNowPlayEmpty = false;
        PlayHelperFunctions.isSongPlaying = false;
            PlayHelperFunctions.audioPlayer((String)PlayHelperFunctions.setPlaySongInfo(Long.parseLong(songInfoObj.nowPlayingList.get(songInfoObj.currPlayPos))),songInfoObj.playerPostion);
        }
        catch(Exception e){
        	Log.i("shared preference", "load failed");
        	isNowPlayEmpty = true;
        }
        }


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


        //updateFrontNowPlaying();
        setUpViewPager();
        setUpTabLayout();
        setUpFloatingActionButton();

        PlayHelperFunctions.seekbar = (SeekBar) findViewById(R.id.front_seekbar);


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
	Intent intent=new Intent(MainActivity.this,SearchResultsActivity.class);
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
             if(!PlayHelperFunctions.isSongPlaying){
                frontPlay.toggle();
             }

             try {
             Cursor cursor = CursorClass.playSongCursor(songInfoObj.songId);
             PlayHelperFunctions.setMainActivityCurrsongInfoFromCursor(songInfoObj.songId,cursor);
            /* cursor.moveToFirst();
                 
            Long albumId = cursor.getLong(3);
             songInfoObj.songName = cursor.getString(5);
             songInfoObj.playPath =cursor.getString(0);
             songInfoObj.artist = cursor.getString(1);
             songInfoObj.album = cursor.getString(2);
             songInfoObj.duration =cursor.getString(4);
             songInfoObj.bitmap = CursorClass.albumArtCursor(getApplicationContext(), albumId); */

                    frontAlbumArt.setImageBitmap(songInfoObj.bitmap);
                    frontTitle.setText(songInfoObj.songName);
                    frontAlbum.setText(songInfoObj.album);
                }catch (Exception e){
                    //Load the first song in tracks info
                    return;
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

                             Intent intent = new Intent(act, PlayListSelectionTest.class);
                             intent.putExtra("songName", songName);
                             intent.putExtra("selectedIdList", selectedIdList);
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


                     switch (item.getItemId()) {
                         case R.id.cnt_menu_play:

                             UtilFunctions.playSelectedSongsfromChoice(act, selectedGenreId, "genre", false);

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
                             UtilFunctions.deleteSinglePopUp(act, act, selectedGenreId.toString(), "Are you sure you want to delete the selected Genre?", " 1 Genre Deleted", "genre");

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


                     switch (item.getItemId()) {

                         case R.id.cnt_menu_play:

                             UtilFunctions.playSelectedSongsfromChoice(act, selectedId, "playlist", false);

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

                             AlertDialog.Builder builder = new AlertDialog.Builder(act);
                             builder.setTitle("Confirm Delete");
                             builder.setMessage("Are you sure you want to delete the Playlist?");
                             builder.setCancelable(true);
                             builder.setPositiveButton("Yes",
                                     new DialogInterface.OnClickListener() {
                                         public void onClick(DialogInterface dialog, int thisid) {
                                             ContentResolver resolver = act.getApplicationContext().getContentResolver();
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
             setUpNowPlayingToolBar();
             updateFrontNowPlaying();
         }
         
        

         public void setUpToolBar()
         {
             mToolbar = (Toolbar) findViewById(R.id.toolbar);
             setSupportActionBar(mToolbar);
             setTitle("My Music");
             mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
             mToolbar.inflateMenu(R.menu.main);
         }

         public void setUpNowPlayingToolBar()
         {
             frontAlbumArt= (RoundedImageView) findViewById(R.id.front_album_art);
             frontTitle = (TextView) findViewById(R.id.front_title);
             frontAlbum = (TextView) findViewById(R.id.front_album);
             frontPlay = (PlayPauseView) findViewById(R.id.front_play_Pause);
             PlayHelperFunctions.seekbar = (SeekBar) findViewById(R.id.front_seekbar);
             PlayHelperFunctions.seekbar.setOnTouchListener(new View.OnTouchListener() {
                 @Override
                 public boolean onTouch(View v, MotionEvent event) {
                     return true;
                 }
             });
             frontPlay.setPauseBackgroundColor(Color.parseColor("#00FFFFFF"));
             frontPlay.setPlayBackgroundColor(Color.parseColor("#00FFFFFF"));
             frontPlay.setBackgroundColor(Color.parseColor("#00FFFFFF"));
             frontPlay.setNeedShadow(false);
         }

         public void setUpViewPager()
         {
             pager = (ViewPager) findViewById(R.id.viewPager);
             pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
             pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                 @Override
                 public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                 }

                 @Override
                 public void onPageSelected(int position) {
                     if(position < 4){
                         fab.show();
                     } else {
                         fab.hide();
                     }

                 }

                 @Override
                 public void onPageScrollStateChanged(int state) {

                 }
             });

         }

         public void setUpTabLayout()
         {
             tabLayout = (TabLayout) findViewById(R.id.tabLayout);
             tabLayout.setupWithViewPager(pager);

         }

         public void setUpFloatingActionButton()
         {
              fab = (FloatingActionButton) findViewById(R.id.fabButton);
             fab.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     UtilFunctions.playSelectedSongs(MainActivity.this, idList, 0, true);
                 }
             });

         }


     }
