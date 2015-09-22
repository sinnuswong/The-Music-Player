package com.Project100Pi.themusicplayer;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by BalachandranAR on 8/24/2015.
 */
public class FirstFragmentTest extends Fragment implements ClickInterface{

    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;
    trackRecyclerAdapter tra;
    ArrayList<TrackObject> tracks;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v =inflater.inflate(R.layout.first_frag_test, container, false);
        final RecyclerView firstFragRecycler = (RecyclerView)v.findViewById(R.id.firstFragRecycler);
        firstFragRecycler.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity().getApplicationContext());
        firstFragRecycler.setLayoutManager(llm);

        String title="";
        String path="";
        String trackAlbum = "";
        String trackArtist = "";
        String trackDuration  = "";
        Long id=null;
        MainActivity.idList = new ArrayList<String>();
        tracks = new ArrayList<TrackObject>();
        Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Audio.Media.TITLE + " ASC");
        int i = 0;

        while(cursor.moveToNext()){
            try{
                title= cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                id=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                trackAlbum = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                trackArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                trackDuration = UtilFunctions.convertSecondsToHMmSs(Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));
                TrackObject currTrack =  new TrackObject(i,id.toString(),title,trackArtist,trackDuration,path);
                tracks.add(currTrack);
                MainActivity.idList.add(id.toString());
                MainActivity.idToTrackObj.put(id,currTrack);
                i++;
            }catch(Exception e){
                continue;
            }
        }


        // tra = new trackRecyclerAdapter(getActivity(),tracks);
        tra = new trackRecyclerAdapter(this,tracks,MainActivity.idList,getActivity());
        firstFragRecycler.setAdapter(tra);
        firstFragRecycler.setItemAnimator(new DefaultItemAnimator());
        FastScroller fastScroller=(FastScroller)v.findViewById(R.id.firstfastscroller);
        fastScroller.setRecyclerView(firstFragRecycler);

        return v;
    }

    public static FirstFragmentTest newInstance(String text) {

        FirstFragmentTest f = new FirstFragmentTest();
        return f;
    }

    @Override
    public void onItemClicked(int position) {
        if (actionMode != null) {
            toggleSelection(position);
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        if (actionMode == null) {
           actionMode= ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallback);
        }

        toggleSelection(position);

        return true;
    }

    private void toggleSelection(int position) {
        tra.toggleSelection(position);
        int count = tra.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count)+" item(s) selected");
            actionMode.invalidate();
        }
    }


    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MainActivity.isLongClickOn =  true;
           MainActivity.mToolbar.getLayoutParams().height = 0;
            mode.getMenuInflater().inflate (R.menu.multi_choice_options, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            final ArrayList<String> selIdList=getidList();
            final ArrayList<String> selsongNameList=getsongNameList();
            final int size=selIdList.size();
            switch (item.getItemId()) {

                case R.id.itemShuffle:
                    UtilFunctions.playSelectedSongs(getActivity(), selIdList, 0, true);
                    break;
                case R.id.itemPlay:
        		/*
        		 Intent intent=new Intent(getActivity(),PlayActivity.class);
				 	intent.putExtra("do", "Play");
		           intent.putExtra("position", 0);
		            intent.putStringArrayListExtra("playingList", selsongNameList);
		            startActivity(intent);
		            */
                    UtilFunctions.playSelectedSongs(getActivity(),selIdList,0,false);
                    break;

                case R.id.itemPlayNext:
                    UtilFunctions.playSongsNext(getActivity(), selIdList);


                    break;
                case R.id.itemAddQueue:

                    UtilFunctions.addToQueueSongs(getActivity(), selIdList);
                    break;

                case R.id.itemShare:
                    Intent sharingIntent=new Intent(Intent.ACTION_SEND_MULTIPLE);
                    sharingIntent.setType("audio/*");
                    //   String mimetypes[]={"audio/*"};




                    //  cursor=makeAlbumSongCursor(selectedAlbumId);
                    ArrayList<Uri> uris=new ArrayList<Uri>();
                    //  while(cursor.moveToNext()){
                    for(int i=0;i<size;i++)
                    {
                        String id=selIdList.get(i);
                        String path=FirstFragment.idToPath.get(Long.parseLong(id));
                        path="file://"+path;

                        // Toast.makeText(getContext(),"Hi there", Toast.LENGTH_LONG).show();
                        Uri uri=Uri.parse(path);
                        uris.add(uri);
                    }
                    // Uri uri=Uri.parse(pathList.get(currPosition));

                    sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    //	 sharingIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                    // sharingIntent.putExtra(Intent.EXTRA_TEXT, songName);
                    Intent chooser=new Intent(Intent.ACTION_CHOOSER);
                    chooser.putExtra(Intent.EXTRA_INTENT, sharingIntent);
                    chooser.putExtra(Intent.EXTRA_TITLE, "title");
                    Intent addIntent=new Intent();
                    // addIntent.setComponent(new ComponentName("com.android.bluetooth","com.android.bluetooth.opp.BluetoothOppLauncherActivity"));
                    addIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    addIntent.setPackage("com.android.bluetooth");
                    addIntent.setType("*/*");
                    addIntent.setAction(Intent.ACTION_SEND_MULTIPLE);

                    Intent intentarray[]={addIntent};
                    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentarray);
                    //Toast.makeText(getContext(),"choice is "+choice, Toast.LENGTH_SHORT).show();
                    // sharingIntent.

                    startActivity(chooser);
                    break;

                case R.id.itemDelete:
            /*	AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        		builder.setTitle("Confirm Delete");
        		builder.setMessage("Are you sure you want to delete the selected songs?");
        		builder.setCancelable(true);
        		builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    	   ContentResolver resolver=getActivity().getApplicationContext().getContentResolver();
                    	   for(int i=0;i<size;i++)
     	        		  {

                    	   String selectedId=selIdList.get(i);
                    	   String songName=idToName.get(Long.parseLong(selectedId));
                    	   int rowsDeleted	=resolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media._ID + " LIKE \"" + selectedId + "\"", null);
                    	   File file = new File(idToPath.get(Long.parseLong(selectedId)));
                    	   boolean deleted = file.delete();
     	        		  }
                    	   Toast.makeText(getActivity(), size+ " Songs deleted ",Toast.LENGTH_SHORT).show();
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
            	*/

                    UtilFunctions.deletePopUp(getActivity(), getActivity(), selIdList, "Are you sure you want to delete the selected Songs?","Songs deleted");
                    break;
                case R.id.itemToPlaylist :

                    Intent intent=new Intent(getActivity(),PlayListSelectionTest.class);
                    // intent.putExtra("songName",songName);

                    intent.putExtra("selectedIdList",selIdList);
                    startActivity(intent);
                    break;



            }

            return false;
            }



        private ArrayList<String> getidList(){
            List<Integer> selItems = tra.getSelectedItems();
            ArrayList<String> selIdList = new ArrayList<String>();
            int size = selItems.size();
            int i;
            String title;
            for(i=0;i<size;i++){
                //title =   selCards.get(i).getTitle();
                String id=tracks.get(selItems.get(i)).getTrackId();
                selIdList.add(id);

            }
            Collections.reverse(selIdList); // PROBLEM MAY ARISE
            return selIdList;

        }

        private ArrayList<String> getsongNameList(){
            List<Integer> selItems = tra.getSelectedItems();
            ArrayList<String> selNameList = new ArrayList<String>();
            int size = selItems.size();
            int i;
            String title;
            for(i=0;i<size;i++){
                //title =   selCards.get(i).getTitle();
                String id=tracks.get(selItems.get(i)).getTrackName();
                selNameList.add(id);

            }
            Collections.reverse(selNameList); // PROBLEM MAY ARISE
            return selNameList;

        }
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            tra.clearSelection();
            MainActivity.isLongClickOn = false;
            actionMode = null;
            MainActivity.mToolbar.getLayoutParams().height = MainActivity.mActionBarSize;
        }
    }


}
