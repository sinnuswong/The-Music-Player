package com.Project100Pi.themusicplayer;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by BalachandranAR on 8/24/2015.
 */
public class NowPlayingRecyclerAdapter extends SelectableAdapter<NowPlayingRecyclerAdapter.NowPlayingViewHolder> implements ItemTouchHelperAdapter {

    List<TrackObject> tracks;
    static ArrayList<String > idList;
    Activity mactivity;
    private OnDragStartListener mDragStartListener;
    Long playListid;
    static NowPlayingViewHolder nowPlayingHolder;

    @Override
    public void onItemMove(int fromPosition, int toPosition) {

            TrackObject prev = tracks.remove(fromPosition);
            String prevStr = songInfoObj.nowPlayingList.remove(fromPosition);
            tracks.add(toPosition, prev);
            songInfoObj.nowPlayingList.add(toPosition, prevStr);
            songInfoObj.currPlayPos = songInfoObj.nowPlayingList.indexOf(songInfoObj.songId.toString());
        if(mactivity instanceof SongsUnderTest){
            idList.remove(fromPosition);
            idList.add(toPosition,prevStr);
            ContentResolver resolver=mactivity.getContentResolver();
            boolean result= MediaStore.Audio.Playlists.Members.moveItem(resolver, playListid,fromPosition,toPosition);

        }
        notifyItemMoved(fromPosition, toPosition);

    }

    @Override
    public void onItemDismiss(int position) {
        if (mactivity instanceof NowPlayingListTest) {
            songInfoObj.nowPlayingList.remove(tracks.get(position).getTrackId().toString());
            songInfoObj.initialPlayingList.remove(tracks.get(position).getTrackId().toString());
            tracks.remove(position);
            if (songInfoObj.currPlayPos == position) {
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
            }else{
                songInfoObj.currPlayPos = songInfoObj.nowPlayingList.indexOf(songInfoObj.songId.toString());
            }
            notifyItemRemoved(position);
           notifyDataSetChanged();
        }
    }

    public static class NowPlayingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,ItemTouchHelperViewHolder{
        CardView cv;
        TextView trackName;
        TextView trackArtist;
        TextView trackDuration;
        ImageView overflowButton,dragHandle;
        Activity viewActivity;
        GifImageView animatedBars;
        GifDrawable bars;
        RelativeLayout outerLayout;

        public NowPlayingViewHolder(Activity con,View itemView) throws IOException {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            trackName = (TextView) itemView.findViewById(R.id.playList_name);
            trackArtist = (TextView) itemView.findViewById(R.id.track_artist);
            trackDuration = (TextView) itemView.findViewById(R.id.artist_noOfSongs);
            viewActivity= con;
            overflowButton=(ImageView)itemView.findViewById(R.id.my_overflow);
            dragHandle=(ImageView)itemView.findViewById(R.id.drag_handle);
            animatedBars = (GifImageView)itemView.findViewById(R.id.animated_bars);
            bars = new GifDrawable( viewActivity.getResources(), R.drawable.soundbars_blue);
            outerLayout = (RelativeLayout)itemView.findViewById(R.id.outerLayout);
            itemView.setOnClickListener(this);

        }



