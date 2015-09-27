package com.Project100Pi.themusicplayer;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SongsUnderTest extends AppCompatActivity  implements ClickInterface{
    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;
    trackRecyclerAdapter tra;
    ArrayList<TrackObject> tracks;
    ArrayList<String > currIdList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_frag_test);

        final RecyclerView firstFragRecycler = (RecyclerView)findViewById(R.id.firstFragRecycler);
        firstFragRecycler.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        firstFragRecycler.setLayoutManager(llm);
        tracks = new ArrayList<TrackObject>();
        currIdList = new ArrayList<String>();
        String title="";
        String path="";
        String trackAlbum = "";
        String trackArtist = "";
        String trackDuration  = "";
        Long trackId=null;
        Intent intent = getIntent();
        String X = intent.getStringExtra("X");
        Long id = intent.getLongExtra("id", 0L);
        Cursor cursor = null;
        if(X.equals("Album")){
            cursor = makeAlbumSongCursor(id);
        }else if(X.equals("Artist")){
            cursor = makeArtistSongCursor(id);
        }else if(X.equals("Genre")){

            String[] proj2={MediaStore.Audio.Genres.Members.AUDIO_ID,
                    MediaStore.Audio.Genres.Members.TITLE,
                    MediaStore.Audio.Genres.Members.ALBUM,
                    MediaStore.Audio.Genres.Members.ARTIST,
                    MediaStore.Audio.Genres.Members.DATA,
                    MediaStore.Audio.Genres.Members.DURATION};
            Uri uri = MediaStore.Audio.Genres.Members.getContentUri("external", id);
            cursor = getContentResolver().query(uri, proj2, null,null,null);
        }else if(X.equals("PlayList")){

            String[] projection1 = {
                    MediaStore.Audio.Playlists.Members.AUDIO_ID,
                    MediaStore.Audio.Playlists.Members.TITLE,
                    MediaStore.Audio.Playlists.Members.ALBUM,
                    MediaStore.Audio.Playlists.Members.ARTIST,
                    MediaStore.Audio.Playlists.Members.DATA,
                    MediaStore.Audio.Playlists.Members.DURATION
            };
            Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", id);
            cursor =SongsUnderTest.this.getApplicationContext().getContentResolver().query(
                    uri,
                    projection1,
                    MediaStore.Audio.Playlists.Members.PLAYLIST_ID+ " = "+id+"",
                    null,
                    null);


        }
        int i = 0;

        while(cursor.moveToNext()){
            try{
                trackId=cursor.getLong(0);
                title= cursor.getString(1);
                trackAlbum = cursor.getString(2);
                trackArtist = cursor.getString(3);
                path=cursor.getString(4);
                trackDuration = UtilFunctions.convertSecondsToHMmSs(Long.parseLong(cursor.getString(5)));
                TrackObject currTrack =  new TrackObject(i,trackId.toString(),title,trackArtist,trackDuration,path);
                tracks.add(currTrack);
                currIdList.add(trackId.toString());
                i++;
            }catch(Exception e){
                continue;
            }
        }

        tra = new trackRecyclerAdapter(this,tracks,currIdList,SongsUnderTest.this);
        firstFragRecycler.setAdapter(tra);
        firstFragRecycler.setItemAnimator(new DefaultItemAnimator());
        FastScroller fastScroller=(FastScroller)findViewById(R.id.firstfastscroller);
        fastScroller.setRecyclerView(firstFragRecycler);

    }

    @Override
    public void onItemClicked(int position) {
        if (actionMode != null) {
            toggleSelection(position);
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        if (actionMode == null) {
            actionMode= ((AppCompatActivity) SongsUnderTest.this).startSupportActionMode(actionModeCallback);
        }

        toggleSelection(position);

        return true;
    }

    private void toggleSelection(int position) {
        tra.toggleSelection(position);
        int count = tra.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count)+" item(s) selected");
            actionMode.invalidate();
        }
    }


    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MainActivity.isLongClickOn =  true;
            MainActivity.mToolbar.getLayoutParams().height = 0;
            mode.getMenuInflater().inflate (R.menu.multi_choice_options, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            final ArrayList<String> selIdList=getidList();
            final ArrayList<String> selsongNameList=getsongNameList();
            final int size=selIdList.size();
            switch (item.getItemId()) {

                case R.id.itemShuffle:
                    UtilFunctions.playSelectedSongs(SongsUnderTest.this, selIdList, 0, true);
                    break;
                case R.id.itemPlay:
        		/*
        		 Intent intent=new Intent(SongsUnderTest.this,PlayActivity.class);
				 	intent.putExtra("do", "Play");
		           intent.putExtra("position", 0);
		            intent.putStringArrayListExtra("playingList", selsongNameList);
		            startActivity(intent);
		            */
                    UtilFunctions.playSelectedSongs(SongsUnderTest.this,selIdList,0,false);
                    break;

                case R.id.itemPlayNext:
                    UtilFunctions.playSongsNext(SongsUnderTest.this, selIdList);


                    break;
                case R.id.itemAddQueue:

                    UtilFunctions.addToQueueSongs(SongsUnderTest.this, selIdList);
                    break;

                case R.id.itemShare:
                    Intent sharingIntent=new Intent(Intent.ACTION_SEND_MULTIPLE);
                    sharingIntent.setType("audio/*");
                    //   String mimetypes[]={"audio/*"};




                    //  cursor=makeAlbumSongCursor(selectedAlbumId);
                    ArrayList<Uri> uris=new ArrayList<Uri>();
                    //  while(cursor.moveToNext()){
                    for(int i=0;i<size;i++)
                    {
                        String id=selIdList.get(i);
                        String path=FirstFragment.idToPath.get(Long.parseLong(id));
                        path="file://"+path;

                        // Toast.makeText(getContext(),"Hi there", Toast.LENGTH_LONG).show();
                        Uri uri=Uri.parse(path);
                        uris.add(uri);
                    }
                    // Uri uri=Uri.parse(pathList.get(currPosition));

                    sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    //	 sharingIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                    // sharingIntent.putExtra(Intent.EXTRA_TEXT, songName);
                    Intent chooser=new Intent(Intent.ACTION_CHOOSER);
                    chooser.putExtra(Intent.EXTRA_INTENT, sharingIntent);
                    chooser.putExtra(Intent.EXTRA_TITLE, "title");
                    Intent addIntent=new Intent();
                    // addIntent.setComponent(new ComponentName("com.android.bluetooth","com.android.bluetooth.opp.BluetoothOppLauncherActivity"));
                    addIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    addIntent.setPackage("com.android.bluetooth");
                    addIntent.setType("*/*");
                    addIntent.setAction(Intent.ACTION_SEND_MULTIPLE);

                    Intent intentarray[]={addIntent};
                    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentarray);
                    //Toast.makeText(getContext(),"choice is "+choice, Toast.LENGTH_SHORT).show();
                    // sharingIntent.

                    startActivity(chooser);
                    break;

                case R.id.itemDelete:
            /*	AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        		builder.setTitle("Confirm Delete");
        		builder.setMessage("Are you sure you want to delete the selected songs?");
        		builder.setCancelable(true);
        		builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    	   ContentResolver resolver=SongsUnderTest.this.getApplicationContext().getContentResolver();
                    	   for(int i=0;i<size;i++)
     	        		  {

                    	   String selectedId=selIdList.get(i);
                    	   String songName=idToName.get(Long.parseLong(selectedId));
                    	   int rowsDeleted	=resolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media._ID + " LIKE \"" + selectedId + "\"", null);
                    	   File file = new File(idToPath.get(Long.parseLong(selectedId)));
                    	   boolean deleted = file.delete();
     	        		  }
                    	   Toast.makeText(SongsUnderTest.this, size+ " Songs deleted ",Toast.LENGTH_SHORT).show();
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
            	*/

                    UtilFunctions.deletePopUp(SongsUnderTest.this, SongsUnderTest.this, selIdList, "Are you sure you want to delete the selected Songs?","Songs deleted");
                    break;
                case R.id.itemToPlaylist :

                    Intent intent=new Intent(SongsUnderTest.this,PlayListSelectionTest.class);
                    // intent.putExtra("songName",songName);

                    intent.putExtra("selectedIdList",selIdList);
                    startActivity(intent);
                    break;



            }

            return false;
        }



        private ArrayList<String> getidList(){
            List<Integer> selItems = tra.getSelectedItems();
            ArrayList<String> selIdList = new ArrayList<String>();
            int size = selItems.size();
            int i;
            String title;
            for(i=0;i<size;i++){
                //title =   selCards.get(i).getTitle();
                String id=tracks.get(selItems.get(i)).getTrackId();
                selIdList.add(id);

            }
            Collections.reverse(selIdList); // PROBLEM MAY ARISE
            return selIdList;

        }

        private ArrayList<String> getsongNameList(){
            List<Integer> selItems = tra.getSelectedItems();
            ArrayList<String> selNameList = new ArrayList<String>();
            int size = selItems.size();
            int i;
            String title;
            for(i=0;i<size;i++){
                //title =   selCards.get(i).getTitle();
                String id=tracks.get(selItems.get(i)).getTrackName();
                selNameList.add(id);

            }
            Collections.reverse(selNameList); // PROBLEM MAY ARISE
            return selNameList;

        }
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            tra.clearSelection();
            MainActivity.isLongClickOn = false;
            actionMode = null;
            MainActivity.mToolbar.getLayoutParams().height = MainActivity.mActionBarSize;
        }
    }

    private final Cursor makeAlbumSongCursor(final Long albumId) {
        final StringBuilder selection = new StringBuilder();
        selection.append(MediaStore.Audio.AudioColumns.IS_MUSIC + "=1");
        selection.append(" AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''");
        selection.append(" AND " + MediaStore.Audio.AudioColumns.ALBUM_ID + "=" + albumId);
        return getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {
		/* 0 */ MediaStore.Audio.AudioColumns._ID,
		/* 1 */ MediaStore.Audio.AudioColumns.TITLE,
		/* 2 */ MediaStore.Audio.AudioColumns.ALBUM,
		/* 3 */ MediaStore.Audio.AudioColumns.ARTIST,
		/* 4 */ MediaStore.Audio.AudioColumns.DATA,
		/* 5 */ MediaStore.Audio.AudioColumns.DURATION
                }, selection.toString(), null, MediaStore.Audio.Media.TRACK + ", " + MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
    }

    private final Cursor makeArtistSongCursor(final Long artistId) {
        final StringBuilder selection = new StringBuilder();
        selection.append(MediaStore.Audio.AudioColumns.IS_MUSIC + "=1");
        selection.append(" AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''");
        selection.append(" AND " + MediaStore.Audio.AudioColumns.ARTIST_ID + "=" + artistId);
        return getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {
		/* 0 */ MediaStore.Audio.AudioColumns._ID,
		/* 1 */ MediaStore.Audio.AudioColumns.TITLE,
		/* 2 */ MediaStore.Audio.AudioColumns.ALBUM,
		/* 3 */ MediaStore.Audio.AudioColumns.ARTIST,
		/* 4 */ MediaStore.Audio.AudioColumns.DATA,
		/* 5 */ MediaStore.Audio.AudioColumns.DURATION
                }, selection.toString(), null, MediaStore.Audio.Media.TRACK + ", " + MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
    }

    private final Cursor makeGenreSongCursor(final Long genreId) {
        final StringBuilder selection = new StringBuilder();
        selection.append(MediaStore.Audio.AudioColumns.IS_MUSIC + "=1");
        selection.append(" AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''");
        selection.append(" AND " + BaseColumns._ID + "=" + genreId);
        return getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {
		/* 0 */ MediaStore.Audio.AudioColumns._ID,
		/* 1 */ MediaStore.Audio.AudioColumns.TITLE,
		/* 2 */ MediaStore.Audio.AudioColumns.ALBUM,
		/* 3 */ MediaStore.Audio.AudioColumns.ARTIST,
		/* 4 */ MediaStore.Audio.AudioColumns.DATA,
		/* 5 */ MediaStore.Audio.AudioColumns.DURATION
                }, selection.toString(), null, MediaStore.Audio.Media.TRACK + ", " + MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
    }
}
