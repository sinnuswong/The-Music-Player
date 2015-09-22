package com.Project100Pi.themusicplayer;


import java.io.File;
import java.nio.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

import dalvik.system.PathClassLoader;
import android.R.color;
import android.R.integer;
import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Path;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Audio.GenresColumns;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardArrayMultiChoiceAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener;
import it.gmariotti.cardslib.library.prototypes.CardSection;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.base.CardViewWrapper;




public class SixthFragment extends Fragment  {
	 CardArrayMultiChoiceAdapter mCardArrayAdapter = null;
	 ArrayList<String> allFilePaths = new ArrayList<String>();
	//  static HashMap<String, String> pathToNameInfo =  new HashMap<String,String>();
	  static HashMap<String, String> pathToIdInfo =  new HashMap<String,String>();
	 int pathSize = 0;
	  int currLevel = 1;
	  String currentPath = "/";
	  HashSet<String> thisList = null;
	  ArrayList<Card> cards = null;
	  View v = null;


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
          super.onCreateView(inflater, container, savedInstanceState);


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
        	 pathToIdInfo.put(path, thisId);

          }
          v =inflater.inflate(R.layout.sixth_frag, container, false);
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
      				 populateCards();

                      return true;

                  }

                  return false;
              }
          });
         populateCards();
        return v;
    }

 @Override
