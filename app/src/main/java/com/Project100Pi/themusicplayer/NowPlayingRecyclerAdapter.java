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
public class NowPlayingRecyclerAdapter extends SelectableAdapter<NowPlayingRecyclerAdapter.NowPlayingViewHolder> implements ItemTouchHelperAdapter {

    List<TrackObject> tracks;
    static ArrayList<String > idList;
    Activity mactivity;

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        TrackObject prev = tracks.remove(fromPosition);
        String prevStr = songInfoObj.nowPlayingList.remove(fromPosition);
        tracks.add(toPosition,prev);
        songInfoObj.nowPlayingList.add(toPosition,prevStr);
        songInfoObj.currPlayPos= songInfoObj.nowPlayingList.indexOf(songInfoObj.songId.toString());
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        songInfoObj.nowPlayingList.remove(tracks.get(position).getTrackId().toString());
        songInfoObj.initialPlayingList.remove(tracks.get(position).getTrackId().toString());
        tracks.remove(position);
        if(songInfoObj.currPlayPos == position){
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
        }
        notifyItemRemoved(position);
    }


    public static class NowPlayingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,ItemTouchHelperViewHolder{
        CardView cv;
        TextView trackName;
        TextView trackArtist;
        TextView trackDuration;
        ImageView overflowButton;
        Activity viewActivity;

        public NowPlayingViewHolder(Activity con,View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            trackName = (TextView) itemView.findViewById(R.id.playlist_name);
            trackArtist = (TextView) itemView.findViewById(R.id.track_artist);
            trackDuration = (TextView) itemView.findViewById(R.id.artist_noOfSongs);
            viewActivity= con;
            overflowButton=(ImageView)itemView.findViewById(R.id.my_overflow);
            itemView.setOnClickListener(this);

        }



        @Override
        public void onClick(View v) {
               // UtilFunctions.playSelectedSongs(viewActivity, idList, getAdapterPosition(), false);
            //Play song staying in NowPlayingListTest activity.
            try {
                PlayHelperFunctions.isSongPlaying = true;
                PlayHelperFunctions.audioPlayer((String) PlayHelperFunctions.setPlaySongInfo(Long.parseLong(songInfoObj.nowPlayingList.get(getAdapterPosition()))),1);
                songInfoObj.currPlayPos = getAdapterPosition();
            } catch(Exception e){

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

    public NowPlayingRecyclerAdapter(Activity act,List<TrackObject> trackList){
        this.tracks = trackList;
        this.mactivity = act;

    }

    public NowPlayingRecyclerAdapter(List<TrackObject> trackList,ArrayList<String> idList,Activity act){
        super();
        this.tracks = trackList;
        this.mactivity = act;
        this.idList = idList;
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
    public void onBindViewHolder(NowPlayingViewHolder trackViewHolder, final int i) {

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