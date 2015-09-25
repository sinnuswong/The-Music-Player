package com.Project100Pi.themusicplayer;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;


public class CursorClass {

	public static Context mContext;
	
	public static Cursor playSongCursor(Long id){
		 String projection[] = { MediaStore.Audio.Media.DATA,
					MediaStore.Audio.Media.ARTIST,
					MediaStore.Audio.Media.ALBUM,
					MediaStore.Audio.Media.ALBUM_ID,
					MediaStore.Audio.Media.DURATION,
					MediaStore.Audio.Media.TITLE,
				 	MediaStore.Audio.Media._ID
      };

		 Cursor cursor = mContext.getContentResolver().query(
	        	    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
	        	    projection, 
	        	    MediaStore.Audio.Media._ID + " LIKE \"" + id + "\"", 
	        	    null, 
	        	    null);
		 return cursor;
	 }
	
	public static Bitmap albumArtCursor(Long albumId){
		
		final Uri ART_CONTENT_URI = Uri.parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(ART_CONTENT_URI, albumId);

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), albumArtUri);
           
        } catch (Exception exception) {
            // log error
        }
        return bitmap;
	}
	
	
	  public static  Cursor makeAlbumSongCursor(Activity activity,final Long albumId) {
			final StringBuilder selection = new StringBuilder();
			selection.append(AudioColumns.IS_MUSIC + "=1");
			selection.append(" AND " + AudioColumns.TITLE + " != ''");
			selection.append(" AND " + AudioColumns.ALBUM_ID + "=" + albumId);
			return activity.getApplicationContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
			new String[] {
			/* 0 */ BaseColumns._ID,
			/* 1 */ AudioColumns.TITLE,
			/* 2 */ AudioColumns.ALBUM,
			/* 3 */ AudioColumns.ALBUM
			}, selection.toString(), null, MediaStore.Audio.Media.TRACK + ", " + MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
	  }
	  
	  public static Cursor makeArtistSongCursor(Activity activity,final Long artistId) {
			final StringBuilder selection = new StringBuilder();
			selection.append(AudioColumns.IS_MUSIC + "=1");
			selection.append(" AND " + AudioColumns.TITLE + " != ''");
			selection.append(" AND " + AudioColumns.ARTIST_ID + "=" + artistId);
			return activity.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
			new String[] {
			/* 0 */ BaseColumns._ID,
			/* 1 */ AudioColumns.TITLE,
			/* 2 */ AudioColumns.ARTIST,
			/* 3 */ AudioColumns.ARTIST
			}, selection.toString(), null, MediaStore.Audio.Media.TRACK + ", " + MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
	  }
	  
	  public static Cursor makeGenreSongCursor(Activity activity,final Long GenreId){
		  String[] proj2={MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE};
	      Uri uri = MediaStore.Audio.Genres.Members.getContentUri("external",GenreId);
	      Cursor cursor = activity.getApplicationContext().getContentResolver().query(uri, proj2, null,null,null);
	      return cursor;
	  }
	  

        public static  Cursor makePlaylistSongCursor(Activity activity,final Long id){
	          String[] projection1 = {
		      MediaStore.Audio.Playlists.Members.AUDIO_ID,
              MediaStore.Audio.Playlists.Members.TITLE,
              MediaStore.Audio.Playlists.Members.PLAY_ORDER,
              MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER
              
           };
              Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", id);
              Cursor cursor =activity.getApplicationContext().getContentResolver().query(
              uri,
              projection1,
              MediaStore.Audio.Playlists.Members.PLAYLIST_ID+ " = "+id+"",
              null,
              MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER );
             
               return cursor;
     }
        
	  public static Cursor makeCursorBasedOnChoice(Activity activity,final Long selectedId,String choice){
		  Cursor cursor=null;
          if(choice.equals("artist")){
      	      cursor=CursorClass.makeArtistSongCursor(activity,selectedId);
      	   }else if(choice.equals("album")){
      		  cursor=CursorClass.makeAlbumSongCursor(activity,selectedId);
      	   }else if(choice.equals("genre")){
      		  cursor=CursorClass.makeGenreSongCursor(activity,selectedId);
      	   }else if(choice.equals("playlist")){
      		  cursor=CursorClass.makePlaylistSongCursor(activity,selectedId);
      	   }
          
          return cursor;
	  }
	
}          
