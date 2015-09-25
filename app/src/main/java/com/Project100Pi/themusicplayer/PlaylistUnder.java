package com.Project100Pi.themusicplayer;

import it.gmariotti.cardslib.library.extra.dragdroplist.internal.CardDragDropArrayAdapter;
import it.gmariotti.cardslib.library.extra.dragdroplist.view.CardListDragDropView;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener;

import java.util.ArrayList;

import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.OnItemMovedListener;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class PlaylistUnder extends Activity {

	int playlistLength;
	Long id = 0L;
	CardDragDropArrayAdapter mCardArrayAdapter = null;
	 ArrayList<Card> cards=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final ArrayList<Card> cards = new ArrayList<Card>();
		final ArrayList<String> idList = new ArrayList<String>();
		final ArrayList<String> pathList = new ArrayList<String>();
		Long thisId;
		String thisPath;
		setContentView(R.layout.activity_playlist_under);
		Intent intent = getIntent();
		String X = intent.getStringExtra("X");
		 id = intent.getLongExtra("id",0L);
		String XName="";
		String audioId = "";
		String playOrder,sortOrder;
		Cursor cursor = null;
		final ArrayList<String> XList = new ArrayList<String>();
		if(X.equals("Album")){
		 cursor = makeAlbumSongCursor(id);
		}else if(X.equals("Artist")){
			 cursor = makeArtistSongCursor(id);
		}else if(X.equals("Genre")){
			
			 String[] proj2={MediaStore.Audio.Media.TITLE};
			 Uri uri = MediaStore.Audio.Genres.Members.getContentUri("external", id);
			 cursor = getContentResolver().query(uri, proj2, null,null,null);
		}else if(X.equals("PlayList")){

		       String[] projection1 = {
		              MediaStore.Audio.Playlists.Members.TITLE,
		              MediaStore.Audio.Playlists.Members.AUDIO_ID,
		              MediaStore.Audio.Playlists.Members.PLAY_ORDER,
		              MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER,
		              MediaStore.Audio.Playlists.Members.DATA
		           };
		       Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", id);
		           cursor =PlaylistUnder.this.getApplicationContext().getContentResolver().query(
		              uri,
		              projection1,
		              MediaStore.Audio.Playlists.Members.PLAYLIST_ID+ " = "+id+"",
		              null,
		              MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER );
		           
		       
		}
		if(!X.equals("Genre")){
	
		 while (cursor.moveToNext()) {
			 if(X.equals("PlayList")){
				 XName = cursor.getString(0);
				 audioId = cursor.getString(1);
				 playOrder=cursor.getString(2);
				 sortOrder = cursor.getString(3);
				 thisPath = cursor.getString(4);
				idList.add(audioId);
				pathList.add(thisPath);
			 }else{
	            XName = cursor.getString(1); 
			 }
	            XList.add(XName);
	            
	            Card card = new Card(this);

	            //Create a CardHeader
	            CardHeader header = new CardHeader(this);
	      
	            //Add Header to card
	            card.addCardHeader(header);
	            card.setId(audioId);
	      
	            header.setTitle(XName);
	            card.setInnerLayout(R.layout.track_layout);
	            card.setOnClickListener(new OnCardClickListener() {
					
					@Override
					public void onClick(Card card, View view) {
						// TODO Auto-generated method stub
						
						UtilFunctions.playSelectedSongs(PlaylistUnder.this, idList, cards.indexOf(card), false);
					}
				});
	          	            cards.add(card);
	          //  path.put(title,fullpath);
	           
	        }
		} else{
			if(cursor.moveToFirst())
	          {
	              do{
	                  
	                   XName=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
	                   XList.add(XName);
	   	            Card card = new Card(this);

	   	            //Create a CardHeader
	   	            CardHeader header = new CardHeader(this);
	   	      
	   	            //Add Header to card
	   	            card.addCardHeader(header);
	   	            header.setTitle(XName);
	   	         card.setOnClickListener(new OnCardClickListener() {
						
						@Override
						public void onClick(Card card, View view) {
							// TODO Auto-generated method stub
							
							 Intent intent=new Intent(PlaylistUnder.this,PlayActivity.class);
							 intent.putExtra("do", "Play");
					           intent.putExtra("position", cards.indexOf(card));
					            intent.putStringArrayListExtra("playingList", idList);
					            startActivity(intent);   
						}
					});
	   	          	            cards.add(card);

	              }while(cursor.moveToNext());
	          }
		}
		mCardArrayAdapter = new CardDragDropArrayAdapter(this, cards){
			 @Override
			 public View getView( final int position, View convertView, final ViewGroup parent) {
	    		
	    		
					
				    View v =super.getView(position, convertView, parent);
				    Long id = Long.parseLong(cards.get(position).getId());
				    songInfo thisSong = FirstFragment.idToSongInfo.get((id));
			    	//TextView albumTitle = (TextView) v.findViewById(R.id.album_title);
			    	//albumTitle.setText(thisSong.albumName);
			    	TextView artistTitle = (TextView) v.findViewById(R.id.artist_title);
			    	artistTitle.setText(thisSong.artistName);
			    	TextView durSong = (TextView) v.findViewById(R.id.artist_noOfSongs);
			    	durSong.setText(thisSong.songDuration);

				    if( convertView != null )
				        v = convertView;
				    else{
				    
				    	
				    final ImageView button = (ImageView)v.findViewById(R.id.overflow_button);
				    
				   //t1.setTag(position);
				    button.setTag(position);
				//    Toast.makeText(getActivity()," position is " + position, Toast.LENGTH_LONG).show();
		            
				    
				   
				    button.setOnClickListener(new OnClickListener() {
		                public void onClick(View v) {
		                	
		                	PopupMenu popupMenu = new PopupMenu(getContext(),v); 
		            	    popupMenu.inflate(R.menu.playlist_options);
		            	    View parentRow=(View) v.getParent();
				            CardListDragDropView myListView=(CardListDragDropView) parentRow.getParent().getParent();
				            final int currPosition=myListView.getPositionForView(parentRow);
				            final String songName= cards.get(currPosition).getCardHeader().getTitle();
				             final Long selectedId=Long.parseLong(cards.get(currPosition).getId());
				             final ArrayList<String> selectedIdList=new ArrayList<String>();
				             selectedIdList.add(selectedId.toString()); // created to send the ArrayList to UtilFunctions.deletePopUp
				             final ArrayList<String> selSongNameList = new ArrayList<String>();
				             selSongNameList.add(songName);
				            Toast.makeText(PlaylistUnder.this, songName +"and position is" + currPosition, Toast.LENGTH_LONG).show();
				            
		            	    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
		            			
		            			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
								@Override
		            			public boolean onMenuItemClick(MenuItem item) {
									
		            			             	
		            			       switch(item.getItemId()){
		            			       
		            			       case R.id.playlist_play:
		            			    	  UtilFunctions.playSelectedSongs(PlaylistUnder.this, idList,currPosition,false);
		            			    	   break;
		            			       case R.id.playlist_play_next:
		            			    	   UtilFunctions.playSongsNext(PlaylistUnder.this,selectedIdList);
		            			    	   break;
		            			       case R.id.playlist_add_to_queue:
		            			    	   UtilFunctions.addToQueueSongs(PlaylistUnder.this, selectedIdList);
		            			    	   break;
		            			       case R.id.playlist_edit:
		            			    	   UtilFunctions.changeSongInfo(PlaylistUnder.this, selectedId);
		            			    	   break;
		            			       case R.id.playlist_add_playlist:
		            			    	   Intent intent=new Intent(PlaylistUnder.this,PlaylistSelection.class);
		            			            intent.putExtra("songName",songName);
		            			            intent.putExtra("selectedId",selectedId);
		            			            startActivity(intent);
		            			    	   break;
		            			       case R.id.playlist_share:
		            			    	   Intent sharingIntent=new Intent(Intent.ACTION_SEND);
	            			        	   String path=FirstFragment.pathList.get(currPosition);
	            			        	   path="file://"+path;
	            			        	   Toast.makeText(PlaylistUnder.this,"Path is "+path,Toast.LENGTH_SHORT).show();
		            			        	 Uri uri=Uri.parse(path);
		            			        	 sharingIntent.setType("audio/*");
		            			        	 sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
		            			        	 sharingIntent.putExtra(Intent.EXTRA_TITLE, songName);
		            			        	 startActivity(Intent.createChooser(sharingIntent, "Share Using"));
		            			    	   break;
		            			       case R.id.playlist_delete:
		            			    	   
		            			    	   UtilFunctions.deletePopUp(getContext(),PlaylistUnder.this, selectedIdList, "Are you sure you want to delete the selected song?","Song deleted");
		            			    	   break;
		            			       case R.id.playlist_remove_from_playlist:
		            			    	   UtilFunctions.deletePlaylistTracks(PlaylistUnder.this, PlaylistUnder.this.id, selectedId);
		            			    	   if(songInfoObj.currPlayPos == currPosition){
		            			    		   PlayHelperFunctions.mp.reset();
		            			    	   }
		            			    	   Toast.makeText(PlaylistUnder.this,songName +" is removed from this PlayList",Toast.LENGTH_SHORT).show();
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
		};
		mCardArrayAdapter.setEnableDragSupportOnLongClickOnCard(true);

		   CardListDragDropView mListView = (CardListDragDropView) this.findViewById(R.id.songsUnderPlayList);
		   mListView.setFastScrollEnabled(true);
		   if (mListView != null) {
		           mListView.setAdapter(mCardArrayAdapter);
		           
		   }
	
		   playlistLength=XList.size();

		   mListView.setOnItemMovedListener(new OnItemMovedListener() {
			      @Override
			      public void onItemMoved(int originalPosition, int newPosition) {
			          Card card = mCardArrayAdapter.getItem(newPosition);
			          String songName=XList.get(originalPosition);
			         long audioId=Long.parseLong(card.getId());
			        //  Toast.makeText(PlaylistUnder.this,"Card "+card.getId() + " moved to position " + newPosition, Toast.LENGTH_SHORT ).show();
			         // songInfoObj.nowPlayingList.clear();
			          // cards.clear();
			         /*   XList.clear();
			         
			          for(int i=0;i<playlistLength;i++)
			          {
			        	  Card tempcard=mCardArrayAdapter.getItem(i);
			        	  XList.add(tempcard.getTitle());
			        	  
			         }
			         */
			          String[] cols = new String[] {
			                  "count(*)"
			          };
			         
			          ContentResolver resolver=getContentResolver();
			          boolean result= MediaStore.Audio.Playlists.Members.moveItem(resolver, id,originalPosition,newPosition);
			          Toast.makeText(getApplicationContext(), "result is " +result, Toast.LENGTH_SHORT).show();
			          
			         /* 
			          Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external",id);
			          Cursor cur = resolver.query(uri, cols, null, null, null);
			          int i=0;
			        while(cur.moveToNext()){
			         
			          ContentValues values = new ContentValues();
			          values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, newPosition);
			          values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audioId);
			         // resolver.delete(uri,  MediaStore.Audio.Playlists.Members.AUDIO_ID + " LIKE \"" + audioId+ "\"", null);
			         int val= resolver.update(uri, values,  MediaStore.Audio.Playlists.Members.AUDIO_ID + " LIKE \"" + audioId+ "\"", null);
			         Toast.makeText(getApplicationContext(),"rows updated is "+val, Toast.LENGTH_SHORT).show();
			         int x;
			         
			         }
			      */
			      }
			   });
	     
		
	}

	
	private final Cursor makeAlbumSongCursor(final Long albumId) {
		final StringBuilder selection = new StringBuilder();
		selection.append(AudioColumns.IS_MUSIC + "=1");
		selection.append(" AND " + AudioColumns.TITLE + " != ''");
		selection.append(" AND " + AudioColumns.ALBUM_ID + "=" + albumId);
		return getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
		new String[] {
		/* 0 */ BaseColumns._ID,
		/* 1 */ AudioColumns.TITLE,
		/* 2 */ AudioColumns.ALBUM,
		/* 3 */ AudioColumns.ALBUM
		}, selection.toString(), null, MediaStore.Audio.Media.TRACK + ", " + MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		}
	
	private final Cursor makeArtistSongCursor(final Long artistId) {
		final StringBuilder selection = new StringBuilder();
		selection.append(AudioColumns.IS_MUSIC + "=1");
		selection.append(" AND " + AudioColumns.TITLE + " != ''");
		selection.append(" AND " + AudioColumns.ARTIST_ID + "=" + artistId);
		return getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
		new String[] {
		/* 0 */ BaseColumns._ID,
		/* 1 */ AudioColumns.TITLE,
		/* 2 */ AudioColumns.ARTIST,
		/* 3 */ AudioColumns.ARTIST
		}, selection.toString(), null, MediaStore.Audio.Media.TRACK + ", " + MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		}
	
	private final Cursor makeGenreSongCursor(final Long genreId) {
		final StringBuilder selection = new StringBuilder();
		selection.append(AudioColumns.IS_MUSIC + "=1");
		selection.append(" AND " + AudioColumns.TITLE + " != ''");
		selection.append(" AND " + BaseColumns._ID + "=" + genreId);
		return getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
		new String[] {
		/* 0 */ BaseColumns._ID,
		/* 1 */ AudioColumns.TITLE
		}, selection.toString(), null, MediaStore.Audio.Media.TRACK + ", " + MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.songs_under, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
