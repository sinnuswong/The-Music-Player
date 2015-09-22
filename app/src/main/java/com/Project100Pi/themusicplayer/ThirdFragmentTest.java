package com.Project100Pi.themusicplayer;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by BalachandranAR on 8/30/2015.
 */
public class ThirdFragmentTest extends Fragment implements  ClickInterface{

    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;
    ArrayList<ArtistInfo> artists ;
    ArtistRecyclerAdapter ara;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v =inflater.inflate(R.layout.third_fragment_test, container, false);
        final RecyclerView thirdFragRecycler = (RecyclerView)v.findViewById(R.id.thirdFragRecycler);
        thirdFragRecycler.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity().getApplicationContext());
        thirdFragRecycler.setLayoutManager(llm);

        artists = new ArrayList<ArtistInfo>();
        String artistName="";
        Long artistId;
        int noOfSongs,noOfAlbums;
        String[] projection = new String[] {BaseColumns._ID, MediaStore.Audio.ArtistColumns.ARTIST, MediaStore.Audio.ArtistColumns.NUMBER_OF_TRACKS, MediaStore.Audio.ArtistColumns.NUMBER_OF_ALBUMS };
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = MediaStore.Audio.Artists.DEFAULT_SORT_ORDER;
        Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sortOrder);

        int i = 0;
        while (cursor.moveToNext()) {
            artistName = cursor.getString(1);
            noOfSongs = cursor.getInt(2);
            noOfAlbums = cursor.getInt(3);
            artistId = cursor.getLong(0);
            ArtistInfo thisSong = new ArtistInfo(i,artistId,artistName,noOfAlbums,noOfSongs);
            artists.add(thisSong);
            i++;
        }

        ara = new ArtistRecyclerAdapter(this,artists,getActivity());
        thirdFragRecycler.setAdapter(ara);
       thirdFragRecycler.setItemAnimator(new DefaultItemAnimator());
        FastScroller fastScroller=(FastScroller)v.findViewById(R.id.thirdfastscroller);
        fastScroller.setRecyclerView(thirdFragRecycler);

        return v;
    }

    public static ThirdFragmentTest newInstance(String text) {

        ThirdFragmentTest f = new ThirdFragmentTest();
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
        ara.toggleSelection(position);
        int count = ara.getSelectedItemCount();

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

                case R.id.itemPlay:

                    UtilFunctions.playSelectedSongfromMultiChoice(getActivity(), selIdList, "artist",false);
                    break;

                case R.id.itemPlayNext:
                    UtilFunctions.playSongsNextfromMultiChoice(getActivity(), selIdList, "artist");
                    break;
                case R.id.itemAddQueue:

                    UtilFunctions.AddToQueuefromMultiChoice(getActivity(), selIdList, "artist");
                    break;

                case R.id.itemShare:
                    startActivity(UtilFunctions.shareMultiple(getActivity(), selIdList, "artist"));
                    break;

                case R.id.itemDelete:
                    UtilFunctions.deleteMultiplePopUp(getActivity(), getActivity(), selIdList, "Are you sure you want to delete the selected artists?","artists deleted","artist");
                    break;
                case R.id.itemToPlaylist :
                    startActivity(UtilFunctions.addMultipletoPlaylist(getActivity(), selIdList, "artist"));
                    break;

                case R.id.itemShuffle:
                    UtilFunctions.playSelectedSongfromMultiChoice(getActivity(), selIdList, "artist",true);
                    break;

            }

            return true;
        }



        private ArrayList<String> getidList(){
            List<Integer> selItems = ara.getSelectedItems();
            ArrayList<String> selIdList = new ArrayList<String>();
            int size = selItems.size();
            int i;
            String title;
            for(i=0;i<size;i++){
                //title =   selCards.get(i).getTitle();
                String id=artists.get(selItems.get(i)).getArtistId().toString();
                selIdList.add(id);

            }
            Collections.reverse(selIdList); // PROBLEM MAY ARISE
            return selIdList;

        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            ara.clearSelection();
            MainActivity.isLongClickOn = false;
            actionMode = null;
            MainActivity.mToolbar.getLayoutParams().height = MainActivity.mActionBarSize;
        }
    }


}
