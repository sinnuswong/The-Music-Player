package com.Project100Pi.themusicplayer;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

/**
 * Created by BalachandranAR on 9/19/2015.
 */
public class NowPlayingListTest extends AppCompatActivity implements NowPlayingRecyclerAdapter.OnDragStartListener {
    ArrayList<TrackObject> tracks;
    NowPlayingRecyclerAdapter tra;
    private ItemTouchHelper mItemTouchHelper;
    RelativeLayout outerWindow;
     boolean isInit = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.now_playing_list_test);
        outerWindow = (RelativeLayout)findViewById(R.id.nowPlayingOuter);
        outerWindow.setBackgroundColor(ColorUtils.primaryBgColor);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Now Playing");
        Button saveToPlaylist = (Button)findViewById(R.id.save_to_playlist);
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
        fastScroller.setHandleColor(ColorUtils.accentColor);
        if(songInfoObj.currPlayPos < 5) {
            firstFragRecycler.scrollToPosition(songInfoObj.currPlayPos);
        }else{
            firstFragRecycler.scrollToPosition(songInfoObj.currPlayPos - 2);
        }

        saveToPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NowPlayingListTest.this, PlayListSelectionTest.class);
                intent.putExtra("selectedIdList", songInfoObj.nowPlayingList);
                startActivity(intent);
            }
        });

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
                Intent intent=new Intent(NowPlayingListTest.this,SearchResultsActivity.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                onBackPressed();;
                return true;

            default:
                break;
        }

        return true;
    }

    @Override
    public void onDragStarted(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
