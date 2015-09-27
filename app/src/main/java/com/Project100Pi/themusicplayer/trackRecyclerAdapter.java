package com.Project100Pi.themusicplayer;


import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BalachandranAR on 8/24/2015.
 */
public class trackRecyclerAdapter extends SelectableAdapter<trackRecyclerAdapter.TrackViewHolder> implements BubbleTextGetter{

    List<TrackObject> tracks;
   static ArrayList<String > idList;
   Activity mactivity;
    private ClickInterface clickListener;

    @Override
    public String getTextToShowInBubble(int pos) {
        return Character.toString(tracks.get(pos).getTrackName().charAt(0));
    }

    public static class TrackViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        CardView cv;
        TextView trackName;
        TextView trackArtist;
        TextView trackDuration;
        ImageView overflowButton;
       Activity viewActivity;
        private ClickInterface listener;

        public TrackViewHolder(Activity con,View itemView,ClickInterface listener) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            trackName = (TextView) itemView.findViewById(R.id.playList_name);
            trackArtist = (TextView) itemView.findViewById(R.id.track_artist);
            trackDuration = (TextView) itemView.findViewById(R.id.artist_noOfSongs);
            viewActivity= con;
            overflowButton=(ImageView)itemView.findViewById(R.id.my_overflow);
            this.listener = listener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }
        
        @Override
        public boolean onLongClick(View v) {
            if (listener != null) {
                return listener.onItemLongClicked(getAdapterPosition());
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            if(!MainActivity.isLongClickOn) {
                UtilFunctions.playSelectedSongs(viewActivity, idList, getAdapterPosition(), false);
                return;
            }
            if (listener != null) {
                listener.onItemClicked(getAdapterPosition());
            }
        }


    }

    public trackRecyclerAdapter(Activity act,List<TrackObject> trackList){
        this.tracks = trackList;
        this.mactivity = act;

    }

    public trackRecyclerAdapter(ClickInterface clickListener,List<TrackObject> trackList,ArrayList<String> idList,Activity act){
        super();
        this.tracks = trackList;
        this.clickListener = clickListener;
        this.mactivity = act;
        this.idList = idList;
    }
    @Override
    public int getItemCount() {
        return tracks.size();
    }

    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.track_layout_test, viewGroup, false);


        TrackViewHolder pvh = new TrackViewHolder(mactivity,v,clickListener);
        return pvh;
    }

    @Override
    public void onBindViewHolder(TrackViewHolder trackViewHolder, final int i) {
        if(i%2 != 0) {
            trackViewHolder.cv.setBackgroundColor(Color.parseColor("#3D3D3D"));

        }else{
            trackViewHolder.cv.setBackgroundColor(Color.parseColor("#484848"));
        }
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