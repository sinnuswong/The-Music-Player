package com.Project100Pi.themusicplayer;


import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.provider.MediaStore;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by BalachandranAR on 8/24/2015.
 */
public class NowPlayingRecyclerAdapter extends SelectableAdapter<NowPlayingRecyclerAdapter.NowPlayingViewHolder> implements ItemTouchHelperAdapter {

    List<TrackObject> tracks;
    static ArrayList<String > idList;
    Activity mactivity;
    private OnDragStartListener mDragStartListener;
    Long playListid;
    static NowPlayingViewHolder nowPlayingHolder;

    @Override
    public void onItemMove(int fromPosition, int toPosition) {

            TrackObject prev = tracks.remove(fromPosition);
            String prevStr = songInfoObj.nowPlayingList.remove(fromPosition);
            tracks.add(toPosition, prev);
            songInfoObj.nowPlayingList.add(toPosition, prevStr);
            songInfoObj.currPlayPos = songInfoObj.nowPlayingList.indexOf(songInfoObj.songId.toString());
        if(mactivity instanceof SongsUnderTest){
            idList.remove(fromPosition);
            idList.add(toPosition,prevStr);
            ContentResolver resolver=mactivity.getContentResolver();
            boolean result= MediaStore.Audio.Playlists.Members.moveItem(resolver, playListid,fromPosition,toPosition);

        }
        notifyItemMoved(fromPosition, toPosition);

    }

    @Override
    public void onItemDismiss(int position) {
        if (mactivity instanceof NowPlayingListTest) {
            songInfoObj.nowPlayingList.remove(tracks.get(position).getTrackId().toString());
            songInfoObj.initialPlayingList.remove(tracks.get(position).getTrackId().toString());
            tracks.remove(position);
            if (songInfoObj.currPlayPos == position) {
                PlayHelperFunctions.mp.reset();
                songInfoObj.currPlayPos = (songInfoObj.currPlayPos) % songInfoObj.nowPlayingList.size();
                PlayHelperFunctions.getPlaySongInfo(Long.parseLong(songInfoObj.nowPlayingList.get(songInfoObj.currPlayPos)));
                try {
                    PlayHelperFunctions.mp.setDataSource(songInfoObj.playPath);
                    PlayHelperFunctions.mp.prepare();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }else{
                songInfoObj.currPlayPos = songInfoObj.nowPlayingList.indexOf(songInfoObj.songId.toString());
            }
            notifyItemRemoved(position);
           notifyDataSetChanged();
        }
    }

    public static class NowPlayingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,ItemTouchHelperViewHolder{
        CardView cv;
        TextView trackName;
        TextView trackArtist;
        TextView trackDuration;
        ImageView overflowButton,dragHandle;
        Activity viewActivity;
        GifImageView animatedBars;

        public NowPlayingViewHolder(Activity con,View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            trackName = (TextView) itemView.findViewById(R.id.playList_name);
            trackArtist = (TextView) itemView.findViewById(R.id.track_artist);
            trackDuration = (TextView) itemView.findViewById(R.id.artist_noOfSongs);
            viewActivity= con;
            overflowButton=(ImageView)itemView.findViewById(R.id.my_overflow);
            dragHandle=(ImageView)itemView.findViewById(R.id.drag_handle);
            animatedBars = (GifImageView)itemView.findViewById(R.id.animated_bars);
            itemView.setOnClickListener(this);

        }



        @Override
        public void onClick(View v) {
               // UtilFunctions.playSelectedSongs(viewActivity, idList, getAdapterPosition(), false);
            if(viewActivity instanceof NowPlayingListTest) {
                //Play song staying in NowPlayingListTest activity.
                try {
                    PlayHelperFunctions.isSongPlaying = true;
                    PlayHelperFunctions.audioPlayer((String) PlayHelperFunctions.setPlaySongInfo(Long.parseLong(songInfoObj.nowPlayingList.get(getAdapterPosition()))), 1);
                    songInfoObj.currPlayPos = getAdapterPosition();
                    nowPlayingHolder.animatedBars.setVisibility(View.GONE);
                    this.animatedBars.setVisibility(View.VISIBLE);
                   this.animatedBars.setAlpha(0.25f);
                    nowPlayingHolder = this;
                } catch (Exception e) {

                }

                }else if(viewActivity instanceof SongsUnderTest){
                UtilFunctions.playSelectedSongs(viewActivity, idList, getAdapterPosition(), false);
                return;
            }
            return;

        }


        @Override
        public void onItemSelected()
        {
          //  itemView.setBackgroundColor(Color.BLUE);
        }

        @Override
        public void onItemClear() {
     //       itemView.setBackgroundColor(0);
        }
    }

    public NowPlayingRecyclerAdapter(Activity act,List<TrackObject> trackList,OnDragStartListener dragStartListener){
        this.tracks = trackList;
        this.mactivity = act;
        this.mDragStartListener = dragStartListener;

    }

    public interface OnDragStartListener {
        void onDragStarted(RecyclerView.ViewHolder viewHolder);
    }

    public NowPlayingRecyclerAdapter(List<TrackObject> trackList,ArrayList<String> idList,Activity act,OnDragStartListener dragStartListener){
        super();
        this.tracks = trackList;
        this.mactivity = act;
        this.idList = idList;
        this.mDragStartListener = dragStartListener;
    }
    public NowPlayingRecyclerAdapter(List<TrackObject> trackList,ArrayList<String> idList,Activity act,OnDragStartListener dragStartListener,Long id){
        super();
        this.tracks = trackList;
        this.mactivity = act;
        this.idList = idList;
        this.mDragStartListener = dragStartListener;
        this.playListid = id;
    }
    @Override
    public int getItemCount() {
        return tracks.size();
    }

    @Override
    public NowPlayingViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.now_playing_list_inner, viewGroup, false);


        NowPlayingViewHolder pvh = new NowPlayingViewHolder(mactivity,v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final NowPlayingViewHolder trackViewHolder, final int i) {
        final Drawable dragIcon = mactivity.getResources().getDrawable(R.drawable.grab_material);
        dragIcon.setColorFilter(Color.parseColor("#C1C1C1"), PorterDuff.Mode.SRC_ATOP);
        trackViewHolder.dragHandle.setImageDrawable(dragIcon);
        trackViewHolder.dragHandle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onDragStarted(trackViewHolder);
                }
                return false;
            }
        });

        if(Long.parseLong(tracks.get(i).getTrackId()) == songInfoObj.songId && mactivity instanceof NowPlayingListTest){
            trackViewHolder.animatedBars.setVisibility(View.VISIBLE);
            trackViewHolder.animatedBars.setAlpha(0.25f);
            nowPlayingHolder = trackViewHolder;
        }else {
            trackViewHolder.animatedBars.setVisibility(View.GONE);
        }

        trackViewHolder.cv.setBackgroundColor(Color.parseColor("#3D3D3D"));
        trackViewHolder.trackName.setText(tracks.get(i).getTrackName());
        trackViewHolder.trackArtist.setText(tracks.get(i).getTrackArtist());
        trackViewHolder.trackDuration.setText(tracks.get(i).getTrackDuration());
        trackViewHolder.overflowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /// button click eventP
                MainActivity.overflowReaction(v,mactivity,tracks.get(i));


            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }




}