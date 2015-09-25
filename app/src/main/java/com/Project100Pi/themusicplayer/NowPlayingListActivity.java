package com.Project100Pi.themusicplayer;

import it.gmariotti.cardslib.library.extra.dragdroplist.internal.CardDragDropArrayAdapter;
import it.gmariotti.cardslib.library.extra.dragdroplist.view.CardListDragDropView;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener;

import java.io.IOException;
import java.util.ArrayList;

import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.OnItemMovedListener;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class NowPlayingListActivity extends Activity {
	/*
	int playlistLength ;
	CardDragDropArrayAdapter mCardArrayAdapter = null;
	 ArrayList<Card> cards=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nowplayinglist_layout);
		populateCards();
		
	}
	
	private void playSelSong(Long songId){
	
		PlayActivity.getPlaySongInfo(getApplicationContext(),songId);
		songInfoObj.currPlayPos = songInfoObj.nowPlayingList.indexOf(songId);
		MainActivity.mp.reset();
		
		try {
			MainActivity.mp.setDataSource(songInfoObj.playPath);
			MainActivity.mp.prepare();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void populateCards(){
		cards = new ArrayList<Card>();
		 playlistLength = songInfoObj.nowPlayingList.size();
		 for(int i=0;i<playlistLength;i++){
	      //Create a Card
	      Card card = new Card(this);

	      //Card must have a stable Id.
	       card.setId(songInfoObj.nowPlayingList.get(i));

	      //Create a CardHeader
	      CardHeader header = new CardHeader(this);
	
	      //Add Header to card
	      card.addCardHeader(header);
	      header.setTitle(FirstFragment.idToName.get(Long.parseLong(songInfoObj.nowPlayingList.get(i))));
	 	 
	     card.setInnerLayout(R.layout.track_layout);
	    
	      card.setOnClickListener(new OnCardClickListener() {
				
				@Override
				public void onClick(Card card, View view) {
					// TODO Auto-generated method stub
					playSelSong(Long.parseLong(card.getId()));
					
				}
			});
	      cards.add(card);
	    
	    
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
			            	    popupMenu.inflate(R.menu.now_playing_options);
			            	    View parentRow=(View) v.getParent();
					            CardListDragDropView myListView=(CardListDragDropView) parentRow.getParent().getParent();
					            final int currPosition=myListView.getPositionForView(parentRow);
					            final String songName= cards.get(currPosition).getCardHeader().getTitle();
					             final Long selectedId=Long.parseLong(songInfoObj.nowPlayingList.get(currPosition));
					             final ArrayList<String> selectedIdList=new ArrayList<String>();
					             selectedIdList.add(selectedId.toString()); // created to send the ArrayList to UtilFunctions.deletePopUp
					             final ArrayList<String> selSongNameList = new ArrayList<String>();
					             selSongNameList.add(songName);
					            Toast.makeText(NowPlayingListActivity.this, songName +"and position is" + currPosition, Toast.LENGTH_LONG).show();
					            
			            	    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			            			
			            			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
									@Override
			            			public boolean onMenuItemClick(MenuItem item) {
										
			            			             	
			            			       switch(item.getItemId()){
			            			       
			            			       case R.id.now_play_play:
			            			    	   playSelSong(selectedId);
			            			    	   break;
			            			       case R.id.now_play_edit:
			            			    	   UtilFunctions.changeSongInfo(NowPlayingListActivity.this, selectedId);
			            			    	   break;
			            			       case R.id.now_play_add_playlist:
			            			    	   Intent intent=new Intent(NowPlayingListActivity.this,PlaylistSelection.class);
			            			            intent.putExtra("songName",songName);
			            			            intent.putExtra("selectedId",selectedId);
			            			            startActivity(intent);
			            			    	   break;
			            			       case R.id.now_play_share:
			            			    	   Intent sharingIntent=new Intent(Intent.ACTION_SEND);
		            			        	   String path=FirstFragment.pathList.get(currPosition);
		            			        	   path="file://"+path;
		            			        	   Toast.makeText(NowPlayingListActivity.this,"Path is "+path,Toast.LENGTH_SHORT).show();
			            			        	 Uri uri=Uri.parse(path);
			            			        	 sharingIntent.setType("audio/*");
			            			        	 sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
			            			        	 sharingIntent.putExtra(Intent.EXTRA_TITLE, songName);
			            			        	 startActivity(Intent.createChooser(sharingIntent, "Share Using"));
			            			    	   break;
			            			       case R.id.now_play_delete:
			            			    	   
			            			    	   UtilFunctions.deletePopUp(getContext(),NowPlayingListActivity.this, selectedIdList, "Are you sure you want to delete the selected song?","Song deleted");
			            			    	   break;
			            			       case R.id.now_play_remove_from_queue:
			            			    	   songInfoObj.nowPlayingList.remove(selectedId.toString());
			            			    	   songInfoObj.initialPlayingList.remove(selectedId.toString());
			            			    	   if(songInfoObj.currPlayPos == currPosition){
			            			    		   MainActivity.mp.reset();
			            			    	   }
			            			    	   Toast.makeText(NowPlayingListActivity.this,songName +" is removed from the Queue",Toast.LENGTH_SHORT).show();
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

		   CardListDragDropView mListView = (CardListDragDropView) this.findViewById(R.id.carddemo_extra_list_dragdrop);
		   mListView.setFastScrollEnabled(true);
		   if (mListView != null) {
		           mListView.setAdapter(mCardArrayAdapter);
		   }
	

		   mListView.setOnItemMovedListener(new OnItemMovedListener() {
			      @Override
			      public void onItemMoved(int originalPosition, int newPosition) {
			          Card card = mCardArrayAdapter.getItem(newPosition);
			          Toast.makeText(NowPlayingListActivity.this,"Card "+card.getId() + " moved to position " + newPosition, Toast.LENGTH_SHORT ).show();
			          songInfoObj.nowPlayingList.clear();
			         
			          for(int i=0;i<playlistLength;i++)
			          {
			        	  Card tempcard=mCardArrayAdapter.getItem(i);
			        	  songInfoObj.nowPlayingList.add(tempcard.getId());
			        	  
			         }
			         songInfoObj.currPlayPos= songInfoObj.nowPlayingList.indexOf(songInfoObj.songId.toString());
			        // populateCards();
			      }
			   });
			  
	}
	*/
}

