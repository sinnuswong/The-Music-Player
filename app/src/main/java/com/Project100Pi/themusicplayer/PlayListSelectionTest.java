package com.Project100Pi.themusicplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by BalachandranAR on 9/21/2015.
 */
public class PlayListSelectionTest extends AppCompatActivity implements ClickInterface{

    ArrayList<PlaylistInfo> playlists ;
    PlaylistRecyclerAdapter pra;
    String selectedlist;
    ArrayList<String> audioIdList=null;
    RecyclerView fifthFragRecycler;
    int i =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fifth_frag_test);
        Intent intent=getIntent();
        //songName=i.getExtras().getString("songName");
        //	audioId=i.getLongExtra("selectedId",0);
        audioIdList=intent.getExtras().getStringArrayList("selectedIdList");

       fifthFragRecycler = (RecyclerView)findViewById(R.id.fifthFragRecycler);
        fifthFragRecycler.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(PlayListSelectionTest.this.getApplicationContext());
        fifthFragRecycler.setLayoutManager(llm);

        TextView createNewPlaylist = (TextView)findViewById(R.id.create_new_playlist);
        createNewPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });
    populateList();

    }

    void populateList(){

        playlists = new ArrayList<PlaylistInfo>();

        final String[] PROJECTION_PLAYLIST = new String[] {
                MediaStore.Audio.Playlists._ID,
                MediaStore.Audio.Playlists.NAME,
                MediaStore.Audio.Playlists.DATA
        };

        Cursor cursor=PlayListSelectionTest.this.getApplicationContext().getContentResolver().query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, PROJECTION_PLAYLIST, null, null,MediaStore.Audio.Playlists.DATE_MODIFIED);
        while (cursor.moveToNext()) {
            PlaylistInfo thisPlaylist = new PlaylistInfo(i,cursor.getLong(0),cursor.getString(1));
            playlists.add(thisPlaylist);
            i++;
        }

        pra = new PlaylistRecyclerAdapter(this,playlists,PlayListSelectionTest.this,true,audioIdList);
        fifthFragRecycler.setAdapter(pra);
        fifthFragRecycler.setItemAnimator(new DefaultItemAnimator());

    }
    @Override
    public void onItemClicked(int position) {

    }

    @Override
    public boolean onItemLongClicked(int position) {
        return false;
    }

    private void showInputDialog() {
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.dialog_box, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        selectedlist=editText.getText().toString();
                    //    playlistnames.add(editText.getText().toString());
                        createNewPlayList(editText.getText().toString());
                        populateList();

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
        // TODO Auto-generated method stub

    }

    public  void createNewPlayList(String name)
    {
        ContentValues mInserts = new ContentValues();
        mInserts.put(MediaStore.Audio.Playlists.NAME, name);
        mInserts.put(MediaStore.Audio.Playlists.DATE_ADDED, System.currentTimeMillis());
        mInserts.put(MediaStore.Audio.Playlists.DATE_MODIFIED, System.currentTimeMillis());
        ContentResolver mContentResolver=this.getApplicationContext().getContentResolver();
        Cursor cursor=null;
        Uri mUri = mContentResolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, mInserts);
        final String[] PROJECTION_PLAYLIST = new String[] {
                MediaStore.Audio.Playlists._ID,
                MediaStore.Audio.Playlists.NAME,
                MediaStore.Audio.Playlists.DATA
        };
        long mPlaylistId;
        if (mUri != null) {
            mPlaylistId = -1;
            //mResult = FM.SUCCESS;
            cursor = mContentResolver.query(mUri, PROJECTION_PLAYLIST, null, null,MediaStore.Audio.Playlists.DATE_MODIFIED);
            if (cursor != null) {
                cursor.moveToLast();
                // Save the newly created ID so it can be selected.  Names are allowed to be duplicated,
                // but IDs can never be.
                mPlaylistId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID));
                cursor.close();
            }


            UtilFunctions.addMultipleToPlaylist(getContentResolver(), audioIdList, mPlaylistId);
            //addToPlaylist(getContentResolver(), audioId, mPlaylistId);
            Toast.makeText(PlayListSelectionTest.this, "Added to " + name, Toast.LENGTH_SHORT).show();
            finish();

        }
    }
    public static void addToPlaylist(ContentResolver resolver, Long audioId,Long playlistID) {

        String[] cols = new String[] {
                "count(*)"
        };
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistID);
        Cursor cur = resolver.query(uri, cols, null, null, null);
        cur.moveToLast();
        final int base = cur.getInt(0);
        cur.close();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, Long.valueOf(base));
        values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audioId);
        resolver.insert(uri, values);
    }

}
