package com.Project100Pi.themusicplayer;



import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener;
import it.gmariotti.cardslib.library.view.CardListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;



public class PlaylistSelectionMultiple extends Activity {
	     String selectedlist;
	   
      //  static LinkedHashMap<String,String> innerMap=new LinkedHashMap<String,String>();
      //  SongsAlbArt playListObj= null;
         ArrayList<String> playlistnames=null;
     	 ArrayList<Long> playlistIdLists=null;
     	 String songName=null;
     	 Long audioId;
     	 ArrayList<String> audioIdList=null;
     	 int count;
     	 
     	//ArrayList<String> selsongpath= new ArrayList<String>();
		protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.playlist_selection);
           
            Button button;
        	
       
        	Intent i=getIntent();
        	//songName=i.getExtras().getString("songName");
        //	audioId=i.getLongExtra("selectedId",0);
        	audioIdList=i.getExtras().getStringArrayList("selectedIdList");
        	count = audioIdList.size();
        	final String[] PROJECTION_PLAYLIST = new String[] {
            	    MediaStore.Audio.Playlists._ID,
            	    MediaStore.Audio.Playlists.NAME,
            	    MediaStore.Audio.Playlists.DATA
            	};
            
            Cursor cursor=this.getApplicationContext().getContentResolver().query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, PROJECTION_PLAYLIST, null, null,MediaStore.Audio.Playlists.DATE_MODIFIED);
            playlistnames=new ArrayList<String>();
            playlistIdLists=new ArrayList<Long>();
            while (cursor.moveToNext()) {
            	playlistnames.add(cursor.getString(1));
            	playlistIdLists.add(cursor.getLong(0));
            }
        	//selsongpath=(ArrayList<String>)i.getExtras().getStringArrayList("songpath");
        	//Toast.makeText(PlaylistSelection.this, selsongname.get(0) +' '+selsongpath.get(0), Toast.LENGTH_LONG).show();
            button = (Button) findViewById(R.id.more_button);
        	

        	button.setOnClickListener(new OnClickListener() {

        		@Override
        		public void onClick(View view) {
        			showInputDialog();
        		}
        	});
        	ArrayList<Card> cards = new ArrayList<Card>();
        	int size = playlistnames.size();
        	for(int j = 0;j<size;j++){
        		
        		 Card card = new Card(this);

                 //Create a CardHeader
                 CardHeader header = new CardHeader(this);
           
                 //Add Header to card
                 card.addCardHeader(header);
                 header.setTitle( playlistnames.get(j));
                 card.setTitle( playlistnames.get(j));
                 card.setClickable(true);
                 card.setId(""+j);
                 card.setOnClickListener(new OnCardClickListener() {

        			@Override
        			public void onClick(Card card, View arg1) {
        				
        			
        			//	for(int i=0;i<count;i++){
        				// long  audioId=Long.parseLong(audioIdList.get(i));
        				
        				Long playlistId=playlistIdLists.get(Integer.parseInt(card.getId()));
        				/* Cursor cursor = PlaylistSelection.this.getContentResolver().query(
        						 MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
        						 PROJECTION_PLAYLIST, 
        						 MediaStore.Audio.Playlists._ID +" LIKE \""+playlistId,
        						 null,
        						 MediaStore.Audio.Playlists.DATE_MODIFIE);*/
        				 addMultipleToPlaylist(getContentResolver(), audioIdList,playlistId);
        				//}
        				Toast.makeText(PlaylistSelectionMultiple.this,count+" song(s) added to "+card.getTitle(), Toast.LENGTH_SHORT).show();
        				// Toast.makeText(PlaylistSelection.this,songName+" added to "+card.getTitle(), Toast.LENGTH_SHORT).show();
        				 finish();
        				 // Have to write query to get the playlist songs
        				
        				// TODO Auto-generated method stub
        				
        			}
                 
                 });
        				
                 cards.add(card);
                 
        	}
        	 CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(this,cards);
        	    
             CardListView listView = (CardListView)findViewById(R.id.playListSelectFragList);
             if (listView!=null){
                 listView.setAdapter(mCardArrayAdapter);
             }
     
        
		}		
        	
         
           private void showInputDialog() {
        	// get prompts.xml view
        		LayoutInflater layoutInflater = LayoutInflater.from(this);
        		View promptView = layoutInflater.inflate(R.layout.dialog_box, null);
        		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        		alertDialogBuilder.setView(promptView);

        		final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        		// setup a dialog window
        		alertDialogBuilder.setCancelable(false)
        				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        					public void onClick(DialogInterface dialog, int id) {
        						 selectedlist=editText.getText().toString();
        						
        						    playlistnames.add(editText.getText().toString());
        						    createNewPlayList(editText.getText().toString());
        						 
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
        	// TODO Auto-generated method stub
        	
        }
           public static void addToPlaylist(ContentResolver resolver, Long audioId,Long playlistID) {

               String[] cols = new String[] {
                       "count(*)"
               };
               Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistID);
               Cursor cur = resolver.query(uri, cols, null, null, null);
               cur.moveToFirst();
               final int base = cur.getInt(0);
               cur.close();
               ContentValues values = new ContentValues();
               values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, Long.valueOf(base));
               values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audioId);
               
               resolver.insert(uri, values);
           }
           
           public static void addMultipleToPlaylist(ContentResolver resolver, ArrayList<String> audioIdListCurr,Long playlistID) {

               String[] cols = new String[] {
                       "count(*)"
               };
               Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistID);
               Cursor cur = resolver.query(uri, cols, null, null, null);
               cur.moveToFirst();
               final int base = cur.getInt(0);
               cur.close();
               int count=audioIdListCurr.size();
               ContentValues[] bulkInsertArray=new ContentValues[count];
               for(int i=0;i<count;i++){
               Long audioId=Long.parseLong(audioIdListCurr.get(i));
               ContentValues values = new ContentValues();
               
               values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, Long.valueOf(base+i));
               values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audioId); 
               bulkInsertArray[i]=values;
              // resolver.insert(uri, values);
               }
               resolver.bulkInsert(uri,bulkInsertArray );
           }

            public  void createNewPlayList(String name)
            {
            	  ContentValues mInserts = new ContentValues();
                  mInserts.put(MediaStore.Audio.Playlists.NAME, name);
                  mInserts.put(MediaStore.Audio.Playlists.DATE_ADDED, System.currentTimeMillis());
                  mInserts.put(MediaStore.Audio.Playlists.DATE_MODIFIED, System.currentTimeMillis());
                  ContentResolver mContentResolver=this.getApplicationContext().getContentResolver();
                  Cursor cursor=null;
                  Uri mUri = mContentResolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, mInserts);
                  final String[] PROJECTION_PLAYLIST = new String[] {
                	    MediaStore.Audio.Playlists._ID,
                	    MediaStore.Audio.Playlists.NAME,
                	    MediaStore.Audio.Playlists.DATA
                	};
                  long mPlaylistId;
                  if (mUri != null) {
                      mPlaylistId = -1;
                      //mResult = FM.SUCCESS;
                      cursor = mContentResolver.query(mUri, PROJECTION_PLAYLIST, null, null,MediaStore.Audio.Playlists.DATE_MODIFIED);
                      if (cursor != null) {
                    	  cursor.moveToLast();
                          // Save the newly created ID so it can be selected.  Names are allowed to be duplicated,
                          // but IDs can never be.
                          mPlaylistId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID));
                          cursor.close();
                      }
                     
                     
         				
        
         				 addMultipleToPlaylist(getContentResolver(), audioIdList,mPlaylistId);
         				
         				Toast.makeText(PlaylistSelectionMultiple.this,count+" song(s) added to "+name, Toast.LENGTH_SHORT).show();
         				 finish();
                  }
            }

	 
	    
		    
		        
	   }


	

	
