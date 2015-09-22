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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by BalachandranAR on 8/31/2015.
 */
public class PlaylistRecyclerAdapter extends SelectableAdapter<PlaylistRecyclerAdapter.PlaylistViewHolder>{
    static List<PlaylistInfo> playlists;
    Activity mactivity;
    private ClickInterface clickListener;
    private static Boolean isSelect = false;
    static ArrayList<String > audioIdList;

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        CardView cv;
        TextView playlistName;
        ImageView overflowButton;
        Activity viewActivity;
        private ClickInterface listener;

        public PlaylistViewHolder(Activity con,View itemView,ClickInterface listener) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            playlistName = (TextView) itemView.findViewById(R.id.playlist_name);
            viewActivity= con;
            if(!isSelect) {
                overflowButton = (ImageView) itemView.findViewById(R.id.my_overflow);
                this.listener = listener;
                itemView.setOnLongClickListener(this);
            }
            itemView.setOnClickListener(this);

        }
        
        @Override
        public boolean onLongClick(View v) {
            if(!isSelect) {
                if (listener != null) {
                    return listener.onItemLongClicked(getAdapterPosition());
                }
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            if (!isSelect) {
                if (!MainActivity.isLongClickOn) {
                    Intent intent = new Intent(viewActivity, SongsUnderTest.class);
                    intent.putExtra("X", "PlayList");
                    intent.putExtra("id", playlists.get(getAdapterPosition()).getPlaylistId());
                    viewActivity.startActivity(intent);
                    return;
                }
                if (listener != null) {
                    listener.onItemClicked(getAdapterPosition());
                }
            }else{
                UtilFunctions.addMultipleToPlaylist(viewActivity.getContentResolver(), audioIdList, playlists.get(getAdapterPosition()).getPlaylistId());
                Toast.makeText(viewActivity, "Added to " + playlists.get(getAdapterPosition()).getPlaylistName(), Toast.LENGTH_SHORT).show();

                viewActivity.finish();
            }
        }

    }

    public PlaylistRecyclerAdapter(ClickInterface clickListener,List<PlaylistInfo> objs,Activity act,Boolean Select,ArrayList<String> idList){
        this.playlists = objs;
        this.mactivity = act;
        this.clickListener = clickListener;
        this.isSelect = Select;
        this.audioIdList = idList;
    }

    @Override
    public PlaylistRecyclerAdapter.PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if(!isSelect) {
             v = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_layout, parent, false);
        }else{
             v = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_selection_layout, parent, false);
        }

        PlaylistViewHolder gvh = new PlaylistViewHolder(mactivity,v,clickListener);
        return gvh;
    }

    @Override
    public void onBindViewHolder(PlaylistRecyclerAdapter.PlaylistViewHolder holder, final int position) {
        if(position%2 == 0) {
            holder.cv.setBackgroundColor(Color.parseColor("#484848"));

        }else{
            holder.cv.setBackgroundColor(Color.parseColor("#3D3D3D"));
        }
        holder.playlistName.setText(playlists.get(position).getPlaylistName());
        if(!isSelect) {
            holder.overflowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.fifthOverflowReaction(v, mactivity, playlists.get(position));
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }


}
