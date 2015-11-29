package com.Project100Pi.themusicplayer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
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
           holder.cv.setBackgroundColor(ColorUtils.primaryBgColor);

        }else{
            holder.cv.setBackgroundColor(ColorUtils.secondaryBgColor);
        }

        holder.albumName.setText(albums.get(position).getAlbumName());
        holder.albumName.setTextColor(ColorUtils.primaryTextColor);
        holder.artistName.setText(albums.get(position).getArtistName());
        holder.artistName.setTextColor(ColorUtils.secondaryTextColor);
        holder.noOfSongs.setText(albums.get(position).getNoOfSongs() + " tracks");
        holder.noOfSongs.setTextColor(ColorUtils.secondaryTextColor);
        holder.overflowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               secondOverflowReaction(v,mactivity,albums.get(position));
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

    public AlbumRecyclerAdapter(ClickInterface clickListener,List<AlbumInfo> albumList,Activity act){
        this.clickListener = clickListener;
        this.albums = albumList;
        this.mactivity = act;
    }
    void secondOverflowReaction(View v, final Activity act,AlbumInfo selAlbum){

        PopupMenu popupMenu = new PopupMenu(act,v);
        popupMenu.inflate(R.menu.long_click_actions);
        final int currPosition = selAlbum.getsNo();


        // final String songName=titleList.get(currPosition);
        final Long selectedAlbumId=selAlbum.getAlbumId();
        final String selectedAlbumName = selAlbum.getAlbumName();
        //  Toast.makeText(getActivity(), songName +"and position is" + currPosition, Toast.LENGTH_LONG).show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                switch(item.getItemId()){
                    case R.id.cnt_menu_play:

                        UtilFunctions.playSelectedSongsfromChoice(act, selectedAlbumId, "album",false);

                        break;

                    case R.id.cnt_menu_play_next:

                        UtilFunctions.playSongsNextfromChoice(act, selectedAlbumId, "album");

                        break;

                    case R.id.cnt_menu_add_queue:
                        UtilFunctions.addToQueuefromChoice(act,selectedAlbumId, "album");
                        break;
                    case R.id.addToPlaylist:

                        act.startActivity(UtilFunctions.addSongstoPlaylist(act,selectedAlbumId,"album"));

                        break;
                    case R.id.cnt_mnu_edit:

                        //editAlbumInfo(selectedAlbumName, selectedAlbumId); // NOT WORKING

                        break;
                    case R.id.cnt_mnu_delete:

                        AlertDialog.Builder builder=new AlertDialog.Builder(act);
                        builder.setTitle("Confirm Delete");
                        builder.setMessage("Are you sure you want to delete the selected Album?");
                        builder.setCancelable(true);
                        builder.setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        ContentResolver resolver=act.getApplicationContext().getContentResolver();
                                        Cursor cursor=CursorClass.makeCursorBasedOnChoice(act, selectedAlbumId, "album");
                                        while(cursor.moveToNext()){
                                            Long songId=cursor.getLong(0);


                                            File file = new File(MainActivity.idToTrackObj.get(songId).getTrackPath());
                                            boolean deleted = file.delete();
                                            if (deleted)
                                                resolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media._ID + " LIKE \"" + songId + "\"", null);

                                        }
                                        Toast.makeText(act, "Album Deleted", Toast.LENGTH_SHORT).show();
                                        removeAt(currPosition);
                                        dialog.cancel();
                                    }
                                });
                        builder.setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder.create();
                        alert11.show();


                        break;
                    case R.id.cnt_mnu_share:


                        act.startActivity(UtilFunctions.shareSingle(act, selectedAlbumId, "album"));

                        break;

                }

                return true;

            }
        });
        popupMenu.show();
    }
    public void removeAt(int position) {
        albums.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, albums.size());
    }
}
