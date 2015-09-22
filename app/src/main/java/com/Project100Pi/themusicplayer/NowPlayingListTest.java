package com.Project100Pi.themusicplayer;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.ArrayList;

/**
 * Created by BalachandranAR on 9/19/2015.
 */
public class NowPlayingListTest extends Activity {
    ArrayList<TrackObject> tracks;
    NowPlayingRecyclerAdapter tra;
    private ItemTouchHelper mItemTouchHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.now_playing_list_test);
        final RecyclerView firstFragRecycler = (RecyclerView)findViewById(R.id.firstFragRecycler);
        firstFragRecycler.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        firstFragRecycler.setLayoutManager(llm);
        MainActivity.idList = new ArrayList<String>();
        tracks = new ArrayList<TrackObject>();
        int nowPlayingSize = MainActivity.currSongInfo.nowPlayingList.size();
        for(int i=0;i<nowPlayingSize;i++){
            tracks.add(MainActivity.idToTrackObj.get(Long.parseLong(MainActivity.currSongInfo.nowPlayingList.get(i))));
        }

        tra = new NowPlayingRecyclerAdapter(tracks,MainActivity.currSongInfo.nowPlayingList,NowPlayingListTest.this);
        firstFragRecycler.setAdapter(tra);
        firstFragRecycler.setItemAnimator(new DefaultItemAnimator());

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(tra);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(firstFragRecycler);

    }
}
