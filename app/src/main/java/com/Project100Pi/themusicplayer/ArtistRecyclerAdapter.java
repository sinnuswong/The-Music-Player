package com.Project100Pi.themusicplayer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by BalachandranAR on 8/31/2015.
 */
public class ArtistRecyclerAdapter extends SelectableAdapter<ArtistRecyclerAdapter.ArtistViewHolder>implements BubbleTextGetter{
static List<ArtistInfo> artists;
    Activity mactivity;
    private ClickInterface clickListener;

    public static class ArtistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        CardView cv;
        TextView artistName;
        TextView noOfAlbums;
        TextView noOfTracks;
        ImageView overflowButton;
        Activity viewActivity;
    private ClickInterface listener;
        public ArtistViewHolder(Activity con,View itemView,ClickInterface listener) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            artistName = (TextView) itemView.findViewById(R.id.playlist_name);
            noOfAlbums = (TextView) itemView.findViewById(R.id.artist_noAlbum);
            noOfTracks = (TextView) itemView.findViewById(R.id.artist_noOfSongs);
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
                Intent intent=new Intent(viewActivity,SongsUnderTest.class);
                intent.putExtra("X","Artist");
                intent.putExtra("id",artists.get(getAdapterPosition()).getArtistId());
                viewActivity.startActivity(intent);
                return;
            }
            if (listener != null) {
                listener.onItemClicked(getAdapterPosition());
            }
        }

    }

    public ArtistRecyclerAdapter(ClickInterface clickListener,List<ArtistInfo> objs,Activity act){
        this.artists = objs;
        this.mactivity = act;
        this.clickListener = clickListener;
    }

    @Override
    public ArtistRecyclerAdapter.ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_layout_test, parent, false);


        ArtistViewHolder avh = new ArtistViewHolder(mactivity,v,clickListener);
        return avh;
    }

    @Override
    public void onBindViewHolder(ArtistRecyclerAdapter.ArtistViewHolder holder, final int position) {
        if(position%2 == 0) {
            holder.cv.setBackgroundColor(Color.parseColor("#3D3D3D"));

        }else{
            holder.cv.setBackgroundColor(Color.parseColor("#484848"));
        }
        holder.artistName.setText(artists.get(position).getArtistName());
        holder.noOfAlbums.setText(artists.get(position).getNoOfAlbums()+" Albums");
        holder.noOfTracks.setText(artists.get(position).getNoOfSongs()+" Tracks");
        holder.overflowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.thirdOverflowReaction(v,mactivity,artists.get(position));
            }
        });


    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        return Character.toString(artists.get(pos).getArtistName().charAt(0));
    }
}
