package com.Project100Pi.themusicplayer;

/**
 * Created by BalachandranAR on 11/14/2015.
 */
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

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
        import java.util.List;
public class AlbumGridRecyclerAdapter  extends SelectableAdapter<AlbumGridRecyclerAdapter.AlbumViewHolder> {

    static List<AlbumInfo> albums;
    Activity mactivity;
    private ClickInterface clickListener;

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_grid_layout, parent, false);
        AlbumViewHolder avh = new AlbumViewHolder(mactivity, v, clickListener);
        return avh;
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, final int position) {
        holder.albumName.setText(albums.get(position).getAlbumName());
        holder.artistName.setText(albums.get(position).getArtistName());
        File myAlbumArtFile =  null;
        try{
             myAlbumArtFile = new File(albums.get(position).getAlbumArtPath());
        }
        catch (Exception e){}
        Picasso.with(mactivity)
                .load(myAlbumArtFile)
                .placeholder(R.drawable.music_default)
                .error(R.drawable.music_default)
                .into(holder.albumArt);
        holder.overflowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.secondOverflowReaction(v, mactivity, albums.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.albums.size();
    }


    // private ClickInterface clickListener;

    public static class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView albumName, artistName;
        ImageView overflowButton,albumArt;
        Activity viewActivity;
        private ClickInterface listener;

        public AlbumViewHolder(Activity con, View itemView, ClickInterface listener) {
            super(itemView);
            albumName = (TextView) itemView.findViewById(R.id.album_name);
            artistName = (TextView) itemView.findViewById(R.id.album_artist);
            albumArt = (ImageView) itemView.findViewById(R.id.album_art);
            viewActivity = con;
            overflowButton = (ImageView) itemView.findViewById((R.id.my_overflow));
            this.listener = listener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View v) {

            if (!MainActivity.isLongClickOn) {
                Intent intent = new Intent(viewActivity, SongsUnderTest.class);
                intent.putExtra("X", "Album");
                intent.putExtra("id", albums.get(getAdapterPosition()).getAlbumId());
                intent.putExtra("title",albums.get(getAdapterPosition()).getAlbumName());
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

    public AlbumGridRecyclerAdapter(ClickInterface clickListener, List<AlbumInfo> albumList, Activity act) {
        this.clickListener = clickListener;
        this.albums = albumList;
        this.mactivity = act;
    }
}