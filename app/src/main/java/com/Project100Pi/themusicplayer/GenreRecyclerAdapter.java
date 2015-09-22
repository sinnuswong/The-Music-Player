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
public class GenreRecyclerAdapter extends SelectableAdapter<GenreRecyclerAdapter.GenreViewHolder>implements BubbleTextGetter{
    static List<GenreInfo> genres;
    Activity mactivity;
    private ClickInterface clickListener;

    public static class GenreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        CardView cv;
        TextView genreName;
        ImageView overflowButton;
        Activity viewActivity;
        private ClickInterface listener;
        public GenreViewHolder(Activity con,View itemView,ClickInterface listener) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            genreName = (TextView) itemView.findViewById(R.id.playlist_name);
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
                intent.putExtra("X","Genre");
                intent.putExtra("id",genres.get(getAdapterPosition()).getGenreId());
                viewActivity.startActivity(intent);
                return;
            }
            if (listener != null) {
                listener.onItemClicked(getAdapterPosition());
            }
        }

    }

    public GenreRecyclerAdapter(ClickInterface clickListener,List<GenreInfo> objs,Activity act){
        this.genres = objs;
        this.mactivity = act;
        this.clickListener = clickListener;
    }

    @Override
    public GenreRecyclerAdapter.GenreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.genre_layout_test, parent, false);


        GenreViewHolder gvh = new GenreViewHolder(mactivity,v,clickListener);
        return gvh;
    }

    @Override
    public void onBindViewHolder(GenreRecyclerAdapter.GenreViewHolder holder, final int position) {
        if(position%2 == 0) {
            holder.cv.setBackgroundColor(Color.parseColor("#3D3D3D"));

        }else{
            holder.cv.setBackgroundColor(Color.parseColor("#484848"));
        }
        holder.genreName.setText(genres.get(position).getGenreName());
        holder.overflowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               MainActivity.fourthOverflowReaction(v,mactivity,genres.get(position));
            }
        });


    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        return Character.toString(genres.get(pos).getGenreName().charAt(0));
    }
}
