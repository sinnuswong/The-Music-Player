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
 * Created by BalachandranAR on 8/30/2015.
 */
public class AlbumRecyclerAdapter extends SelectableAdapter<AlbumRecyclerAdapter.AlbumViewHolder> implements BubbleTextGetter{

     static List<AlbumInfo> albums;
    Activity mactivity;
    private ClickInterface clickListener;

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_layout_test,parent,false);
        AlbumViewHolder avh = new AlbumViewHolder(mactivity,v,clickListener);
        return  avh;
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, final int position) {
        if(position%2 != 0) {
           holder.cv.setBackgroundColor(Color.parseColor("#3D3D3D"));

        }else{
            holder.cv.setBackgroundColor(Color.parseColor("#484848"));
        }

        holder.albumName.setText(albums.get(position).getAlbumName());
        holder.artistName.setText(albums.get(position).getArtistName());
        holder.noOfSongs.setText(albums.get(position).getNoOfSongs()+" tracks");
        holder.overflowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.secondOverflowReaction(v,mactivity,albums.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.albums.size();
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        return Character.toString(albums.get(pos).getAlbumName().charAt(0));
    }
    // private ClickInterface clickListener;

    public static class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        CardView cv;
        TextView albumName,artistName,noOfSongs;
        ImageView overflowButton;
        Activity viewActivity;
        private ClickInterface listener;

        public AlbumViewHolder (Activity con,View itemView ,ClickInterface listener){
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            albumName = (TextView)itemView.findViewById(R.id.playList_name);
            artistName = (TextView)itemView.findViewById(R.id.album_artist);
            noOfSongs = (TextView)itemView.findViewById(R.id.artist_noOfSongs);
            viewActivity = con;
            overflowButton = (ImageView) itemView.findViewById((R.id.my_overflow));
            this.listener = listener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View v) {

            if(!MainActivity.isLongClickOn) {
                Intent intent=new Intent(viewActivity,SongsUnderTest.class);
                intent.putExtra("X","Album");
                intent.putExtra("id",albums.get(getAdapterPosition()).getAlbumId());
                viewActivity.startActivity(intent);
                return;
            }
            if (listener != null) {
                listener.onItemClicked(getAdapterPosition());
            }

        }

        @Override
        public boolean onLongClick(View v) {
            if (listener != null) {
                return listener.onItemLongClicked(getAdapterPosition());
            }
            return false;
        }
    }

    public AlbumRecyclerAdapter(ClickInterface clickListener,List<AlbumInfo> albumList,Activity act){
        this.clickListener = clickListener;
        this.albums = albumList;
        this.mactivity = act;
    }

}