        @Override
        public void onClick(View v) {
               // UtilFunctions.playSelectedSongs(viewActivity, idList, getAdapterPosition(), false);
            if(viewActivity instanceof NowPlayingListTest) {
                //Play song staying in NowPlayingListTest activity.
                try {
                    PlayHelperFunctions.isSongPlaying = true;
                    PlayHelperFunctions.audioPlayer((String) PlayHelperFunctions.setPlaySongInfo(Long.parseLong(songInfoObj.nowPlayingList.get(getAdapterPosition()))), 1);
                    songInfoObj.currPlayPos = getAdapterPosition();
                    nowPlayingHolder.animatedBars.setVisibility(View.GONE);
                  this.animatedBars.setImageDrawable(this.bars);
                    this.animatedBars.setVisibility(View.VISIBLE);
                   this.animatedBars.setAlpha(0.25f);
                    nowPlayingHolder = this;
                } catch (Exception e) {

                }

                }else if(viewActivity instanceof SongsUnderTest){
                UtilFunctions.playSelectedSongs(viewActivity, idList, getAdapterPosition(), false);
                return;
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

    public NowPlayingRecyclerAdapter(Activity act,List<TrackObject> trackList,OnDragStartListener dragStartListener){
        this.tracks = trackList;
        this.mactivity = act;
        this.mDragStartListener = dragStartListener;

    }

    public interface OnDragStartListener {
        void onDragStarted(RecyclerView.ViewHolder viewHolder);
    }

    public NowPlayingRecyclerAdapter(List<TrackObject> trackList,ArrayList<String> idList,Activity act,OnDragStartListener dragStartListener){
        super();
        this.tracks = trackList;
        this.mactivity = act;
        this.idList = idList;
        this.mDragStartListener = dragStartListener;
    }
    public NowPlayingRecyclerAdapter(List<TrackObject> trackList,ArrayList<String> idList,Activity act,OnDragStartListener dragStartListener,Long id){
        super();
        this.tracks = trackList;
        this.mactivity = act;
        this.idList = idList;
        this.mDragStartListener = dragStartListener;
        this.playListid = id;
    }
    @Override
    public int getItemCount() {
        return tracks.size();
    }

    @Override
    public NowPlayingViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.now_playing_list_inner, viewGroup, false);


        NowPlayingViewHolder pvh = null;
        try {
            pvh = new NowPlayingViewHolder(mactivity,v);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pvh;
    }

    @Override
    public void onBindViewHolder(final NowPlayingViewHolder trackViewHolder, final int i) {
        final Drawable dragIcon = mactivity.getResources().getDrawable(R.drawable.grab_material);
        dragIcon.setColorFilter(ColorUtils.secondaryTextColor, PorterDuff.Mode.SRC_ATOP);
        trackViewHolder.dragHandle.setImageDrawable(dragIcon);
        trackViewHolder.dragHandle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onDragStarted(trackViewHolder);
                }
                return false;
            }
        });

        if(i == songInfoObj.currPlayPos && mactivity instanceof NowPlayingListTest){
            trackViewHolder.animatedBars.setImageDrawable(trackViewHolder.bars);
            trackViewHolder.animatedBars.setVisibility(View.VISIBLE);
            trackViewHolder.animatedBars.setAlpha(0.25f);
            nowPlayingHolder = trackViewHolder;
        }else {
            trackViewHolder.animatedBars.setVisibility(View.GONE);
        }

        trackViewHolder.outerLayout.setBackgroundColor(ColorUtils.secondaryBgColor);
        trackViewHolder.trackName.setText(tracks.get(i).getTrackName());
        trackViewHolder.trackName.setTextColor(ColorUtils.primaryTextColor);
        trackViewHolder.trackArtist.setText(tracks.get(i).getTrackArtist());
        trackViewHolder.trackArtist.setTextColor(ColorUtils.secondaryTextColor);
        trackViewHolder.trackDuration.setText(tracks.get(i).getTrackDuration());
        trackViewHolder.trackDuration.setTextColor(ColorUtils.secondaryTextColor);
        trackViewHolder.overflowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /// button click eventP
                overflowReaction(v, mactivity, tracks.get(i));


            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    void overflowReaction(View v, final Activity act, final TrackObject selTrack){
        PopupMenu popupMenu = new PopupMenu(act,v);
        popupMenu.inflate(R.menu.long_click_actions);
        final String songName=selTrack.getTrackName();
        final Long selectedId=Long.parseLong(selTrack.getTrackId());
        final int currPosition = selTrack.getsNo();
        final ArrayList<String> selectedIdList=new ArrayList<String>();
        selectedIdList.add(selectedId.toString()); // created to send the ArrayList to UtilFunctions.deletePopUp
        final ArrayList<String> selSongNameList = new ArrayList<String>();
        selSongNameList.add(songName);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.cnt_menu_play:

                        UtilFunctions.playSelectedSongs(act, idList, currPosition, false);

                        break;
                    case R.id.cnt_menu_play_next:

                        UtilFunctions.playSongsNext(act, selectedIdList);

                        break;

                    case R.id.cnt_menu_add_queue:
                        UtilFunctions.addToQueueSongs(act, selectedIdList);
                        break;
                    case R.id.addToPlaylist:

                        Intent intent = new Intent(act, PlayListSelectionTest.class);
                        intent.putExtra("songName", songName);
                        intent.putExtra("selectedIdList", selectedIdList);
                        act.startActivity(intent);


                        break;
                    case R.id.cnt_mnu_edit:

                        UtilFunctions.changeSongInfo(act, selectedId);


                        //Toast.makeText(this, "Edit :" , Toast.LENGTH_SHORT).show();

                        break;
                    case R.id.cnt_mnu_delete:

                        AlertDialog.Builder builder=new AlertDialog.Builder(act);
                        builder.setTitle("Confirm Delete");
                        builder.setMessage("Are you sure you want to delete the selected song?");
                        builder.setCancelable(true);
                        builder.setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        ContentResolver resolver = act.getApplicationContext().getContentResolver();
                                        int size = selectedIdList.size();
                                        for (int i = 0; i < size; i++) {
                                            String selectedId = selectedIdList.get(i);
                                            //String songName = FirstFragment.idToName.get(Long.parseLong(selectedId));
                                            File file = new File(MainActivity.idToTrackObj.get(Long.parseLong(selectedId)).getTrackPath());
                                            boolean deleted = file.delete();
                                            if(deleted)
                                                resolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media._ID + " LIKE \"" + selectedId + "\"", null);

                                        }
                                        Toast.makeText(act,"Song deleted", Toast.LENGTH_SHORT).show();
                                        if(selectedId == songInfoObj.songId){
                                            if (mactivity instanceof NowPlayingListTest) {
                                                songInfoObj.nowPlayingList.remove(tracks.get(currPosition).getTrackId().toString());
                                                songInfoObj.initialPlayingList.remove(tracks.get(currPosition).getTrackId().toString());
                                                tracks.remove(currPosition);
                                                if (songInfoObj.currPlayPos == currPosition) {
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
                                                }else{
                                                    songInfoObj.currPlayPos = songInfoObj.nowPlayingList.indexOf(songInfoObj.songId.toString());
                                                }
                                                notifyItemRemoved(currPosition);
                                                notifyDataSetChanged();
                                            }
                                        }

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
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        String path = selTrack.getTrackPath();
                        path = "file://" + path;
                        Toast.makeText(act, "Path is " + path, Toast.LENGTH_SHORT).show();
                        Uri uri = Uri.parse(path);
                        sharingIntent.setType("audio/*");
                        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        sharingIntent.putExtra(Intent.EXTRA_TITLE, songName);
                        act.startActivity(Intent.createChooser(sharingIntent, "Share Using"));

                        break;

                }

                return true;

            }
        });
        popupMenu.show();

    }
    public void removeAt(int position) {
        tracks.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, tracks.size());
    }


}