public void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);


 }

    public static SixthFragment newInstance(String text) {

        SixthFragment f = new SixthFragment();
        return f;
    }

    public enum SupportedFileFormat
    {
        _3GP("3gp"),
        MP4("mp4"),
        M4A("m4a"),
        AAC("aac"),
        TS("ts"),
        FLAC("flac"),
        MP3("mp3"),
        MID("mid"),
        XMF("xmf"),
        MXMF("mxmf"),
        RTTTL("rtttl"),
        RTX("rtx"),
        OTA("ota"),
        IMY("imy"),
        OGG("ogg"),
        MKV("mkv"),
        WAV("wav");

        private String filesuffix;

        SupportedFileFormat( String filesuffix ) {
            this.filesuffix = filesuffix;
        }

        public String getFilesuffix() {
            return filesuffix;
        }
    }
    public boolean checkExtension( String fileName ) {
        String ext = getFileExtension(fileName);
        if ( ext == null) return false;
        try {
            if ( SupportedFileFormat.valueOf(ext.toUpperCase()) != null ) {
                return true;
            }
        } catch(IllegalArgumentException e) {
            return false;
        }
        return false;
    }

    public String getFileExtension( String fileName ) {
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            return fileName.substring(i+1);
        } else
            return null;
    }
    public void populateCards(){

    	 cards = new ArrayList<Card>();
         pathSize = allFilePaths.size();
         int i;
    	 thisList = new HashSet<>();
    	 final ArrayList<String> songList = new ArrayList<String>();
         for( i=0;i<pathSize;i++){
       	 String thisPath = allFilePaths.get(i);
       	 if(thisPath.indexOf(currentPath) == 0){
       	  List<String> folders=Arrays.asList(thisPath.split("/"));
       	  thisList.add(folders.get(currLevel));

       	 }

         }
         for (String s : thisList) {
       	  Card card = new Card(this.getActivity());
       	  CardHeader header = new CardHeader(this.getActivity());

             //Add Header to card
             card.addCardHeader(header);
             header.setTitle(s);

             card.setClickable(true);

             if(checkExtension(s)){

            	 songList.add(pathToIdInfo.get(currentPath+s));
             }
             card.setOnClickListener(new OnCardClickListener() {

 				@Override
 				public void onClick(Card card, View view) {
 					// TODO Auto-generated method stub
 					String clickName = card.getCardHeader().getTitle();
 					if(checkExtension(clickName)){
 					int position = songList.indexOf(pathToIdInfo.get(currentPath+clickName));
 					 Intent intent=new Intent(getActivity(),PlayActivity.class);
					 	intent.putExtra("do", "Play");
			           intent.putExtra("position", position);
			            intent.putStringArrayListExtra("playingList", songList);
			            startActivity(intent);
			            return;
 					}
 					 currLevel++;
 					 currentPath = currentPath+card.getCardHeader().getTitle()+"/";
 					 populateCards();
 				}
 			});
             card.setOnLongClickListener(new Card.OnLongCardClickListener() {
                 @Override
                 public boolean onLongClick(Card card, View view) {
                     return mCardArrayAdapter.startActionMode(getActivity());

                 }
            });
             cards.add(card);
       	}


      mCardArrayAdapter = new CardArrayMultiChoiceAdapter(this.getActivity(),cards){




  		@Override
  		 public View getView( final int position, View convertView, final ViewGroup parent) {

  			    View v =super.getView(position, convertView, parent);
  			 //   Card card=cards.get(position);
  			//    Long id = genresIdList.get(position);
			LinearLayout cardLayout = (LinearLayout) v.findViewById(R.id.card_main_layout);
			if(position%2 == 0 ) {
				cardLayout.setBackgroundColor(Color.parseColor("#3D3D3D"));
			}else{
				cardLayout.setBackgroundColor(Color.parseColor("#484848"));
			}

  			    if( convertView != null )
  			        v = convertView;
  			    else{
  			    	//LayoutInflater inflater=getLayoutInflater();
  			    	//v=inflater.inflate(R.layout.my_two_lines, parent, false);
  			    	//TextView t1 = (TextView) v.findViewById(R.id.line_app);
  			    final ImageView button = (ImageView)v.findViewById(R.id.overflow_button);
  			   //t1.setTag(position);
  			    button.setTag(position);
  			//    Toast.makeText(getActivity()," position is " + position, Toast.LENGTH_LONG).show();



  			    button.setOnClickListener(new OnClickListener() {
  	                public void onClick(View v) {

  	                	PopupMenu popupMenu = new PopupMenu(getContext(),v);
  	            	    popupMenu.inflate(R.menu.long_click_actions);
  	            	    View parentRow=(View) v.getParent();
  			            CardListView myListView=(CardListView) parentRow.getParent();
  			            final int currPosition=myListView.getPositionForView(parentRow);
  			            final Card card=cards.get(currPosition);
  			            final String currFolderName=card.getCardHeader().getTitle();

  			             final String pathTosend;
  			             if(!checkExtension(currFolderName)){
  			            		pathTosend= currentPath+currFolderName+"/";
  			             }else{
  			            	    pathTosend=currentPath+currFolderName;
  			             }
  			           // final String songName=titleList.get(currPosition);
  			             final Long selectedAlbumId=(long) 0;
  			          //  Toast.makeText(getActivity(), songName +"and position is" + currPosition, Toast.LENGTH_LONG).show();

  	            	    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

  	            			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  							@Override
  	            			public boolean onMenuItemClick(MenuItem item) {
  	            				 HashSet<String> sendListHash = getAllSongs(pathTosend);
	            			      ArrayList<String> audioIdList=new ArrayList<>();

  	            			       switch(item.getItemId()){
  	            			       case R.id.cnt_menu_play:

  	            			    	 if(checkExtension(currFolderName)){
  	            			    	 UtilFunctions.playSelectedSongs(getActivity(), songList,cards.indexOf(card), false);
  	            			    	 }else{
  	            			    		int len=sendListHash.size();
  	  	            			    	 for(String path: sendListHash){

  	  	            			    		 audioIdList.add(pathToIdInfo.get(path));
  	  	            			    	 }
  	            			    		UtilFunctions.playSelectedSongs(getActivity(), audioIdList,0, false);
  	            			    	 }
  	            			    	   break;
  	            			       case R.id.cnt_menu_play_next:
  	            			    	 ArrayList<String> sendList = new ArrayList<String>();

  	            			    	 if(checkExtension(currFolderName)){
  	            			    		sendList.add(songList.get( cards.indexOf(card)));
  	            			    		 UtilFunctions.playSongsNext(getActivity(),sendList);
  	            			    	 }else{
  	            			    		int len=sendListHash.size();
  	  	            			    	 for(String path: sendListHash){

  	  	            			    		 audioIdList.add(pathToIdInfo.get(path));
  	  	            			    	 }
  	            			    		 UtilFunctions.playSongsNext(getActivity(), audioIdList);

  	            			    	 }
  	            			    	   break;
  	            			       case R.id.cnt_menu_add_queue:
  	            			    	   sendList = new ArrayList<String>();

  	            			    	 if(checkExtension(currFolderName)){
  	            			    		sendList.add(songList.get( cards.indexOf(card)));
  	            			    		 UtilFunctions.addToQueueSongs(getActivity(), sendList);
  	            			    	 }else{
  	            			    		 int len=sendListHash.size();
  	  	            			    	 for(String path: sendListHash){

  	  	            			    		 audioIdList.add(pathToIdInfo.get(path));
  	  	            			    	 }
  	            			    		 UtilFunctions.addToQueueSongs(getActivity(), audioIdList);

  	            			    	 }
  	            			    	   break;
  	            			       case R.id.addToPlaylist:


  	            			    	 // sendList.addAll(sendListHash);
  	            			    	 Intent intent=new Intent(getActivity(),PlaylistSelectionMultiple.class);
  	            			    	  int len=sendListHash.size();
  	            			    	 for(String path: sendListHash){

  	            			    		 audioIdList.add(pathToIdInfo.get(path));
  	            			    	 }
  	            			    	 intent.putExtra("selectedIdList", audioIdList);
             			             startActivity(intent);






  	            			    	   break;
  	            			           case R.id.cnt_mnu_edit:

  	                                     // changeSongInfo(selectedId);

  	            			               //Toast.makeText(this, "Edit :" , Toast.LENGTH_SHORT).show();

  	            			               break;
  	            			           case R.id.cnt_mnu_delete:

  	  	            			    	 for(String path: sendListHash){
  	  	            			    		 audioIdList.add(pathToIdInfo.get(path));
  	  	            			    	 }
  	            			        	   UtilFunctions.deletePopUp(getContext(), getActivity(), audioIdList,"Are you sure you want to delete the selected songs?", "songs deleted");
  	            			        	   break;
  	            			           case R.id.cnt_mnu_share:

  	            			        	Intent sharingIntent=new Intent(Intent.ACTION_SEND_MULTIPLE);
              			        	    sharingIntent.setType("audio/*");
              			        	    ArrayList<Uri> uris=new ArrayList<Uri>();
              			        	//   String mimetypes[]={"audio/*"};
              			        	    sendListHash = getAllSongs(pathTosend);
              			        	    for(String path: sendListHash){
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
  	            			      	     startActivity(chooser);




  	            			               break;

  	            			       }

  	            			       return true;

  	            			}
  	            	    });
  	            	    popupMenu.show();
  	                	//Toast.makeText(MainActivity.this, "It works, pos=" + v.getTag(), Toast.LENGTH_LONG).show();
  	                }
  	                });
  			   return v;
  			  }
  				return v;
  			}
  		@Override
  	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
  	        //It is very important to call the super method
  	        super.onCreateActionMode(mode, menu);

  	        ActionMode mActionMode = mode; // to manage mode in your Fragment/Activity

  	        //If you would like to inflate your menu
  	        MenuInflater inflater = mode.getMenuInflater();
  	        inflater.inflate(R.menu.multi_choice_options, menu);

  	        return true;
  	    }

  	    @Override
  	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
  	        return false;
  	    }

  	    @Override
  	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
  	    	 final ArrayList<Card> selCardList=getSelectedCards();
  	    	 final int size=selCardList.size();
  	    	 ArrayList<String> audioIdList=new ArrayList<>();

  	    	 Cursor cursor=null;
  	    	switch(item.getItemId()){

  	    	case R.id.itemPlay:

  	    		for(int i=0;i<size;i++){
  	        	  Card card=selCardList.get(i);
  	              String currFolderName=card.getCardHeader().getTitle();//From the Card get the current folder name
  	              String pathTosend;
  	              if(!checkExtension(currFolderName)){
  	            		pathTosend= currentPath+currFolderName+"/";  // If check Extension returns false, it means it is a folder..so append "/"
  	             }else{
  	            	    pathTosend=currentPath+currFolderName;       // Means, the currFolderName here is song, so dont append "/"
  	             }

  	        	 HashSet<String> sendListHash = getAllSongs(pathTosend);
    	        	 for(String path: sendListHash){
  			    		 audioIdList.add(pathToIdInfo.get(path));
  			     }
    	      	  }
  	    	UtilFunctions.playSelectedSongs(getActivity(), audioIdList, 0, false);
  	    		break;

  	    	case R.id.itemPlayNext:
  	    		for(int i=0;i<size;i++){
  	    		Card card=selCardList.get(i);
	              String currFolderName=card.getCardHeader().getTitle();//From the Card get the current folder name
	              String pathTosend;
	              if(!checkExtension(currFolderName)){
	            		pathTosend= currentPath+currFolderName+"/";  // If check Extension returns false, it means it is a folder..so append "/"
	             }else{
	            	    pathTosend=currentPath+currFolderName;       // Means, the currFolderName here is song, so dont append "/"
	             }

	        	 HashSet<String> sendListHash = getAllSongs(pathTosend);
  	        	 for(String path: sendListHash){
			    		 audioIdList.add(pathToIdInfo.get(path));
			     }
  	      	  }
  	    	UtilFunctions.playSongsNext(getActivity(), audioIdList);
  	    		break;
  	    	case R.id.itemAddQueue:
  	    		for(int i=0;i<size;i++){
  	  	    		Card card=selCardList.get(i);
  		              String currFolderName=card.getCardHeader().getTitle();//From the Card get the current folder name
  		              String pathTosend;
  		              if(!checkExtension(currFolderName)){
  		            		pathTosend= currentPath+currFolderName+"/";  // If check Extension returns false, it means it is a folder..so append "/"
  		             }else{
  		            	    pathTosend=currentPath+currFolderName;       // Means, the currFolderName here is song, so dont append "/"
  		             }

  		        	 HashSet<String> sendListHash = getAllSongs(pathTosend);
  	  	        	 for(String path: sendListHash){
  				    		 audioIdList.add(pathToIdInfo.get(path));
  				     }
  	  	      	  }
  	    		UtilFunctions.addToQueueSongs(getActivity(), audioIdList);
  	    		break;

  	    	case R.id.itemShuffle:
  	    		for(int i=0;i<size;i++){
  	  	    		Card card=selCardList.get(i);
  		              String currFolderName=card.getCardHeader().getTitle();//From the Card get the current folder name
  		              String pathTosend;
  		              if(!checkExtension(currFolderName)){
  		            		pathTosend= currentPath+currFolderName+"/";  // If check Extension returns false, it means it is a folder..so append "/"
  		             }else{
  		            	    pathTosend=currentPath+currFolderName;       // Means, the currFolderName here is song, so dont append "/"
  		             }

  		        	 HashSet<String> sendListHash = getAllSongs(pathTosend);
  	  	        	 for(String path: sendListHash){
  				    		 audioIdList.add(pathToIdInfo.get(path));
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
	    	        	  Card card=selCardList.get(i);
	    	              String currFolderName=card.getCardHeader().getTitle();//From the Card get the current folder name
	    	              String pathTosend;
	    	              if(!checkExtension(currFolderName)){
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
	        	  Card card=selCardList.get(i);
	              String currFolderName=card.getCardHeader().getTitle();//From the Card get the current folder name
	              String pathTosend;
	              if(!checkExtension(currFolderName)){
	            		pathTosend= currentPath+currFolderName+"/";  // If check Extension returns false, it means it is a folder..so append "/"
	             }else{
	            	    pathTosend=currentPath+currFolderName;       // Means, the currFolderName here is song, so dont append "/"
	             }

	        	 HashSet<String> sendListHash = getAllSongs(pathTosend);
  	        	 for(String path: sendListHash){
			    		 audioIdList.add(pathToIdInfo.get(path));
			     }
  	      	  }

  	      UtilFunctions.deletePopUp(getContext(), getActivity(), audioIdList,"Are you sure you want to delete the selected songs?", "songs deleted");

  	        	break;
  	        case R.id.itemToPlaylist :
  	        	 Intent intent=new Intent(getActivity(),PlaylistSelectionMultiple.class);

  	        	for(int i=0;i<size;i++){

  	        	  Card card=selCardList.get(i);
  	              String currFolderName=card.getCardHeader().getTitle();//From the Card get the current folder name
  	              String pathTosend;
	              if(!checkExtension(currFolderName)){
	            		pathTosend= currentPath+currFolderName+"/";  // If check Extension returns false, it means it is a folder..so append "/"
	              }else{
	            	    pathTosend=currentPath+currFolderName;       // Means, the currFolderName here is song, so dont append "/"
	              }
  	        	  HashSet<String> sendListHash = getAllSongs(pathTosend);//Get all the songs that match the path

		    	 // sendList.addAll(sendListHash);

		    	    int len=sendListHash.size();
		    	    for(String path: sendListHash){

		    		    audioIdList.add(pathToIdInfo.get(path));
		    	     }
  	        	}
		    	 intent.putExtra("selectedIdList", audioIdList);
	             startActivity(intent);


  	        	break;



  	    	}

  	        return false;
  	    }

  	   private ArrayList<String> getidList(){
  		   ArrayList<Card> selCards = getSelectedCards();
  		   ArrayList<String> selIdList = new ArrayList<String>();
  		   int size = selCards.size();
  		   int i;
  		   String title;
  		   for(i=0;i<size;i++){
  			//title =   selCards.get(i).getTitle();
  		     String id=selCards.get(i).getId();
  		     selIdList.add(id);

  		   }
  		return selIdList;

  	   }

  	    private void discardSelectedItems(ActionMode mode) {
  	        ArrayList<Card> items = getSelectedCards();
  	        for (Card item : items) {
  	            remove(item);
  	        }
  	        mode.finish();
  	    }


  	    private String formatCheckedCard() {

  	        SparseBooleanArray checked = mCardListView.getCheckedItemPositions();
  	        StringBuffer sb = new StringBuffer();
  	        for (int i = 0; i < checked.size(); i++) {
  	            if (checked.valueAt(i) == true) {
  	                sb.append("\nPosition=" + checked.keyAt(i));
  	            }
  	        }
  	        return sb.toString();
  	    }

  		@Override
  		public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
  				boolean checked, CardViewWrapper cardView, Card card) {
  			// TODO Auto-generated method stub
  			ArrayList<Card> items = getSelectedCards();
  			Toast.makeText(getContext(), items.size()+" cards selected", Toast.LENGTH_SHORT).show();
  		}

  	   };
		/*
  	 Button shuffleSixthFrag = (Button) v.findViewById(R.id.shuffle_sixth_frag);
     shuffleSixthFrag.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ArrayList<String> sendList= new ArrayList<String>();
			 HashSet<String> sendListHash = getAllSongs(currentPath);
			 int len=sendListHash.size();
		    	 for(String path: sendListHash){

		    		sendList.add(pathToIdInfo.get(path));
		    	 }
			UtilFunctions.playSelectedSongs(getActivity(), sendList, 0, true);
		}
	});
	*/
      NestedCardListView listView = (NestedCardListView) v.findViewById(R.id.sixthFragList);
      listView.setFastScrollEnabled(true);
      if (listView!=null){
          listView.setAdapter(mCardArrayAdapter);
          listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

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

}

