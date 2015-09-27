package com.Project100Pi.themusicplayer;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by BalachandranAR on 8/30/2015.
 */
public class FifthFragmentTest extends Fragment implements  ClickInterface{

    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;
    ArrayList<PlaylistInfo> playlists ;
    PlaylistRecyclerAdapter pra;
    RecyclerView fifthFragRecycler;
    final String[] PROJECTION_PLAYLIST = new String[] {
            MediaStore.Audio.Playlists._ID,
            MediaStore.Audio.Playlists.NAME,
            MediaStore.Audio.Playlists.DATA
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v =inflater.inflate(R.layout.fifth_frag_test, container, false);
         fifthFragRecycler = (RecyclerView)v.findViewById(R.id.fifthFragRecycler);
        fifthFragRecycler.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity().getApplicationContext());
        fifthFragRecycler.setLayoutManager(llm);

        TextView createNewPlaylist = (TextView)v.findViewById(R.id.create_new_playlist);
        createNewPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });
        populateRecyclerList();

       // FastScroller fastScroller=(FastScroller)v.findViewById(R.id.fifthfastscroller);
        //fastScroller.setRecyclerView(fifthFragRecycler);

        return v;
    }


    public void populateRecyclerList(){

        playlists = new ArrayList<PlaylistInfo>();
        Cursor cursor=getActivity().getApplicationContext().getContentResolver().query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, PROJECTION_PLAYLIST, null, null,MediaStore.Audio.Playlists.DATE_MODIFIED);
        int i =0;
        while (cursor.moveToNext()) {
            PlaylistInfo thisPlaylist = new PlaylistInfo(i,cursor.getLong(0),cursor.getString(1));
            playlists.add(thisPlaylist);
            i++;
        }

        pra = new PlaylistRecyclerAdapter(this,playlists,getActivity(),false,null);
        fifthFragRecycler.setAdapter(pra);
        fifthFragRecycler.setItemAnimator(new DefaultItemAnimator());
    }
    private void showInputDialog() {
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.dialog_box, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        UtilFunctions.createNewPlayList(editText.getText().toString(),getActivity());
                       populateRecyclerList();
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



    public  Cursor makePlaylistSongCursor(long id)
    {
        String[] projection1 = {
                MediaStore.Audio.Playlists.Members.AUDIO_ID,
                MediaStore.Audio.Playlists.Members.TITLE,
                MediaStore.Audio.Playlists.Members.PLAY_ORDER,
                MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER

        };
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", id);
        Cursor cursor =getActivity().getApplicationContext().getContentResolver().query(
                uri,
                projection1,
                MediaStore.Audio.Playlists.Members.PLAYLIST_ID+ " = "+id+"",
                null,
                MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER );

        return cursor;
    }

    public static FifthFragmentTest newInstance(String text) {

        FifthFragmentTest f = new FifthFragmentTest();
        return f;
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
            actionMode= ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallback);
        }

        toggleSelection(position);

        return true;
    }

    private void toggleSelection(int position) {
        pra.toggleSelection(position);
        int count = pra.getSelectedItemCount();

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
            final int size=selIdList.size();
            Cursor cursor=null;
            switch(item.getItemId()){
                case R.id.itemShuffle:
                    UtilFunctions.playSelectedSongfromMultiChoice(getActivity(), selIdList, "playlist", true);
                    break;

                case R.id.itemPlay:

                    UtilFunctions.playSelectedSongfromMultiChoice(getActivity(), selIdList, "playlist",false);
                    break;
                case R.id.itemPlayNext:
                    UtilFunctions.playSongsNextfromMultiChoice(getActivity(), selIdList, "playlist");
                    break;
                case R.id.itemAddQueue:

                    UtilFunctions.AddToQueuefromMultiChoice(getActivity(), selIdList, "playlist");
                    break;
                case R.id.itemShare:

                    startActivity(UtilFunctions.shareMultiple(getActivity(), selIdList,"playlist"));
                    break;

                case R.id.itemDelete:

                    break;



            }

            return true;
        }



        private ArrayList<String> getidList(){
            List<Integer> selItems = pra.getSelectedItems();
            ArrayList<String> selIdList = new ArrayList<String>();
            int size = selItems.size();
            int i;
            String title;
            for(i=0;i<size;i++){
                //title =   selCards.get(i).getTitle();
                String id=playlists.get(selItems.get(i)).getPlaylistId().toString();
                selIdList.add(id);

            }
            Collections.reverse(selIdList); // PROBLEM MAY ARISE
            return selIdList;

        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            pra.clearSelection();
            MainActivity.isLongClickOn = false;
            actionMode = null;
            MainActivity.mToolbar.getLayoutParams().height = MainActivity.mActionBarSize;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        populateRecyclerList();
    }
}
