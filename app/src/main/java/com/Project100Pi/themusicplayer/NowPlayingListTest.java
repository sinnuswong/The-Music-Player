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
import android.view.View;
import android.widget.AbsListView;

import java.util.ArrayList;

import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

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
        int nowPlayingSize = songInfoObj.nowPlayingList.size();
        for(int i=0;i<nowPlayingSize;i++){
            tracks.add(MainActivity.idToTrackObj.get(Long.parseLong(songInfoObj.nowPlayingList.get(i))));
        }

        tra = new NowPlayingRecyclerAdapter(tracks,songInfoObj.nowPlayingList,NowPlayingListTest.this);
        firstFragRecycler.setAdapter(tra);
        firstFragRecycler.setItemAnimator(new DefaultItemAnimator());

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(tra);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(firstFragRecycler);

        final VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) findViewById(R.id.fast_scroller);

        // Connect the recycler to the scroller (to let the scroller scroll the list)
        fastScroller.setRecyclerView(firstFragRecycler);
        fastScroller.setVisibility(View.INVISIBLE);
        
        // Connect the scroller to the recycler (to let the recycler scroll the scroller's handle)
        firstFragRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && !fastScroller.isPressed()) {
                  fastScroller.setVisibility(View.INVISIBLE);
                }else if(newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING || newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    fastScroller.setVisibility(View.VISIBLE);
                }else{
                    fastScroller.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
       firstFragRecycler.setOnScrollListener(fastScroller.getOnScrollListener());
        if(songInfoObj.currPlayPos < 5) {
            firstFragRecycler.scrollToPosition(songInfoObj.currPlayPos);
        }else{
            firstFragRecycler.scrollToPosition(songInfoObj.currPlayPos - 2);
        }

    }
}
