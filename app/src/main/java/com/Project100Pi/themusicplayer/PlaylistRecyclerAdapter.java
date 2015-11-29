package com.Project100Pi.themusicplayer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
            playlistName = (TextView) itemView.findViewById(R.id.playList_name);
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
                    intent.putExtra("title",playlists.get(getAdapterPosition()).getPlaylistName());
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
            holder.cv.setBackgroundColor(ColorUtils.secondaryBgColor);

        }else{
            holder.cv.setBackgroundColor(ColorUtils.primaryBgColor);
        }
        holder.playlistName.setText(playlists.get(position).getPlaylistName());
        holder.playlistName.setTextColor(ColorUtils.primaryTextColor);
        if(!isSelect) {
            holder.overflowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   fifthOverflowReaction(v, mactivity, playlists.get(position));
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    void fifthOverflowReaction(View v, final Activity act, final PlaylistInfo selPlaylist){

        PopupMenu popupMenu = new PopupMenu(act,v);
        popupMenu.inflate(R.menu.menu_playlist_popup);
        final int currPosition = selPlaylist.getsNo();


        // final String songName=titleList.get(currPosition);
        final Long selectedId=selPlaylist.getPlaylistId();
        //  Toast.makeText(getActivity(), songName +"and position is" + currPosition, Toast.LENGTH_LONG).show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                switch (item.getItemId()) {

                    case R.id.cnt_menu_play:

                        UtilFunctions.playSelectedSongsfromChoice(act, selectedId, "playlist", false);

                        break;
                    case R.id.cnt_menu_play_next:

                        UtilFunctions.playSongsNextfromChoice(act, selectedId, "playlist");

                        break;

                    case R.id.cnt_menu_add_queue:
                        UtilFunctions.addToQueuefromChoice(act, selectedId, "playlist");
                        break;
                    case R.id.cnt_mnu_edit:


                        LayoutInflater layoutInflater = LayoutInflater.from(act);
                        View promptView = layoutInflater.inflate(R.layout.dialog_box, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(act);
                        alertDialogBuilder.setView(promptView);
                        //	Toast.makeText(activity,"Id is "+id,Toast.LENGTH_SHORT).show();
                        TextView textView = (TextView) promptView.findViewById(R.id.textView);
                        textView.setText("Edit PlayList Name");
                        final EditText editTitleText = (EditText) promptView.findViewById(R.id.edittext);
                        editTitleText.setText(selPlaylist.getPlaylistName());

                        //final EditText editAlbumText=(EditText) promptView.findViewById(R.id.edittext);
                        // setup a dialog window
                        alertDialogBuilder.setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        ContentResolver thisContentResolver = act.getContentResolver();
                                        UtilFunctions.renamePlaylist(thisContentResolver, selectedId, editTitleText.getText().toString());
                                        Toast.makeText(act, "PlayList Renamed", Toast.LENGTH_LONG).show();

                                        // populateCards();
                                    }
                                })
                                .setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                        // create an alert dialog
                        AlertDialog alert = alertDialogBuilder.create();
                        alert.show();


                        //Toast.makeText(this, "Edit :" , Toast.LENGTH_SHORT).show();

                        break;
                    case R.id.cnt_mnu_delete:

                        AlertDialog.Builder builder = new AlertDialog.Builder(act);
                        builder.setTitle("Confirm Delete");
                        builder.setMessage("Are you sure you want to delete the Playlist?");
                        builder.setCancelable(true);
                        builder.setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int thisid) {
                                        ContentResolver resolver = act.getApplicationContext().getContentResolver();
                                        UtilFunctions.deletePlaylist(resolver, selectedId);
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

                        act.startActivity(UtilFunctions.shareSingle(act, selectedId, "playlist"));
                        break;

                }

                return true;

            }
        });
        popupMenu.show();
    }

    public void removeAt(int position) {
        playlists.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, playlists.size());
    }
}
