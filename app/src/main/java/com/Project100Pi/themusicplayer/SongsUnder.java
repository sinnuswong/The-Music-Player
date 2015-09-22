package com.Project100Pi.themusicplayer;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayMultiChoiceAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.base.CardViewWrapper;

import java.util.ArrayList;
import java.util.Collections;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class SongsUnder extends Activity {

	CardArrayMultiChoiceAdapter mCardArrayAdapter = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final ArrayList<Card> cards = new ArrayList<Card>();
		final ArrayList<String> idList = new ArrayList<String>();
		final ArrayList<String> pathList = new ArrayList<String>();
		Long thisId;
		String thisPath;
		setContentView(R.layout.songs_under);
		Intent intent = getIntent();
		String X = intent.getStringExtra("X");
		Long id = intent.getLongExtra("id",0L);
		String XName="";
		Cursor cursor = null;
		final ArrayList<String> XList = new ArrayList<String>();
		if(X.equals("Album")){
		 cursor = makeAlbumSongCursor(id);
		}else if(X.equals("Artist")){
			 cursor = makeArtistSongCursor(id);
		}else if(X.equals("Genre")){
			
			 String[] proj2={MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE};
			 Uri uri = MediaStore.Audio.Genres.Members.getContentUri("external", id);
			 cursor = getContentResolver().query(uri, proj2, null,null,null);
		}else if(X.equals("PlayList")){

		       String[] projection1 = {
		              MediaStore.Audio.Playlists.Members.TITLE
		           };
		       Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", id);
		           cursor =SongsUnder.this.getApplicationContext().getContentResolver().query(
		              uri,
		              projection1,
		              MediaStore.Audio.Playlists.Members.PLAYLIST_ID+ " = "+id+"",
		              null,
		              null);
		           
		       
		}
		if(!X.equals("Genre")){
		 while (cursor.moveToNext()) {
			 if(X.equals("PlayList")){
				 XName = cursor.getString(0);
			 }else{
	            XName = cursor.getString(1); 
	            thisId = cursor.getLong(0);
	            thisPath = FirstFragment.idToPath.get(thisId);
	            idList.add(thisId.toString());
	            pathList.add(thisPath);
			 }
	            XList.add(XName);
	            Card card = new Card(this);

	            //Create a CardHeader
	            CardHeader header = new CardHeader(this);
	      
	            //Add Header to card
	            card.addCardHeader(header);
	            header.setTitle(XName);
	            card.setInnerLayout(R.layout.track_layout);
	            card.setOnClickListener(new OnCardClickListener() {
					
					@Override
					public void onClick(Card card, View view) {
						// TODO Auto-generated method stub
						
						UtilFunctions.playSelectedSongs(SongsUnder.this, idList, cards.indexOf(card), false);
					}
				});
	            card.setOnLongClickListener(new Card.OnLongCardClickListener() {
	                @Override
	                public boolean onLongClick(Card card, View view) {
	                    return mCardArrayAdapter.startActionMode(SongsUnder.this);

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
	                   thisId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
	   	            thisPath = FirstFragment.idToPath.get(thisId);
	   	            idList.add(thisId.toString());
	   	            pathList.add(thisPath);
	                   XList.add(XName);
	   	            Card card = new Card(this);

	   	            //Create a CardHeader
	   	            CardHeader header = new CardHeader(this);
	   	      
	   	            //Add Header to card
	   	            card.addCardHeader(header);
	   	            header.setTitle(XName);
	   	         card.setInnerLayout(R.layout.track_layout);
	   	         card.setOnClickListener(new OnCardClickListener() {
						
						@Override
						public void onClick(Card card, View view) {
							// TODO Auto-generated method stub
							UtilFunctions.playSelectedSongs(SongsUnder.this, idList, cards.indexOf(card), false); 
						}
					});
	   	      card.setOnLongClickListener(new Card.OnLongCardClickListener() {
	              @Override
	              public boolean onLongClick(Card card, View view) {
	                  return mCardArrayAdapter.startActionMode(SongsUnder.this);

	              }
	         });
	   	          	            cards.add(card);

	              }while(cursor.moveToNext());
	          }
		}
		
		mCardArrayAdapter = new CardArrayMultiChoiceAdapter(SongsUnder.this,cards){
		    
	    	@Override
			 public View getView( final int position, View convertView, final ViewGroup parent) {
	    		
	    		
					
				    View v =super.getView(position, convertView, parent);
				    Long id = Long.parseLong(idList.get(position));
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
				    	//LayoutInflater inflater=getLayoutInflater();
				    	//v=inflater.inflate(R.layout.my_two_lines, parent, false);
				    	//TextView t1 = (TextView) v.findViewById(R.id.line_app);
				    
				    	//CardListView myListView=(CardListView)v.getParent();
				          // final int currPos=myListView.getPositionForView(v);
				            //final Long selectedId=idList.get(currPos);
				    	
				    final ImageView button = (ImageView)v.findViewById(R.id.overflow_button);
				    
				   //t1.setTag(position);
				    button.setTag(position);
				//    Toast.makeText(SongsUnder.this," position is " + position, Toast.LENGTH_LONG).show();
		            
				    
				   
				    button.setOnClickListener(new OnClickListener() {
		                public void onClick(View v) {
		                	
		                	PopupMenu popupMenu = new PopupMenu(getContext(),v); 
		            	    popupMenu.inflate(R.menu.long_click_actions);
		            	    View parentRow=(View) v.getParent();
				            CardListView myListView=(CardListView) parentRow.getParent().getParent();
				            final int currPosition=myListView.getPositionForView(parentRow);
				            final String songName=XList.get(currPosition);
				             final Long selectedId=Long.parseLong(idList.get(currPosition));
				             final ArrayList<String> selectedIdList=new ArrayList<String>();
				             selectedIdList.add(selectedId.toString()); // created to send the ArrayList to UtilFunctions.deletePopUp
				             final ArrayList<String> selSongNameList = new ArrayList<String>();
				             selSongNameList.add(songName);
				            Toast.makeText(SongsUnder.this, songName +"and position is" + currPosition, Toast.LENGTH_LONG).show();
				            
		            	    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
		            			
		            			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
								@Override
		            			public boolean onMenuItemClick(MenuItem item) {
									
		            			             	
		            			       switch(item.getItemId()){
		            			       case R.id.cnt_menu_play:
		            			    	   
		            			    	  UtilFunctions.playSelectedSongs(SongsUnder.this,idList,currPosition,false);
		            			    	   
		            			    	   break;
		            			       case R.id.cnt_menu_play_next:
		            			    	  
		            			    	UtilFunctions.playSongsNext(SongsUnder.this,selectedIdList);
		            			    	   
		            			    	   break;
		            			    	   
		            			       case R.id.cnt_menu_add_queue:
		            			    		UtilFunctions.addToQueueSongs(SongsUnder.this, selectedIdList);
		            			    	   break;
		            			       case R.id.addToPlaylist:
		            			    	  
		            			    	 //   Adapter menuInfo=(AdapterContextMenuInfo) item.getMenuInfo();
		            			    	  //  int pos=menuInfo.position;
		            			            
		            			          //  int posi=mCardArrayAdapter.getPosition(this);
		            			           // Toast.makeText(SongsUnder.this,"First Visible position is " + firstVisiblePos, Toast.LENGTH_LONG).show();
		            			          //  Toast.makeText(SongsUnder.this," position is " + position, Toast.LENGTH_LONG).show();
		            			           
		            			            
		            			          
		            			           Intent intent=new Intent(SongsUnder.this,PlaylistSelection.class);
		            			            intent.putExtra("songName",songName);
		            			            intent.putExtra("selectedId",selectedId);
		            			            startActivity(intent);
		            			          /*
		            			            selsongname.add(songname);
		            			            String songpath=(String)allsongs.contents.get(songname);
		            			            selsongpath.add(songpath);
		            			           Toast.makeText(SongsUnder.this, selsongname.get(0) +' '+selsongpath.get(0), Toast.LENGTH_LONG).show();
		            			            
			            			          //  intent.putExtra("art",(Parcelable)allsongs);
			            			        intent.putExtra("songname",selsongname);
			            			    	intent.putExtra("songpath",selsongpath);
			            			        startActivity(intent);*/
		            			    	   
		            			    	
		            			    	   break;
		            			           case R.id.cnt_mnu_edit:
		            			        	   
	                                         UtilFunctions.changeSongInfo(SongsUnder.this,selectedId);
		            			         	  
		            			        	 
		            			        	   
		            			               //Toast.makeText(this, "Edit :" , Toast.LENGTH_SHORT).show();
		            			        	   
		            			               break;
		            			           case R.id.cnt_mnu_delete:
		            			        	 /* 
		            			        		AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
		            			        		builder.setTitle("Confirm Delete");
		            			        		builder.setMessage("Are you sure you want to delete the song?");
		            			        		builder.setCancelable(true);
		            			        		builder.setPositiveButton("Yes", 
		            			                new DialogInterface.OnClickListener() {
		            			                    public void onClick(DialogInterface dialog, int id) {
		            			                    	   ContentResolver resolver=SongsUnder.this.getApplicationContext().getContentResolver();
		            			                    	   int rowsDeleted	=resolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media.TITLE + " LIKE \"" + songName + "\"", null);
		            			                    	   File file = new File(idToPath.get(selectedId));
		            			                    	   boolean deleted = file.delete();
		            			                    	   Toast.makeText(SongsUnder.this,"File Deleted "+deleted,Toast.LENGTH_SHORT).show();
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
		            			        	     UtilFunctions.deletePopUp(getContext(), SongsUnder.this, selectedIdList, "Are you sure you want to delete the selected song?","Song deleted");
		            			        		
		            	 						//ContentValues mInserts = new ContentValues();
		            	 					 
		            	 						
		            	 						//String songName=idToName.get(id);
		            	 					//	Toast.makeText(SongsUnder.this,"songName  is "+songName,Toast.LENGTH_SHORT).show();

		            	 						//mInserts.put(MediaStore.Audio.Media.TITLE, value);
		            	 						
		            	 				     
		            			        	   
		            			        	   
		            			        	   break;
		            			           case R.id.cnt_mnu_share:
		            			        	   Intent sharingIntent=new Intent(Intent.ACTION_SEND);
		            			        	   String path=pathList.get(currPosition);
		            			        	   path="file://"+path;
		            			        	   Toast.makeText(SongsUnder.this,"Path is "+path,Toast.LENGTH_SHORT).show();
			            			        	 Uri uri=Uri.parse(path);
			            			        	 sharingIntent.setType("audio/*");
			            			        	 sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
			            			        	 sharingIntent.putExtra(Intent.EXTRA_TITLE, songName);
			            			        	 startActivity(Intent.createChooser(sharingIntent, "Share Using"));
		            			        
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
	        	 final ArrayList<String> selIdList=getidList();
	        	 final ArrayList<String> selsongNameList=getsongNameList();
	        	 final int size=selIdList.size();
	        	switch(item.getItemId()){
	        	case R.id.itemPlay:
	        		/*
	        		 Intent intent=new Intent(SongsUnder.this,PlayActivity.class);
					 	intent.putExtra("do", "Play");
			           intent.putExtra("position", 0);
			            intent.putStringArrayListExtra("playingList", selsongNameList);
			            startActivity(intent); 
			            */
	        		UtilFunctions.playSelectedSongs(SongsUnder.this,idList,0,false);
	        		break;
	        		
	        	case R.id.itemPlayNext:
	        		UtilFunctions.playSongsNext(SongsUnder.this, selIdList);
			  
	        		
	        		break;
	        	case R.id.itemAddQueue:
	        		
	        		UtilFunctions.addToQueueSongs(SongsUnder.this, selIdList);
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
	                    	   ContentResolver resolver=SongsUnder.this.getApplicationContext().getContentResolver();
	                    	   for(int i=0;i<size;i++)
	     	        		  {
	                    	  
	                    	   String selectedId=selIdList.get(i);
	                    	   String songName=idToName.get(Long.parseLong(selectedId));
	                    	   int rowsDeleted	=resolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media._ID + " LIKE \"" + selectedId + "\"", null);
	                    	   File file = new File(idToPath.get(Long.parseLong(selectedId)));
	                    	   boolean deleted = file.delete();
	     	        		  }
	                    	   Toast.makeText(SongsUnder.this, size+ " Songs deleted ",Toast.LENGTH_SHORT).show();
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
	            	
	            	UtilFunctions.deletePopUp(getContext(), SongsUnder.this, selIdList, "Are you sure you want to delete the selected Songs?","Songs deleted");
	            	break;
	            case R.id.itemToPlaylist :
	            
	            	 Intent intent=new Intent(SongsUnder.this,PlaylistSelectionMultiple.class);
			           // intent.putExtra("songName",songName);
			            
			            intent.putExtra("selectedIdList",selIdList);
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
	    	Collections.reverse(selIdList); // PROBLEM MAY ARISE
			return selIdList;
	    	   
	       }
	       
	       private ArrayList<String> getsongNameList(){
	    	   ArrayList<Card> selCards = getSelectedCards();
	    	   ArrayList<String> selsongNameList = new ArrayList<String>();
	    	   int size = selCards.size();
	    	   int i;
	    	   String title;
	    	   for(i=0;i<size;i++){
	    	 title =   selCards.get(i).getCardHeader().getTitle();
	    	     selsongNameList.add(title);
	   
	    	   }
	    	   Collections.reverse(selsongNameList);
			return selsongNameList;
	    	   
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
	    
	       CardListView listView = (CardListView)findViewById(R.id.songsUnderFragList);
	    // listView.setFastScrollEnabled(true);
	       if (listView!=null){
	           listView.setAdapter(mCardArrayAdapter);
	           listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
	          
	           
	       }
		
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
