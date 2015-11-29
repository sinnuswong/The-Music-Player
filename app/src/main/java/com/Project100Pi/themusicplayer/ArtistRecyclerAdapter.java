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
 * Created by BalachandranAR on 8/31/2015.
 */
public class ArtistRecyclerAdapter extends SelectableAdapter<ArtistRecyclerAdapter.ArtistViewHolder>implements BubbleTextGetter,SectionIndexer{
static List<ArtistInfo> artists;
    Activity mactivity;
    private ClickInterface clickListener;

    HashMap<String, Integer> alphaIndexer;
    HashMap<Integer, Integer> positionIndexer;
    String[] sections;
    ArrayList<String> listItems;

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
            return positionIndexer.get(artists.size()-2);
    }

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
            artistName = (TextView) itemView.findViewById(R.id.playList_name);
            noOfAlbums = (TextView) itemView.findViewById(R.id.artist_no_album);
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
                intent.putExtra("title",artists.get(getAdapterPosition()).getArtistName());
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

        listItems = new ArrayList<String>();
        // here is the tricky stuff
        alphaIndexer = new HashMap<String, Integer>();
        // in this hashmap we will store here the positions for
        // the sections
        positionIndexer = new HashMap<Integer, Integer>();
        int size = artists.size();
        for (int i = size - 1; i >= 0; i--) {
            String element = artists.get(i).getArtistName();
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

        Collections.sort(keyList, String.CASE_INSENSITIVE_ORDER);

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
    public ArtistRecyclerAdapter.ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_layout_test, parent, false);


        ArtistViewHolder avh = new ArtistViewHolder(mactivity,v,clickListener);
        return avh;
    }

    @Override
    public void onBindViewHolder(ArtistRecyclerAdapter.ArtistViewHolder holder, final int position) {
        if(position%2 != 0) {
            holder.cv.setBackgroundColor(ColorUtils.primaryBgColor);

        }else{
            holder.cv.setBackgroundColor(ColorUtils.secondaryBgColor);
        }
        holder.artistName.setText(artists.get(position).getArtistName());
        holder.artistName.setTextColor(ColorUtils.primaryTextColor);
        holder.noOfAlbums.setText(artists.get(position).getNoOfAlbums() + " Albums");
        holder.noOfAlbums.setTextColor(ColorUtils.secondaryTextColor);
        holder.noOfTracks.setText(artists.get(position).getNoOfSongs() + " Tracks");
        holder.noOfTracks.setTextColor(ColorUtils.secondaryTextColor);
        holder.overflowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thirdOverflowReaction(v,mactivity,artists.get(position));
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
    void thirdOverflowReaction(View v, final Activity act,ArtistInfo selArtist){

        PopupMenu popupMenu = new PopupMenu(act,v);
        popupMenu.inflate(R.menu.long_click_actions);
        final int currPosition = selArtist.getsNo();


        // final String songName=titleList.get(currPosition);
        final Long selectedArtistId=selArtist.getArtistId();
        final String selectedAlbumName = selArtist.getArtistName();
        //  Toast.makeText(getActivity(), songName +"and position is" + currPosition, Toast.LENGTH_LONG).show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                switch(item.getItemId()){
                    case R.id.cnt_menu_play:

                        UtilFunctions.playSelectedSongsfromChoice(act, selectedArtistId, "artist",false);

                        break;
                    case R.id.cnt_menu_play_next:

                        UtilFunctions.playSongsNextfromChoice(act, selectedArtistId, "artist");

                        break;

                    case R.id.cnt_menu_add_queue:
                        UtilFunctions.addToQueuefromChoice(act,selectedArtistId, "artist");
                        break;
                    case R.id.addToPlaylist:
                        act.startActivity(UtilFunctions.addSongstoPlaylist(act, selectedArtistId, "artist"));


                        break;
                    case R.id.cnt_mnu_edit:

                        break;
                    case R.id.cnt_mnu_delete:

                        AlertDialog.Builder builder=new AlertDialog.Builder(act);
                        builder.setTitle("Confirm Delete");
                        builder.setMessage("Are you sure you want to delete the selected Artist?");
                        builder.setCancelable(true);
                        builder.setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        ContentResolver resolver=act.getApplicationContext().getContentResolver();
                                        Cursor cursor=CursorClass.makeCursorBasedOnChoice(act, selectedArtistId, "artist");
                                        while(cursor.moveToNext()){
                                            Long songId=cursor.getLong(0);


                                            File file = new File(MainActivity.idToTrackObj.get(songId).getTrackPath());
                                            boolean deleted = file.delete();
                                            if (deleted)
                                                resolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media._ID + " LIKE \"" + songId + "\"", null);

                                        }
                                        Toast.makeText(act, "Artist Deleted", Toast.LENGTH_SHORT).show();
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


                        act.startActivity(UtilFunctions.shareSingle(act, selectedArtistId, "artist"));


                        break;

                }

                return true;

            }
        });
        popupMenu.show();
    }
    public void removeAt(int position) {
        artists.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, artists.size());
    }
}
