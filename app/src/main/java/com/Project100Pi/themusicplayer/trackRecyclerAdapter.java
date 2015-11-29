package com.Project100Pi.themusicplayer;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by BalachandranAR on 8/24/2015.
 */
public class trackRecyclerAdapter extends SelectableAdapter<trackRecyclerAdapter.TrackViewHolder> implements BubbleTextGetter,SectionIndexer{

    List<TrackObject> tracks;
   static ArrayList<String > idList;
   Activity mactivity;
    private ClickInterface clickListener;

    HashMap<String, Integer> alphaIndexer;
    HashMap<Integer, Integer> positionIndexer;
    String[] sections;
    ArrayList<String> listItems;

    @Override
    public String getTextToShowInBubble(int pos) {
        return Character.toString(tracks.get(pos).getTrackName().charAt(0));
    }

    @Override
    public Object[] getSections() {
        return sections;
    }

    @Override
    public int getPositionForSection(int section) {
        String letter = sections[section];
        return alphaIndexer.get(letter);
    }

    @Override
    public int getSectionForPosition(int position) {
        Integer returnSection = 0;
        try {
            returnSection = positionIndexer.get(position);
        }catch(Exception e ){}
        if(returnSection != null)
            return returnSection;
        else
            return positionIndexer.get(tracks.size()-2);
    }

    public static class TrackViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        CardView cv;
        TextView trackName;
        TextView trackArtist;
        TextView trackDuration;
        ImageView overflowButton;
       Activity viewActivity;
        private ClickInterface listener;

        public TrackViewHolder(Activity con,View itemView,ClickInterface listener) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            trackName = (TextView) itemView.findViewById(R.id.playList_name);
            trackArtist = (TextView) itemView.findViewById(R.id.track_artist);
            trackDuration = (TextView) itemView.findViewById(R.id.artist_noOfSongs);
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
                UtilFunctions.playSelectedSongs(viewActivity, idList, getAdapterPosition(), false);
                return;
            }
            if (listener != null) {
                listener.onItemClicked(getAdapterPosition());
            }
        }


    }

    public trackRecyclerAdapter(Activity act,List<TrackObject> trackList){
        this.tracks = trackList;
        this.mactivity = act;

    }

    public trackRecyclerAdapter(ClickInterface clickListener,List<TrackObject> trackList,ArrayList<String> idList,Activity act) {
        super();
        this.tracks = trackList;
        this.clickListener = clickListener;
        this.mactivity = act;
        this.idList = idList;

        listItems = new ArrayList<String>();
        // here is the tricky stuff
        alphaIndexer = new HashMap<String, Integer>();
        // in this hashmap we will store here the positions for
        // the sections
        positionIndexer = new HashMap<Integer, Integer>();
        int size = tracks.size();
        for (int i = size - 1; i >= 0; i--) {
            String element = tracks.get(i).getTrackName();
            listItems.add(element);
            alphaIndexer.put(element.substring(0, 1).toUpperCase(), i);
        }
        Set<String> keys = alphaIndexer.keySet(); // set of letters ...sets
        // cannot be sorted...

        Iterator<String> it = keys.iterator();
        ArrayList<String> keyList = new ArrayList<String>(); // list can be
        // sorted

        while (it.hasNext()) {
            String key = it.next();
            keyList.add(key);
        }

        Collections.sort(keyList,String.CASE_INSENSITIVE_ORDER);

        sections = new String[keyList.size()]; // simple conversion to an
        // array of object
        keyList.toArray(sections);

        List<Integer> values = new ArrayList();
        for (int i : alphaIndexer.values()) {
            values.add(i);
        }
        values.add(listItems.size()-1);
        Collections.sort(values);

        int k = 0;
        int z = 0;
        for(int i = 0; i < values.size()-1; i++) {
            int temp = values.get(i+1);
            do {
                positionIndexer.put(k, z);
                k++;
            } while(k < temp);
            z++;
        }
    }
    @Override
    public int getItemCount() {
        return tracks.size();
    }

    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.track_layout_test, viewGroup, false);


        TrackViewHolder pvh = new TrackViewHolder(mactivity,v,clickListener);
        return pvh;
    }

    @Override
    public void onBindViewHolder(TrackViewHolder trackViewHolder, final int i) {
        if(i%2 != 0) {
            trackViewHolder.cv.setBackgroundColor(ColorUtils.primaryBgColor);

        }else{
            trackViewHolder.cv.setBackgroundColor(ColorUtils.secondaryBgColor);
        }
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
        overflowReaction(v,mactivity,tracks.get(i));


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