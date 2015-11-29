package com.Project100Pi.themusicplayer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardListView;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

/**
 * Created by BalachandranAR on 8/30/2015.
 */
public class SixthFragmentTest extends Fragment implements  ClickInterface{

    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;
    ArrayList<FolderInfo> folders ;
    FolderRecyclerAdapter gra;
    RecyclerView sixthFragRecycler;
    View v;
    HashSet<String> thisList = null;
    ArrayList<String> allFilePaths = new ArrayList<String>();
    ArrayList<String> songList;
     int pathSize = 0;
     int currLevel = 1;
     String currentPath = "/";
    RelativeLayout outerWindow;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        MainActivity.pathToIdInfo =   new HashMap<String,String>();
        v =inflater.inflate(R.layout.sixh_fragment_test, container, false);
        outerWindow = (RelativeLayout)v.findViewById(R.id.sixthFragOuter);
        outerWindow.setBackgroundColor(ColorUtils.primaryBgColor);
        sixthFragRecycler = (RecyclerView)v.findViewById(R.id.sixthFragRecycler);
        sixthFragRecycler.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity().getApplicationContext());
        sixthFragRecycler.setLayoutManager(llm);

        folders = new ArrayList<FolderInfo>();
        Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Audio.Media.TITLE + " ASC");
        while(cursor.moveToNext()){
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String thisId = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            allFilePaths.add(path);
            //	 pathToNameInfo.put(path, title);
            MainActivity.pathToIdInfo.put(path, thisId);

    }


        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){

                    // handle back button
                    currLevel--;
                    if(currLevel<=0){
                        getActivity().finish();
                        return true;
                    }
                    getActivity().getSupportFragmentManager().popBackStack();

                    List<String> folders=Arrays.asList(currentPath.split("/"));
                    ArrayList<String> currFolder = new ArrayList<String>();
                    currFolder.addAll(folders);
                    currFolder.remove(0);
                    currFolder.remove(currFolder.size()-1);
                    currentPath = "/";
                    for (String s : currFolder)
                    {
                        currentPath = currentPath+s+"/";
                    }
                    populateRecyclerView();

                    return true;

                }

                return false;
            }
        });

        populateRecyclerView();



        gra = new FolderRecyclerAdapter(this,folders,getActivity());
        sixthFragRecycler.setAdapter(gra);
        sixthFragRecycler.setItemAnimator(new DefaultItemAnimator());
        final VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) v.findViewById(R.id.sixth_frag_fast_scroller);
        // Connect the recycler to the scroller (to let the scroller scroll the list)
        fastScroller.setRecyclerView(sixthFragRecycler);
        sixthFragRecycler.setOnScrollListener(fastScroller.getOnScrollListener());
        fastScroller.setHandleColor(ColorUtils.accentColor);

        return v;
    }

    public static SixthFragmentTest newInstance(String text) {

        SixthFragmentTest f = new SixthFragmentTest();
        return f;
    }

    public void populateRecyclerView(){
        folders.clear();
        pathSize = allFilePaths.size();
        int i;
        thisList = new HashSet<>();
        songList = new ArrayList<String>();
        for( i=0;i<pathSize;i++){
            String thisPath = allFilePaths.get(i);
            if(thisPath.indexOf(currentPath) == 0){
                List<String> folders= Arrays.asList(thisPath.split("/"));
                thisList.add(folders.get(currLevel));

            }

        }
        int num = 0;
        for(String s : thisList){
            FolderInfo thisFolder = new FolderInfo(num,s,currentPath);
            folders.add(thisFolder);
            num++;
            if(UtilFunctions.checkExtension(s)){

                songList.add(MainActivity.pathToIdInfo.get(currentPath+s));
            }
        }

        if(gra != null){
            gra.notifyDataSetChanged();
        }
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
        gra.toggleSelection(position);
        int count = gra.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count)+" item(s) selected");
            actionMode.invalidate();
        }
    }

    public HashSet<String> getAllSongs(String path){
        HashSet<String > thisList = new HashSet<>();
        final ArrayList<String> songList = new ArrayList<String>();
        for( int i=0;i<pathSize;i++){
            String thisPath = allFilePaths.get(i);
            if(thisPath.indexOf(path) == 0){
                List<String> folders=Arrays.asList(thisPath.split("/"));
                thisList.add(thisPath);

            }

        }

        return thisList;


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
            final ArrayList<String> selFolderList=getNameList();
            final int size=selFolderList.size();
            ArrayList<String> audioIdList=new ArrayList<>();

            Cursor cursor=null;
            switch(item.getItemId()){

                case R.id.itemPlay:

                    for(int i=0;i<size;i++){
                        String currFolderName=selFolderList.get(i);//From the Card get the current folder name
                        String pathTosend;
                        if(!UtilFunctions.checkExtension(currFolderName)){
                            pathTosend= currentPath+currFolderName+"/";  // If check Extension returns false, it means it is a folder..so append "/"
                        }else{
                            pathTosend=currentPath+currFolderName;       // Means, the currFolderName here is song, so dont append "/"
                        }

                        HashSet<String> sendListHash = getAllSongs(pathTosend);
                        for(String path: sendListHash){
                            audioIdList.add(MainActivity.pathToIdInfo.get(path));
                        }
                    }
                    UtilFunctions.playSelectedSongs(getActivity(), audioIdList, 0, false);
                    break;

                case R.id.itemPlayNext:
                    for(int i=0;i<size;i++){
                        String currFolderName=selFolderList.get(i);//From the Card get the current folder name
                        String pathTosend;
                        if(!UtilFunctions.checkExtension(currFolderName)){
                            pathTosend= currentPath+currFolderName+"/";  // If check Extension returns false, it means it is a folder..so append "/"
                        }else{
                            pathTosend=currentPath+currFolderName;       // Means, the currFolderName here is song, so dont append "/"
                        }

                        HashSet<String> sendListHash = getAllSongs(pathTosend);
                        for(String path: sendListHash){
                            audioIdList.add(MainActivity.pathToIdInfo.get(path));
                        }
                    }
                    UtilFunctions.playSongsNext(getActivity(), audioIdList);
                    break;
                case R.id.itemAddQueue:
                    for(int i=0;i<size;i++){
                        String currFolderName=selFolderList.get(i);//From the Card get the current folder name
                        String pathTosend;
                        if(!UtilFunctions.checkExtension(currFolderName)){
                            pathTosend= currentPath+currFolderName+"/";  // If check Extension returns false, it means it is a folder..so append "/"
                        }else{
                            pathTosend=currentPath+currFolderName;       // Means, the currFolderName here is song, so dont append "/"
                        }

                        HashSet<String> sendListHash = getAllSongs(pathTosend);
                        for(String path: sendListHash){
                            audioIdList.add(MainActivity.pathToIdInfo.get(path));
                        }
                    }
                    UtilFunctions.addToQueueSongs(getActivity(), audioIdList);
                    break;

                case R.id.itemShuffle:
                    for(int i=0;i<size;i++){
                        String currFolderName=selFolderList.get(i);//From the Card get the current folder name
                        String pathTosend;
                        if(!UtilFunctions.checkExtension(currFolderName)){
                            pathTosend= currentPath+currFolderName+"/";  // If check Extension returns false, it means it is a folder..so append "/"
                        }else{
                            pathTosend=currentPath+currFolderName;       // Means, the currFolderName here is song, so dont append "/"
                        }

                        HashSet<String> sendListHash = getAllSongs(pathTosend);
                        for(String path: sendListHash){
                            audioIdList.add(MainActivity.pathToIdInfo.get(path));
                        }
                    }
                    UtilFunctions.playSelectedSongs(getActivity(), audioIdList, 0, true);
                    break;

                case R.id.itemShare:

                    Intent sharingIntent=new Intent(Intent.ACTION_SEND_MULTIPLE);
                    sharingIntent.setType("audio/*");
                    ArrayList<Uri> uris=new ArrayList<Uri>();
                    //   String mimetypes[]={"audio/*"};
                    for(int i=0;i<size;i++){
                        String currFolderName=selFolderList.get(i);;//From the Card get the current folder name
                        String pathTosend;
                        if(!UtilFunctions.checkExtension(currFolderName)){
                            pathTosend= currentPath+currFolderName+"/";  // If check Extension returns false, it means it is a folder..so append "/"
                        }else{
                            pathTosend=currentPath+currFolderName;       // Means, the currFolderName here is song, so dont append "/"
                        }

                        HashSet<String> sendListHash = getAllSongs(pathTosend);//Get all the songs that match the path

                        for(String path: sendListHash){
                            path="file://"+path;

                            Uri uri=Uri.parse(path);
                            uris.add(uri);
                        }
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
                    // addIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    addIntent.setPackage("com.android.bluetooth");
                    addIntent.setType("*/*");
                    addIntent.setAction(Intent.ACTION_SEND_MULTIPLE);

                    Intent intentarray[]={addIntent};
                    chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentarray);
                    startActivity(chooser);



                    break;

                case R.id.itemDelete:


                    for(int i=0;i<size;i++){
                        String currFolderName=selFolderList.get(i);//From the Card get the current folder name
                        String pathTosend;
                        if(!UtilFunctions.checkExtension(currFolderName)){
                            pathTosend= currentPath+currFolderName+"/";  // If check Extension returns false, it means it is a folder..so append "/"
                        }else{
                            pathTosend=currentPath+currFolderName;       // Means, the currFolderName here is song, so dont append "/"
                        }

                        HashSet<String> sendListHash = getAllSongs(pathTosend);
                        for(String path: sendListHash){
                            audioIdList.add(MainActivity.pathToIdInfo.get(path));
                        }
                    }

                    UtilFunctions.deletePopUp(getActivity(), getActivity(), audioIdList,"Are you sure you want to delete the selected songs?", "songs deleted");

                    break;
                case R.id.itemToPlaylist :
                    Intent intent=new Intent(getActivity(),PlayListSelectionTest.class);

                    for(int i=0;i<size;i++){
                        String currFolderName=selFolderList.get(i);//From the Card get the current folder name
                        String pathTosend;
                        if(!UtilFunctions.checkExtension(currFolderName)){
                            pathTosend= currentPath+currFolderName+"/";  // If check Extension returns false, it means it is a folder..so append "/"
                        }else{
                            pathTosend=currentPath+currFolderName;       // Means, the currFolderName here is song, so dont append "/"
                        }
                        HashSet<String> sendListHash = getAllSongs(pathTosend);//Get all the songs that match the path

                        // sendList.addAll(sendListHash);

                        int len=sendListHash.size();
                        for(String path: sendListHash){

                            audioIdList.add(MainActivity.pathToIdInfo.get(path));
                        }
                    }
                    intent.putExtra("selectedIdList", audioIdList);
                    startActivity(intent);


                    break;



            }

            return false;
        }



        private ArrayList<String> getNameList(){
            List<Integer> selItems = gra.getSelectedItems();
            ArrayList<String> selIdList = new ArrayList<String>();
            int size = selItems.size();
            int i;
            String title;
            for(i=0;i<size;i++){
                //title =   selCards.get(i).getTitle();
                String id=folders.get(selItems.get(i)).getFolderName();
                selIdList.add(id);

            }
            Collections.reverse(selIdList); // PROBLEM MAY ARISE
            return selIdList;

        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            gra.clearSelection();
            MainActivity.isLongClickOn = false;
            actionMode = null;
            MainActivity.mToolbar.getLayoutParams().height = MainActivity.mActionBarSize;
        }
    }
    public class FolderRecyclerAdapter extends SelectableAdapter<FolderRecyclerAdapter.FolderViewHolder>{
         List<FolderInfo> folders;
        Activity mactivity;
        private ClickInterface clickListener;

        public class FolderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

            CardView cv;
            TextView folderName;
            ImageView overflowButton;
            Activity viewActivity;
            private ClickInterface listener;
            public FolderViewHolder(Activity con,View itemView,ClickInterface listener) {
                super(itemView);
                cv = (CardView) itemView.findViewById(R.id.cv);
                folderName = (TextView) itemView.findViewById(R.id.folder_name);
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
                    // UtilFunctions.playSelectedSongs(viewActivity, MainActivity.idList, getAdapterPosition(), false);
                    String clickName = folders.get(getAdapterPosition()).getFolderName();
                    int position = songList.indexOf(MainActivity.pathToIdInfo.get(currentPath + clickName));
                    if(UtilFunctions.checkExtension(clickName)){
                        Intent intent=new Intent(viewActivity,PlayActivity.class);
                        intent.putExtra("do", "Play");
                        intent.putExtra("position",position);
                        intent.putStringArrayListExtra("playingList", songList);
                        viewActivity.startActivity(intent);
                        return;
                    }
                    currLevel++;
                    currentPath = currentPath+clickName+"/";
                    populateRecyclerView();
                    return;
                }
                if (listener != null) {
                    listener.onItemClicked(getAdapterPosition());
                }
            }

        }

        public FolderRecyclerAdapter(ClickInterface clickListener,List<FolderInfo> objs,Activity act){
            this.folders = objs;
            this.mactivity = act;
            this.clickListener = clickListener;
        }

        @Override
        public FolderRecyclerAdapter.FolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_layout, parent, false);


            FolderViewHolder gvh = new FolderViewHolder(mactivity,v,clickListener);
            return gvh;
        }

        @Override
        public void onBindViewHolder(FolderRecyclerAdapter.FolderViewHolder holder, final int position) {
            if(position%2 == 0) {
                holder.cv.setBackgroundColor(ColorUtils.secondaryBgColor);

            }else{
                holder.cv.setBackgroundColor(ColorUtils.primaryBgColor);
            }
            holder.folderName.setText(folders.get(position).getFolderName());
            holder.folderName.setTextColor(ColorUtils.primaryTextColor);
            holder.overflowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    overflowReaction(v,folders.get(position),position);
                    //MainActivity.fourthOverflowReaction(v,mactivity,folders.get(position));
                }
            });


        }

        @Override
        public int getItemCount() {
            return folders.size();
        }

        public void removeAt(int position) {
            folders.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, folders.size());
        }
        private void overflowReaction(View v, final FolderInfo selFolder, final int currPosition){
            PopupMenu popupMenu = new PopupMenu(getActivity(),v);
            popupMenu.inflate(R.menu.long_click_actions);
            View parentRow=(View) v.getParent();

            final String currFolderName=selFolder.getFolderName();

            final String pathTosend;
            if(!UtilFunctions.checkExtension(currFolderName)){
                pathTosend= currentPath+currFolderName+"/";
            }else{
                pathTosend=currentPath+currFolderName;
            }
            // final String songName=titleList.get(currPosition);
            //  Toast.makeText(getActivity(), songName +"and position is" + currPosition, Toast.LENGTH_LONG).show();

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    HashSet<String> sendListHash = getAllSongs(pathTosend);
                    final ArrayList<String> audioIdList = new ArrayList<>();

                    switch (item.getItemId()) {
                        case R.id.cnt_menu_play:

                            if (UtilFunctions.checkExtension(currFolderName)) {
                                UtilFunctions.playSelectedSongs(getActivity(), songList, selFolder.getsNo(), false);
                            } else {
                                int len = sendListHash.size();
                                for (String path : sendListHash) {

                                    audioIdList.add(MainActivity.pathToIdInfo.get(path));
                                }
                                UtilFunctions.playSelectedSongs(getActivity(), audioIdList, 0, false);
                            }
                            break;
                        case R.id.cnt_menu_play_next:
                            ArrayList<String> sendList = new ArrayList<String>();

                            if (UtilFunctions.checkExtension(currFolderName)) {
                                sendList.add(songList.get(selFolder.getsNo()));
                                UtilFunctions.playSongsNext(getActivity(), sendList);
                            } else {
                                int len = sendListHash.size();
                                for (String path : sendListHash) {

                                    audioIdList.add(MainActivity.pathToIdInfo.get(path));
                                }
                                UtilFunctions.playSongsNext(getActivity(), audioIdList);

                            }
                            break;
                        case R.id.cnt_menu_add_queue:
                            sendList = new ArrayList<String>();

                            if (UtilFunctions.checkExtension(currFolderName)) {
                                sendList.add(songList.get(selFolder.getsNo()));
                                UtilFunctions.addToQueueSongs(getActivity(), sendList);
                            } else {
                                int len = sendListHash.size();
                                for (String path : sendListHash) {

                                    audioIdList.add(MainActivity.pathToIdInfo.get(path));
                                }
                                UtilFunctions.addToQueueSongs(getActivity(), audioIdList);

                            }
                            break;
                        case R.id.addToPlaylist:


                            // sendList.addAll(sendListHash);
                            Intent intent = new Intent(getActivity(), PlayListSelectionTest.class);
                            int len = sendListHash.size();
                            for (String path : sendListHash) {

                                audioIdList.add(MainActivity.pathToIdInfo.get(path));
                            }
                            intent.putExtra("selectedIdList", audioIdList);
                            startActivity(intent);


                            break;
                        case R.id.cnt_mnu_edit:

                            // changeSongInfo(selectedId);

                            //Toast.makeText(this, "Edit :" , Toast.LENGTH_SHORT).show();

                            break;
                        case R.id.cnt_mnu_delete:

                            for (String path : sendListHash) {
                                audioIdList.add(MainActivity.pathToIdInfo.get(path));
                            }
                            UtilFunctions.deletePopUp(getActivity(), getActivity(), audioIdList, "Are you sure you want to delete the selected songs?", "songs deleted");
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Confirm Delete");
                            builder.setMessage("Are you sure you want to delete the selected songs?");
                            builder.setCancelable(true);
                            builder.setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            ContentResolver resolver = getActivity().getApplicationContext().getContentResolver();
                                            int size = audioIdList.size();
                                            for (int i = 0; i < size; i++) {
                                                String selectedId = audioIdList.get(i);
                                                //String songName = FirstFragment.idToName.get(Long.parseLong(selectedId));
                                                File file = new File(MainActivity.idToTrackObj.get(Long.parseLong(selectedId)).getTrackPath());
                                                boolean deleted = file.delete();
                                                if (deleted)
                                                    resolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media._ID + " LIKE \"" + selectedId + "\"", null);

                                            }
                                            Toast.makeText(getActivity(), size + " Songs Deleted", Toast.LENGTH_SHORT).show();
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

                            Intent sharingIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                            sharingIntent.setType("audio/*");
                            ArrayList<Uri> uris = new ArrayList<Uri>();
                            //   String mimetypes[]={"audio/*"};
                            sendListHash = getAllSongs(pathTosend);
                            for (String path : sendListHash) {
                                path = "file://" + path;

                                // Toast.makeText(getContext(),"Hi there", Toast.LENGTH_LONG).show();
                                Uri uri = Uri.parse(path);
                                uris.add(uri);
                            }


                            // Uri uri=Uri.parse(pathList.get(currPosition));

                            sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                            //	 sharingIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                            // sharingIntent.putExtra(Intent.EXTRA_TEXT, songName);
                            Intent chooser = new Intent(Intent.ACTION_CHOOSER);
                            chooser.putExtra(Intent.EXTRA_INTENT, sharingIntent);
                            chooser.putExtra(Intent.EXTRA_TITLE, "title");
                            Intent addIntent = new Intent();
                            // addIntent.setComponent(new ComponentName("com.android.bluetooth","com.android.bluetooth.opp.BluetoothOppLauncherActivity"));
                            addIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                            addIntent.setPackage("com.android.bluetooth");
                            addIntent.setType("*/*");
                            addIntent.setAction(Intent.ACTION_SEND_MULTIPLE);

                            Intent intentarray[] = {addIntent};
                            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentarray);
                            startActivity(chooser);


                            break;

                    }

                    return true;

                }
            });
            popupMenu.show();
            //Toast.makeText(MainActivity.this, "It works, pos=" + v.getTag(), Toast.LENGTH_LONG).show();
        }


    }



}
