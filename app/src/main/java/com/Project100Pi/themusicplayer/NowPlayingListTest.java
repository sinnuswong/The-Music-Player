package com.Project100Pi.themusicplayer;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;

import java.util.ArrayList;

import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

/**
 * Created by BalachandranAR on 9/19/2015.
 */
public class NowPlayingListTest extends AppCompatActivity implements NowPlayingRecyclerAdapter.OnDragStartListener {
    ArrayList<TrackObject> tracks;
    NowPlayingRecyclerAdapter tra;
    private ItemTouchHelper mItemTouchHelper;
     boolean isInit = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.now_playing_list_test);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Now Playing");
        final RecyclerView firstFragRecycler = (RecyclerView)findViewById(R.id.firstFragRecycler);
        firstFragRecycler.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        firstFragRecycler.setLayoutManager(llm);
        //MainActivity.idList = new ArrayList<String>();
        tracks = new ArrayList<TrackObject>();
        int nowPlayingSize = songInfoObj.nowPlayingList.size();
        for(int i=0;i<nowPlayingSize;i++){
            tracks.add(MainActivity.idToTrackObj.get(Long.parseLong(songInfoObj.nowPlayingList.get(i))));
        }

        tra = new NowPlayingRecyclerAdapter(tracks,songInfoObj.nowPlayingList,NowPlayingListTest.this,NowPlayingListTest.this);
        firstFragRecycler.setAdapter(tra);
        firstFragRecycler.setItemAnimator(new DefaultItemAnimator());

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(tra,true,true);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(firstFragRecycler);

        final VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) findViewById(R.id.fast_scroller);

        // Connect the recycler to the scroller (to let the scroller scroll the list)
        fastScroller.setRecyclerView(firstFragRecycler);
        // Connect the scroller to the recycler (to let the recycler scroll the scroller's handle)
       firstFragRecycler.setOnScrollListener(fastScroller.getOnScrollListener());
        if(songInfoObj.currPlayPos < 5) {
            firstFragRecycler.scrollToPosition(songInfoObj.currPlayPos);
        }else{
            firstFragRecycler.scrollToPosition(songInfoObj.currPlayPos - 2);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                onBackPressed();;
                return true;
            default:
                break;
        }
        return true ;
    }

    @Override
    public void onDragStarted(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
