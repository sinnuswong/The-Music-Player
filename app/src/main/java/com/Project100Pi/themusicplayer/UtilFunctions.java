package com.Project100Pi.themusicplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UtilFunctions {
	
	static Long mGenreId;
	static boolean isDeleted  = false;
	
	public static String convertSecondsToHMmSs(long milliseconds) {
		int seconds = (int) (milliseconds / 1000) % 60 ;
		int minutes = (int) ((milliseconds / (1000*60)) % 60);
		int hours   = (int) ((milliseconds / (1000*60*60)) % 24);
		if(hours > 0){
	    return String.format("%d:%02d:%02d", hours,minutes,seconds);
		} else{
			if(minutes > 9){
				return String.format("%02d:%02d", minutes,seconds);
			}else {
				return String.format("%01d:%02d",minutes,seconds);
			}
			
		}
	   

	}
	
	// function to delete the songs when passed with the arrayList containing ID of the songs to be deleted
	public static void deletePopUp(Context context,final Activity activity,final ArrayList<String> selIdList,String message,final String toastMessage){
		isDeleted = false;
		AlertDialog.Builder builder=new AlertDialog.Builder(context);
		builder.setTitle("Confirm Delete");
		builder.setMessage(message);
		builder.setCancelable(true);
		builder.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						ContentResolver resolver = activity.getApplicationContext().getContentResolver();
						int size = selIdList.size();
						for (int i = 0; i < size; i++) {
							String selectedId = selIdList.get(i);
							//String songName = FirstFragment.idToName.get(Long.parseLong(selectedId));
							File file = new File(MainActivity.idToTrackObj.get(Long.parseLong(selectedId)).getTrackPath());
							boolean deleted = file.delete();
							if(deleted)
								resolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media._ID + " LIKE \"" + selectedId + "\"", null);

						}
						Toast.makeText(activity, size + " " + toastMessage, Toast.LENGTH_SHORT).show();
						isDeleted = true;
						dialog.cancel();

					}

				});
        builder.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						isDeleted =false;
						dialog.cancel();
					}
				});

        AlertDialog alert11 = builder.create();
        alert11.show();

	}
	
	//To delete Multiple Songs,Albums,Artists 
	public static void deleteMultiplePopUp(Context context,final Activity activity,final ArrayList<String> selIdList,String message,final String toastMessage,final String choice){
		isDeleted = false;
		AlertDialog.Builder builder=new AlertDialog.Builder(context);
		builder.setTitle("Confirm Delete");
		builder.setMessage(message);
		builder.setCancelable(true);
		builder.setPositiveButton("Yes", 
        new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	 ContentResolver resolver=activity.getApplicationContext().getContentResolver();
            	 int size=selIdList.size();
            	 for(int i=0;i<size;i++)
	        	{           	  
            	   String selectedId=selIdList.get(i);
            	   Cursor cursor=CursorClass.makeCursorBasedOnChoice(activity, Long.parseLong(selectedId), choice);
            	   while(cursor.moveToNext()){
        	    	   Long songId=cursor.getLong(0);
					   File file = new File(MainActivity.idToTrackObj.get(songId).getTrackPath());
					   boolean deleted = file.delete();
					   if(deleted)
            	       	resolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media._ID + " LIKE \"" + songId + "\"", null);

	        		    }
	        		  }
            	   Toast.makeText(activity, size+ " " +toastMessage,Toast.LENGTH_SHORT).show();
				isDeleted = true;
                   dialog.cancel();
            }
        });
        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
				isDeleted = false;
                dialog.cancel();
            }
        });

        AlertDialog alert11 = builder.create();
        alert11.show();
	}
	
	// To delete single album,artist,and genre
	public static void deleteSinglePopUp(Context context,final Activity activity,final String selectedId,String message,final String toastMessage,final String choice){
		isDeleted = false;
		  AlertDialog.Builder builder=new AlertDialog.Builder(context);
	       		builder.setTitle("Confirm Delete");
	       		builder.setMessage(message);
	       		builder.setCancelable(true);
	       		builder.setPositiveButton("Yes", 
	               new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                   	 ContentResolver resolver=activity.getApplicationContext().getContentResolver();
	                   	 Cursor cursor=CursorClass.makeCursorBasedOnChoice(activity, Long.parseLong(selectedId), choice);	                   	 
	                     while(cursor.moveToNext()){
		        		   Long songId=cursor.getLong(0);


							 File file = new File(MainActivity.idToTrackObj.get(songId).getTrackPath());
							 boolean deleted = file.delete();
							 if (deleted)
	                   	   			resolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media._ID + " LIKE \"" + songId + "\"", null);

	    	        		  }
	                   	   Toast.makeText(activity, toastMessage,Toast.LENGTH_SHORT).show();
						   isDeleted = true;
	                          dialog.cancel();
	                   }
	               });
	               builder.setNegativeButton("No",
	                       new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       isDeleted = false;
						   dialog.cancel();
	                   }
	               });

	               AlertDialog alert11 = builder.create();
	               alert11.show();
	        	  

	}
	// returns the intent ..code to add to Playlist option for album,artist and Genre
	 public static Intent addSongstoPlaylist(Activity activity,final Long selectedId,String choice){
		   Intent intent=new Intent(activity,PlayListSelectionTest.class);
      // intent.putExtra("songName",songName);
	   
       ArrayList<String> audioIdList = new ArrayList<String>();
       Cursor cursor=CursorClass.makeCursorBasedOnChoice(activity,selectedId, choice);
	 //  ArrayList<Uri> uris=new ArrayList<Uri>();
	   while(cursor.moveToNext()){
		   Long id=cursor.getLong(0);
		   audioIdList.add(id.toString()); 
	   }
       intent.putExtra("selectedIdList", audioIdList);
       return intent;
      // startActivity(intent);
	}
	 
	 // Context menu option Add to playlist ..This is called when multiple cards are selected
	 public static Intent addMultipletoPlaylist(Activity activity,final ArrayList<String> selIdList,String choice){
		Intent intent=new Intent(activity,PlayListSelectionTest.class);
		int size=selIdList.size();
         // intent.putExtra("songName",songName);
  	    ArrayList<String> entireIdList=new ArrayList<String>(); // arrayList obtained by getting all the ids from the selected albums

  	    for(int i=0;i<size;i++)
  	    {
             Long selectedId=Long.parseLong(selIdList.get(i));
             Cursor cursor=CursorClass.makeCursorBasedOnChoice(activity,selectedId, choice);
              while(cursor.moveToNext()){
	    	     Long id=cursor.getLong(0);
	    	     entireIdList.add(id.toString());
             }
  	    }
           intent.putExtra("selectedIdList", entireIdList);
           return intent;
	 }
	 
	 
	 public static Intent shareMultiple(Activity activity,final ArrayList<String> selIdList,String choice){
		 
			Intent sharingIntent=new Intent(Intent.ACTION_SEND_MULTIPLE);
     	   sharingIntent.setType("audio/*");
     	//   String mimetypes[]={"audio/*"};
     	   ArrayList<Uri> uris=new ArrayList<Uri>();
     	   int size=selIdList.size();
     	 //  while(cursor.moveToNext()){
     		  for(int i=0;i<size;i++)
     		  {
     			  
     	       Long selectedId=Long.parseLong(selIdList.get(i));
     	      Cursor cursor=CursorClass.makeCursorBasedOnChoice(activity,selectedId, choice);
     	      int count=cursor.getCount();
     	       while(cursor.moveToNext()){
     	    	   Long id=cursor.getLong(0);
     		       String path=MainActivity.idToTrackObj.get(id).getTrackPath();
     		       path="file://"+path;
     		   
     		  // Toast.makeText(getContext(),"Hi there", Toast.LENGTH_LONG).show();
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
	        	 return chooser;
	         
	        	// startActivity(chooser);
	 }
	 
	 
	 public static Intent shareSingle(Activity activity,final Long selectedId,String choice){
		 Intent sharingIntent=new Intent(Intent.ACTION_SEND_MULTIPLE);
  	   sharingIntent.setType("audio/*");
  	//   String mimetypes[]={"audio/*"};
  	   
  	      
  	     
  	    
  	    Cursor cursor=CursorClass.makeCursorBasedOnChoice(activity,selectedId, choice);
  	   ArrayList<Uri> uris=new ArrayList<Uri>();
  	   while(cursor.moveToNext()){
  		   Long id=cursor.getLong(0);
  		   String path=MainActivity.idToTrackObj.get(id).getTrackPath();
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
      	 return chooser;
	 }
	
	 
	 
	  
	// function to add songs from an artist, an album,and Genre to a Playlist .For menu..
	
	public static void playSelectedSongs(Activity activity,ArrayList<String> selSongIdList,int playPosition,Boolean shuffle){
		
		Intent intent=new Intent(activity,PlayActivity.class);
 	intent.putExtra("do", "Play");
    intent.putExtra("position", playPosition);
    intent.putExtra("shuffle", shuffle);
     intent.putStringArrayListExtra("playingList", selSongIdList);
     activity.startActivity(intent);		
	}
	
	public static void playSongsNext (Activity activity,ArrayList<String> selSongIdList){
		int size = selSongIdList.size();
		for (int i=0;i<size;i++){
   		 songInfoObj.nowPlayingList.add(songInfoObj.currPlayPos+i+1,selSongIdList.get(i));
	    		songInfoObj.initialPlayingList.add(songInfoObj.currPlayPos+i+1,selSongIdList.get(i));
	    		// Shuffle may create a problem
   		}
		 Toast.makeText(activity, size +" song(s) will be played next", Toast.LENGTH_LONG).show();
	}
	public static void addToQueueSongs (Activity activity,ArrayList<String> selSongIdList){
		int size = selSongIdList.size();
		for (int i=0;i<size;i++){
      		 songInfoObj.nowPlayingList.add(selSongIdList.get(i));
		    		songInfoObj.initialPlayingList.add(selSongIdList.get(i));
		    		// Shuffle may create a problem
      		}
		   Toast.makeText(activity, size +" song(s) added to the Queue", Toast.LENGTH_LONG).show();
	}
	
	 public static void playSelectedSongsfromChoice(Activity activity,final Long selectedId,String choice,Boolean shuffle){
		
     ArrayList<String> audioIdList = getIdListfromChoice(activity, selectedId, choice);
	   playSelectedSongs(activity, audioIdList, 0, shuffle);
	   
    
	}
	 
	 public static void playSelectedSongfromMultiChoice(Activity activity,final ArrayList<String> selIdList,String choice,Boolean shuffle){
		 
		 
		 ArrayList<String> audioIdList = new ArrayList<String>();
			audioIdList = getIdListfromMultipleChoice(activity, selIdList, choice);
	  	playSelectedSongs(activity, audioIdList, 0, shuffle);
	           
		 }
	 public static ArrayList<String> getIdListfromMultipleChoice(Activity activity,final ArrayList<String> selIdList,String choice){
		 
		 ArrayList<String> audioIdList = new ArrayList<String>();
			int size=selIdList.size();
	         // intent.putExtra("songName",songName);
			
	        
	  	    for(int i=0;i<size;i++)
	  	    {
	             Long selectedId=Long.parseLong(selIdList.get(i));
	             Cursor cursor=CursorClass.makeCursorBasedOnChoice(activity,selectedId, choice);
	              while(cursor.moveToNext()){
	            	  String title =cursor.getString(0);
	       		   audioIdList.add(title); 
	             }
	  	    }
	  	    return audioIdList;
	 }
	 
 public static ArrayList<String> getIdListfromChoice(Activity activity,final Long selectedId,String choice){
	 ArrayList<String> audioIdList = new ArrayList<String>();
		 
	 Cursor cursor=CursorClass.makeCursorBasedOnChoice(activity, selectedId, choice);
	 //  ArrayList<Uri> uris=new ArrayList<Uri>();
	   while(cursor.moveToNext()){
		  String title =cursor.getString(0);
		   audioIdList.add(title); 
	   }
	  	    return audioIdList;
	 }
 
 public static void playSongsNextfromChoice(Activity activity,final Long selectedId,String choice){
		
     ArrayList<String> audioIdList = getIdListfromChoice(activity, selectedId, choice);
	   playSongsNext(activity, audioIdList);
	   
    
	}

 public static void playSongsNextfromMultiChoice(Activity activity,final ArrayList<String> selIdList,String choice){
		
     ArrayList<String> audioIdList = getIdListfromMultipleChoice(activity, selIdList, choice);
	   playSongsNext(activity, audioIdList);
	   
    
	}
 
 public static void addToQueuefromChoice(Activity activity,final Long selectedId,String choice){
		
     ArrayList<String> audioIdList = getIdListfromChoice(activity, selectedId, choice);
	   addToQueueSongs(activity, audioIdList);
	   
    
	}

 public static void AddToQueuefromMultiChoice(Activity activity,final ArrayList<String> selIdList,String choice){
		
     ArrayList<String> audioIdList = getIdListfromMultipleChoice(activity, selIdList, choice);
	   addToQueueSongs(activity, audioIdList);
	   
    
	}
 
 public static void changeSongInfo(final Activity activity,final Long audioId) {
  	  String[] projection1 = {
	              MediaStore.Audio.Media.TITLE,
	              MediaStore.Audio.Media.ALBUM,
	              MediaStore.Audio.Media.ARTIST
	           };
	       Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	       Cursor cursor =activity.getApplicationContext().getContentResolver().query(
	              uri,
	              projection1,
	              MediaStore.Audio.Media._ID+ " = "+audioId+"",
	              null,
	              null);
	       cursor.moveToNext();
	       String title = cursor.getString(0);
	       String album = cursor.getString(1);
	       String artist = cursor.getString(2);
   	   String GENRE_ID = MediaStore.Audio.Genres._ID;
          String GENRE_NAME = MediaStore.Audio.Genres.NAME;


       // Get a map from genre ids to names
       Uri GENRES_URI = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
       HashMap<String, String> genreIdMap = new HashMap<String, String>();
       Cursor c = activity.getApplicationContext().getContentResolver().query(
           GENRES_URI,
           new String[] { GENRE_ID, GENRE_NAME },
           null, null, null);
       for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
           genreIdMap.put(c.getString(0), c.getString(1));
       }
       String mGenre = "";
       
       for (String genreId : genreIdMap.keySet()) {
           c = activity.getApplicationContext().getContentResolver().query(
               makeGenreUri(genreId),
               new String[] { MediaStore.Audio.Media.DATA },
               MediaStore.Audio.Media._ID + " LIKE \"" + audioId + "\"",
               null, null);
           if (c.getCount() != 0) {
               mGenre = genreIdMap.get(genreId);
               mGenreId=Long.parseLong(genreId);
               break;
           }
           c = null;
       }
       
	// get prompts.xml view
		LayoutInflater layoutInflater = LayoutInflater.from(activity);
		View promptView = layoutInflater.inflate(R.layout.editinfo_dialog_box, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
		alertDialogBuilder.setView(promptView);
	//	Toast.makeText(activity,"Id is "+id,Toast.LENGTH_SHORT).show();

		final EditText editTitleText = (EditText) promptView.findViewById(R.id.edittext_title);
		editTitleText.setText(title);
		final EditText editAlbumText = (EditText) promptView.findViewById(R.id.edittext_album);
		editAlbumText.setText(album);
		final EditText editArtistText = (EditText) promptView.findViewById(R.id.edittext_artist);
		editArtistText.setText(artist);
		final EditText editGenreText = (EditText) promptView.findViewById(R.id.edittext_genre);
		editGenreText.setText(mGenre);
		//final EditText editAlbumText=(EditText) promptView.findViewById(R.id.edittext);
		// setup a dialog window
		alertDialogBuilder.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						
						String titleChange=editTitleText.getText().toString();
						String albumChange = editAlbumText.getText().toString();
						String artistChange = editArtistText.getText().toString();
						String genreChange = editGenreText.getText().toString();
						ContentValues mInserts = new ContentValues();
					    ContentResolver resolver=activity.getApplicationContext().getContentResolver();
						Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
						//String songName=idToName.get(id);
					//	Toast.makeText(activity,"songName  is "+songName,Toast.LENGTH_SHORT).show();

						mInserts.put(MediaStore.Audio.Media.TITLE, titleChange);
						mInserts.put(MediaStore.Audio.Media.ALBUM, albumChange);
						mInserts.put(MediaStore.Audio.Media.ARTIST, artistChange);
						
				       int rowsUpdated	=resolver.update(uri, mInserts,  MediaStore.Audio.Media._ID + " LIKE \"" + audioId + "\"", null);
				       Toast.makeText(activity,"No of Rows updated is "+rowsUpdated,Toast.LENGTH_SHORT).show();
				       Uri GenreUri=MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
				       ContentValues genreInserts=new ContentValues();
				       genreInserts.put(MediaStore.Audio.Genres.NAME, genreChange);
				       rowsUpdated=activity.getApplicationContext().getContentResolver().update(GenreUri,genreInserts,MediaStore.Audio.Genres._ID+ " LIKE \"" + mGenreId + "\"", null);
				      Toast.makeText(activity,"No of Rows updated for Genre is "+rowsUpdated,Toast.LENGTH_SHORT).show();
				       //Should write code to scan the mediaPlayer again

						
						 
			         //	 mInserts.get(id) ;
			        	 
						
					  //  createNewPlayList(editText.getText().toString());
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
	// TODO Auto-generated method stub
	
}


	public static void editAlbumInfo(final Long selectedAlbumId,String selectedAlbumName,final Activity activity) {

		LayoutInflater layoutInflater = LayoutInflater.from(activity);
		View promptView = layoutInflater.inflate(R.layout.dialog_box, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
		alertDialogBuilder.setView(promptView);
		//	Toast.makeText(activity,"Id is "+id,Toast.LENGTH_SHORT).show();
		TextView textView = (TextView) promptView.findViewById(R.id.textView);
		textView.setText("Edit Album Name");
		final EditText editTitleText = (EditText) promptView.findViewById(R.id.edittext);
		editTitleText.setText(selectedAlbumName);

		//final EditText editAlbumText=(EditText) promptView.findViewById(R.id.edittext);
		// setup a dialog window
		alertDialogBuilder.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						String albumChange = editTitleText.getText().toString();
						ContentValues mInserts = new ContentValues();
						ContentResolver resolver = activity.getApplicationContext().getContentResolver();
						Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
						//String songName=idToName.get(id);
						//	Toast.makeText(activity,"songName  is "+songName,Toast.LENGTH_SHORT).show();

						mInserts.put(MediaStore.Audio.Media.ALBUM, albumChange);
						int rowsUpdated = resolver.update(uri, mInserts, MediaStore.Audio.Media.ALBUM_ID + " LIKE \"" + selectedAlbumId + "\"", null);
						Toast.makeText(activity, "No of Rows updated is " + rowsUpdated, Toast.LENGTH_SHORT).show();

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



/*
		LayoutInflater layoutInflater = LayoutInflater.from(activity);
		View promptView = layoutInflater.inflate(R.layout.editinfo_dialog_box, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
		alertDialogBuilder.setView(promptView);
		//	Toast.makeText(activity,"Id is "+id,Toast.LENGTH_SHORT).show();

		final EditText editAlbumText = (EditText) promptView.findViewById(R.id.edittext_album);
		editAlbumText.setText(selectedAlbumName);
		alertDialogBuilder.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {


						String albumChange = editAlbumText.getText().toString();
						ContentValues mInserts = new ContentValues();
						ContentResolver resolver = activity.getApplicationContext().getContentResolver();
						Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
						//String songName=idToName.get(id);
						//	Toast.makeText(activity,"songName  is "+songName,Toast.LENGTH_SHORT).show();

						mInserts.put(MediaStore.Audio.Media.ALBUM, albumChange);
						int rowsUpdated = resolver.update(uri, mInserts, MediaStore.Audio.Media.ALBUM_ID + " LIKE \"" + selectedAlbumId + "\"", null);
						Toast.makeText(activity, "No of Rows updated is " + rowsUpdated, Toast.LENGTH_SHORT).show();

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
		alert.show();*/
	}

	public static void editArtistInfo(final Long selectedArtistId,String selectedArtistName,final Activity activity) {

		LayoutInflater layoutInflater = LayoutInflater.from(activity);
		View promptView = layoutInflater.inflate(R.layout.dialog_box, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
		alertDialogBuilder.setView(promptView);
		//	Toast.makeText(activity,"Id is "+id,Toast.LENGTH_SHORT).show();
		TextView textView = (TextView) promptView.findViewById(R.id.textView);
		textView.setText("Edit Artist Name");
		final EditText editTitleText = (EditText) promptView.findViewById(R.id.edittext);
		editTitleText.setText(selectedArtistName);

		//final EditText editAlbumText=(EditText) promptView.findViewById(R.id.edittext);
		// setup a dialog window
		alertDialogBuilder.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						String artistChange = editTitleText.getText().toString();
						ContentValues mInserts = new ContentValues();
						ContentResolver resolver = activity.getApplicationContext().getContentResolver();
						Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
						//String songName=idToName.get(id);
						//	Toast.makeText(activity,"songName  is "+songName,Toast.LENGTH_SHORT).show();

						mInserts.put(MediaStore.Audio.Media.ARTIST, artistChange);
						int rowsUpdated = resolver.update(uri, mInserts, MediaStore.Audio.Media.ARTIST_ID + " LIKE \"" + selectedArtistId + "\"", null);
						Toast.makeText(activity, "No of Rows updated is " + rowsUpdated, Toast.LENGTH_SHORT).show();

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

	}

	public static void editGenreInfo(final Long GenreId,String GenreName,final Activity activity) {

		LayoutInflater layoutInflater = LayoutInflater.from(activity);
		View promptView = layoutInflater.inflate(R.layout.dialog_box, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
		alertDialogBuilder.setView(promptView);
		//	Toast.makeText(activity,"Id is "+id,Toast.LENGTH_SHORT).show();
		TextView textView = (TextView) promptView.findViewById(R.id.textView);
		textView.setText("Edit Genre Name");
		final EditText editTitleText = (EditText) promptView.findViewById(R.id.edittext);
		editTitleText.setText(GenreName);

		//final EditText editAlbumText=(EditText) promptView.findViewById(R.id.edittext);
		// setup a dialog window
		alertDialogBuilder.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						String genreChange = editTitleText.getText().toString();

						Uri GenreUri=MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
						ContentValues genreInserts=new ContentValues();
						genreInserts.put(MediaStore.Audio.Genres.NAME, genreChange);
						int rowsUpdated=activity.getApplicationContext().getContentResolver().update(GenreUri,genreInserts,MediaStore.Audio.Genres._ID+ " LIKE \"" + GenreId + "\"", null);
						Toast.makeText(activity,"No of Rows updated for Genre is "+rowsUpdated,Toast.LENGTH_SHORT).show();


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

	}




	private static Uri makeGenreUri(String genreId) {
	 Uri GENRES_URI = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
    String CONTENTDIR = MediaStore.Audio.Genres.Members.CONTENT_DIRECTORY;
    return Uri.parse(
        new StringBuilder()
        .append(GENRES_URI.toString())
        .append("/")
        .append(genreId)
        .append("/")
        .append(CONTENTDIR)
        .toString());
}
 
 public static void deletePlaylistTracks(Context context, long playlistId,
	        long audioId) {
	    ContentResolver resolver = context.getContentResolver();
	    int countDel = 0;
	    try {
	        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri(
	                "external", playlistId);
	        String where = MediaStore.Audio.Playlists.Members.AUDIO_ID + "=?" ; // my mistake was I used .AUDIO_ID here

	        String audioId1 = Long.toString(audioId);
	        String[] whereVal = { audioId1 };
	        countDel=resolver.delete(uri, where,whereVal);      
	        Log.d("TAG", "tracks deleted=" + countDel);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	 }
 public static long getPlaylist(ContentResolver resolver, String name)
 {
 long id = -1;
 Cursor cursor = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
 new String[] { MediaStore.Audio.Playlists._ID },
 MediaStore.Audio.Playlists.NAME + "=?",
 new String[] { name }, null);
 if (cursor != null) {
 if (cursor.moveToNext())
 id = cursor.getLong(0);
 cursor.close();
 }
 return id;
 }
 
 public static void deletePlaylistEX(ContentResolver resolver, long id)
 {
 Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, id);
 resolver.delete(uri, null, null);
 }
 public static void renamePlaylist(ContentResolver resolver, long id, String newName)
 {
 long existingId = getPlaylist(resolver, newName);
 // We are already called the requested name; nothing to do.
 if (existingId == id)
 return;
 // There is already a playlist with this name. Kill it.
 if (existingId != -1)
 deletePlaylist(resolver, existingId);
 ContentValues values = new ContentValues(1);
 values.put(MediaStore.Audio.Playlists.NAME, newName);
 resolver.update(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values, "_id=" + id, null);
 }
 public static void savePreference(Context context){
	 TinyDB tinyDB = new TinyDB(context);
	/*  SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
	    SharedPreferences.tinyDB tinyDB = sharedPreferences.edit();
	    Set<String> nowPlayingSet = new LinkedHashSet<String>();
	    Set<String> initialNowPlayingSet = new LinkedHashSet<String>();
	    nowPlayingSet.addAll(songInfoObj.nowPlayingList);
	    initialNowPlayingSet.addAll(songInfoObj.initialPlayingList);
	    tinyDB.putStringSet("nowPlayingList",nowPlayingSet);
	    tinyDB.putStringSet("initalNowPlayingList",initialNowPlayingSet);
	    */
	 	tinyDB.putListString("nowPlayingList", songInfoObj.nowPlayingList);
	 	tinyDB.putListString("initialNowPlayingList", songInfoObj.initialPlayingList);
	    tinyDB.putInt("isRepeat", songInfoObj.isRepeat);
	    tinyDB.putBoolean("shuffled",songInfoObj.shuffled);
	    tinyDB.putInt("currPlayPos",songInfoObj.currPlayPos);
	    tinyDB.putLong("songId", songInfoObj.songId);
	    tinyDB.putInt("playerPosition", PlayHelperFunctions.mp.getCurrentPosition());

	   Log.i("shared Preference", "Saved Preference Success");
	   
 }
 
 public static void loadPreference(Context context){
	 TinyDB tinyDB = new TinyDB(context);
	 /*
	 SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

	 Set<String> nowPlayingSet = new LinkedHashSet<String>();
	Set<String> empSet = new LinkedHashSet<String>();
	 nowPlayingSet = sharedPreferences.getStringSet("nowPlayingList", empSet);
	 for(String s : nowPlayingSet){
		 songInfoObj.nowPlayingList.add(s);
	 }
	 Set<String> initialNowPlayingSet = new HashSet<String>();
	 initialNowPlayingSet = sharedPreferences.getStringSet("initialNowPlayingList", empSet);
	 for(String s:initialNowPlayingSet){
		 songInfoObj.initialPlayingList.add(s);
	 }
	 */
	 songInfoObj.isRepeat=tinyDB.getInt("isRepeat", 0);
	 songInfoObj.shuffled=tinyDB.getBoolean("shuffled", false);
	 songInfoObj.currPlayPos=tinyDB.getInt("currPlayPos", 0);
	 songInfoObj.songId=tinyDB.getLong("songId",0L);
	 songInfoObj.playerPostion=tinyDB.getInt("playerPosition", 0);
	 songInfoObj.initialPlayingList = tinyDB.getListString("initialNowPlayingList");
	 songInfoObj.nowPlayingList = tinyDB.getListString("nowPlayingList");
	 Log.i("shared Preference", "Load Preference Success");
 }

	public static void addToPlaylist(ContentResolver resolver, Long audioId,Long playlistID) {

		String[] cols = new String[] {
				"count(*)"
		};
		Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistID);
		Cursor cur = resolver.query(uri, cols, null, null, null);
		cur.moveToLast();
		final int base = cur.getInt(0);
		cur.close();
		ContentValues values = new ContentValues();
		values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, Long.valueOf(base));
		values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audioId);
		resolver.insert(uri, values);
	}

	static public  void createNewPlayList(String name,Activity act)
	{
		ContentValues mInserts = new ContentValues();
		mInserts.put(MediaStore.Audio.Playlists.NAME, name);
		mInserts.put(MediaStore.Audio.Playlists.DATE_ADDED, System.currentTimeMillis());
		mInserts.put(MediaStore.Audio.Playlists.DATE_MODIFIED, System.currentTimeMillis());
		ContentResolver mContentResolver=act.getApplicationContext().getContentResolver();
		Cursor cursor=null;
		Uri mUri = mContentResolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, mInserts);
		final String[] PROJECTION_PLAYLIST = new String[] {
				MediaStore.Audio.Playlists._ID,
				MediaStore.Audio.Playlists.NAME,
				MediaStore.Audio.Playlists.DATA
		};
		int mPlaylistId;
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
			//   playlistnames.add(name);
			//    playlistIdLists.add((long) mPlaylistId);

		}
	}

	public static void deletePlaylist(ContentResolver resolver, long id)
	{
		Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, id);
		resolver.delete(uri, null, null);
	}
	public static enum SupportedFileFormat
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
	public static boolean checkExtension( String fileName ) {
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

	public static String getFileExtension( String fileName ) {
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			return fileName.substring(i+1);
		} else
			return null;
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

}


