package com.Project100Pi.themusicplayer;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by BalachandranAR on 8/30/2015.
 */
public class FourthFragmentTest extends Fragment implements  ClickInterface{

    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;
    ArrayList<GenreInfo> genres ;
    GenreRecyclerAdapter gra;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v =inflater.inflate(R.layout.forth_frag_test, container, false);
        final RecyclerView fourthFragRecycler = (RecyclerView)v.findViewById(R.id.forthFragRecycler);
        fourthFragRecycler.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity().getApplicationContext());
        fourthFragRecycler.setLayoutManager(llm);


        genres = new ArrayList<GenreInfo>();
        String genreName="";
        Long genreId;
      //  int noOfSongs=0;
        String[] projection = new String[] {BaseColumns._ID, MediaStore.Audio.GenresColumns.NAME, };
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = MediaStore.Audio.Genres.DEFAULT_SORT_ORDER;
        Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sortOrder);
        int i = 0;
        while (cursor.moveToNext()) {
            genreName = cursor.getString(1);
            genreId = cursor.getLong(0);
            GenreInfo thisGenre = new GenreInfo(i,genreId,genreName);
            genres.add(thisGenre);
            i++;
        }

        gra = new GenreRecyclerAdapter(this,genres,getActivity());
        fourthFragRecycler.setAdapter(gra);
        fourthFragRecycler.setItemAnimator(new DefaultItemAnimator());
        FastScroller fastScroller=(FastScroller)v.findViewById(R.id.forthfastscroller);
        fastScroller.setRecyclerView(fourthFragRecycler);

        return v;
    }

    public static FourthFragmentTest newInstance(String text) {

        FourthFragmentTest f = new FourthFragmentTest();
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
        gra.toggleSelection(position);
        int count = gra.getSelectedItemCount();

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
                    UtilFunctions.playSelectedSongfromMultiChoice(getActivity(), selIdList, "genre", true);
                    break;
                case R.id.itemPlay:

                    UtilFunctions.playSelectedSongfromMultiChoice(getActivity(), selIdList, "genre", false);
                    break;
                case R.id.itemPlayNext:
                    UtilFunctions.playSongsNextfromMultiChoice(getActivity(), selIdList, "genre");
                    break;
                case R.id.itemAddQueue:

                    UtilFunctions.AddToQueuefromMultiChoice(getActivity(), selIdList, "genre");
                    break;
                case R.id.itemShare:
                    startActivity(UtilFunctions.shareMultiple(getActivity(), selIdList, "genre"));
                    break;
                case R.id.itemDelete:
                    UtilFunctions.deleteMultiplePopUp(getActivity(), getActivity(), selIdList, "Are you sure you want to delete the selected genres?", "Genres deleted", "genre");
                    break;
                case R.id.itemToPlaylist :
                    startActivity(UtilFunctions.addMultipletoPlaylist(getActivity(), selIdList, "genre"));
                    break;



            }

            return true;
        }



        private ArrayList<String> getidList(){
            List<Integer> selItems = gra.getSelectedItems();
            ArrayList<String> selIdList = new ArrayList<String>();
            int size = selItems.size();
            int i;
            String title;
            for(i=0;i<size;i++){
                //title =   selCards.get(i).getTitle();
                String id=genres.get(selItems.get(i)).getGenreId().toString();
                selIdList.add(id);

            }
            Collections.reverse(selIdList); // PROBLEM MAY ARISE
            return selIdList;

        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            gra.clearSelection();
            MainActivity.isLongClickOn = false;
            actionMode = null;
            MainActivity.mToolbar.getLayoutParams().height = MainActivity.mActionBarSize;
        }
    }



}
