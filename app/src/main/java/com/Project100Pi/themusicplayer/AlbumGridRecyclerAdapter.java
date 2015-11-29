package com.Project100Pi.themusicplayer;

/**
 * Created by BalachandranAR on 11/14/2015.
 */
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
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AlbumGridRecyclerAdapter  extends SelectableAdapter<AlbumGridRecyclerAdapter.AlbumViewHolder> implements SectionIndexer{

    static List<AlbumInfo> albums;
    Activity mactivity;
    private ClickInterface clickListener;

    HashMap<String, Integer> alphaIndexer;
    HashMap<Integer, Integer> positionIndexer;
    String[] sections;
    ArrayList<String> listItems;


    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_grid_layout, parent, false);
        AlbumViewHolder avh = new AlbumViewHolder(mactivity, v, clickListener);
        return avh;
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, final int position) {
        holder.lower.setBackgroundColor(ColorUtils.secondaryBgColor);
        holder.albumName.setText(albums.get(position).getAlbumName());
        holder.albumName.setTextColor(ColorUtils.primaryTextColor);
        holder.artistName.setText(albums.get(position).getArtistName());
        holder.artistName.setTextColor(ColorUtils.secondaryTextColor);
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
              secondOverflowReaction(v, mactivity, albums.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.albums.size();
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
            return positionIndexer.get(albums.size()-2);
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


    // private ClickInterface clickListener;

    public static class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView albumName, artistName;
        ImageView overflowButton,albumArt;
        Activity viewActivity;
        CardView cv;
        LinearLayout lower;
        private ClickInterface listener;

        public AlbumViewHolder(Activity con, View itemView, ClickInterface listener) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cvGrid);
            albumName = (TextView) itemView.findViewById(R.id.album_name);
            artistName = (TextView) itemView.findViewById(R.id.album_artist);
            albumArt = (ImageView) itemView.findViewById(R.id.album_art);
            lower = (LinearLayout) itemView.findViewById(R.id.lower_layout);
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

        listItems = new ArrayList<String>();
        // here is the tricky stuff
        alphaIndexer = new HashMap<String, Integer>();
        // in this hashmap we will store here the positions for
        // the sections
        positionIndexer = new HashMap<Integer, Integer>();
        int size = albums.size();
        for (int i = size - 1; i >= 0; i--) {
            String element = albums.get(i).getAlbumName();
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
                                        Toast.makeText(act,"Album Deleted", Toast.LENGTH_SHORT).show();
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

