package com.Project100Pi.themusicplayer;

/**
 * Created by BalachandranAR on 9/23/2015.
 */
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

public class SearchResultRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    static List<TrackObject> tracks,albums,artists;
    int trackSize,albumSize,artistSize;
    int needAlbum = 0;
    int needArtist = 0;
    int needTrack = 0;
    Activity mactivity;
    private ClickInterface clickListener;


    public static class SearchResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        CardView cv;
        TextView headingName;
        Activity viewActivity;
        private ClickInterface listener;

        public SearchResultViewHolder(Activity con,View itemView,ClickInterface listener) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            viewActivity= con;
            this.listener = listener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }

        @Override
        public void onClick(View v) {

               // UtilFunctions.playSelectedSongs(viewActivity, idList, getAdapterPosition(), false);
                return;

        }


    }

    public SearchResultRecyclerAdapter(Activity act,List<TrackObject> trackList){
        this.tracks = trackList;
        this.mactivity = act;

    }

    public SearchResultRecyclerAdapter(ClickInterface clickListener,List<TrackObject> albumList,List<TrackObject> artistList,List<TrackObject> trackList,ArrayList<String> idList,Activity act){
        super();
        this.tracks = trackList;
        this.albums = albumList;
        this.artists = artistList;
        this.clickListener = clickListener;
        this.mactivity = act;
        this.albumSize = this.albums.size();
        this.artistSize = this.artists.size();
        this.trackSize = this.tracks.size();
        if(this.albumSize >0) this.needAlbum = 1;
        if(this.artistSize >0) this.needArtist = 1;
        if(this.trackSize>0) this.needTrack = 1;
    }
    @Override
    public int getItemCount() {
        return (trackSize+albumSize+artistSize+needTrack+needArtist+needAlbum);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder avh = null ;
        if(i == 0 || i == albumSize+needAlbum || i == artistSize+albumSize+needAlbum+needArtist ){
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_result_heading_inner, viewGroup, false);
              avh= new SearchResultViewHolder(mactivity,v,clickListener);
        }else if( i>0 && i <=albumSize){
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.album_layout_test,viewGroup,false);
             avh = new AlbumRecyclerAdapter.AlbumViewHolder(mactivity,v,clickListener);
        }else if(i>albumSize+needAlbum && i<=albumSize+artistSize+needArtist ){
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.artist_layout_test, viewGroup, false);
             avh = new ArtistRecyclerAdapter.ArtistViewHolder(mactivity,v,clickListener);
        }else if(i>artistSize+albumSize+needAlbum+needArtist){
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.track_layout_test, viewGroup, false);
             avh= new trackRecyclerAdapter.TrackViewHolder(mactivity,v,clickListener);
        }

        return avh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int i) {
        if(holder instanceof trackRecyclerAdapter.TrackViewHolder) {
            trackRecyclerAdapter.TrackViewHolder trackViewHolder = (trackRecyclerAdapter.TrackViewHolder)holder;
            if (i % 2 == 0) {
               trackViewHolder.cv.setBackgroundColor(Color.parseColor("#3D3D3D"));

            } else {
                trackViewHolder.cv.setBackgroundColor(Color.parseColor("#484848"));
            }
            trackViewHolder.trackName.setText(tracks.get(i).getTrackName());
            trackViewHolder.trackArtist.setText(tracks.get(i).getTrackArtist());
            trackViewHolder.trackDuration.setText(tracks.get(i).getTrackDuration());
            trackViewHolder.overflowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /// button click eventP
                    MainActivity.overflowReaction(v, mactivity, tracks.get(i));


                }
            });
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }




}