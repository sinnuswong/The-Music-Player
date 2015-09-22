package com.Project100Pi.themusicplayer;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.R.color;
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
import android.os.Parcelable;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.ArtistColumns;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
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
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.base.CardViewWrapper;




public class ThirdFragment extends Fragment  {
	 MyIndexerAdapter mCardArrayAdapter = null;
	   static HashMap<Long, ArtistInfo> idToartistInfo =  new HashMap<Long,ArtistInfo>();

	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
    	ArrayList<Card> cards = new ArrayList<Card>();
    	String artistName="";
    	Long artistId;
    	int noOfSongs,noOfAlbums;
    	String[] projection = new String[] {BaseColumns._ID, ArtistColumns.ARTIST,ArtistColumns.NUMBER_OF_TRACKS,ArtistColumns.NUMBER_OF_ALBUMS };
    	String selection = null;
    	String[] selectionArgs = null;
    	String sortOrder = MediaStore.Audio.Artists.DEFAULT_SORT_ORDER;
    	Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sortOrder);
        final ArrayList<String> artistsList = new ArrayList<String>();
        final ArrayList<Long> artistsIdList = new ArrayList<Long>();
       while (cursor.moveToNext()) {
            artistName = cursor.getString(1);  
            noOfSongs = cursor.getInt(2);
            noOfAlbums = cursor.getInt(3);
            artistId = cursor.getLong(0);
            artistsList.add(artistName);
            artistsIdList.add(artistId);
            Card card = new Card(this.getActivity());
            ArtistInfo thisSong = new ArtistInfo();
            thisSong.artistId = artistId;
            thisSong.artistName=artistName;
            thisSong.noOfAlbums = noOfAlbums;
            thisSong.noOfSongs = noOfSongs;
            idToartistInfo.put(artistId,thisSong);
            //Create a CardHeader
            CardHeader header = new CardHeader(this.getActivity());
      
            //Add Header to card
            card.addCardHeader(header);
            header.setTitle(artistName);
            card.setClickable(true);
            card.setId(artistId.toString());
            card.setInnerLayout(R.layout.artist_layout);
            card.setOnClickListener(new OnCardClickListener() {
				
				@Override
				public void onClick(Card card, View view) {
					// TODO Auto-generated method stub
					
					 Intent intent=new Intent(getActivity(),SongsUnder.class);
			            intent.putExtra("X","Artist");
			            intent.putExtra("id", Long.parseLong(card.getId()));
			            startActivity(intent);   
				}
			});
            card.setOnLongClickListener(new Card.OnLongCardClickListener() {
                @Override
                public boolean onLongClick(Card card, View view) {
                    return mCardArrayAdapter.startActionMode(getActivity());

                }
           });
            cards.add(card);
          //  path.put(title,fullpath);
           
        }
   	//View v = inflater.inflate(R.layout.first_frag, container, false);
       View v =inflater.inflate(R.layout.third_frag, container, false);
		/*
       Button shuffleThirdFrag = (Button) v.findViewById(R.id.shuffle_third_frag);
       shuffleThirdFrag.setOnClickListener(new OnClickListener() {
 		
 		@Override
 		public void onClick(View v) {
 			// TODO Auto-generated method stub
 			UtilFunctions.playSelectedSongs(getActivity(), FirstFragment.idList, 0, true);
 		}
 	});
 	*/
       mCardArrayAdapter = new MyIndexerAdapter(this.getActivity(),cards){
    	   
    	    
 		  

    		@Override
    		 public View getView( final int position, View convertView, final ViewGroup parent) {
    				
    			    View v =super.getView(position, convertView, parent);
    			    Long id = artistsIdList.get(position);
    			    ArtistInfo thisSong = idToartistInfo.get((id));
				LinearLayout cardLayout = (LinearLayout) v.findViewById(R.id.card_main_layout);
				if(position%2 == 0 ) {
					cardLayout.setBackgroundColor(Color.parseColor("#3D3D3D"));
				}else{
					cardLayout.setBackgroundColor(Color.parseColor("#484848"));
				}
    		    	TextView artistTitle = (TextView) v.findViewById(R.id.artist_album_total);
    		    	artistTitle.setText(thisSong.noOfAlbums+" Albums");
    		    	TextView durSong = (TextView) v.findViewById(R.id.artist_total);
    		    	durSong.setText(thisSong.noOfSongs+" Tracks");
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
    			           // final String songName=titleList.get(currPosition);
    			             final Long selectedArtistId=artistsIdList.get(currPosition);
    			             final Cursor cursor;
    			          //  Toast.makeText(getActivity(), songName +"and position is" + currPosition, Toast.LENGTH_LONG).show();
    			            
    	            	    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
    	            			
    	            			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    							@Override
    	            			public boolean onMenuItemClick(MenuItem item) {
    								
    	            				   Cursor cursor=null;    	
    	            			       switch(item.getItemId()){
    	            			       case R.id.cnt_menu_play:
    	            			    	   
 		            			    	  UtilFunctions.playSelectedSongsfromChoice(getActivity(), selectedArtistId, "artist",false);
 		            			    	   
 		            			    	   break;
    	            			       case R.id.cnt_menu_play_next:
    		            			    	  
   		            			    	UtilFunctions.playSongsNextfromChoice(getActivity(), selectedArtistId, "artist");
   		            			    	   
   		            			    	   break;
   		            			    	   
   		            			       case R.id.cnt_menu_add_queue:
   		            			    		UtilFunctions.addToQueuefromChoice(getActivity(),selectedArtistId, "artist");
   		            			    	   break;
    	            			       case R.id.addToPlaylist:
    	            			    	   
    	            			    	/*  
    	            			    	   Intent intent=new Intent(getActivity(),PlaylistSelectionMultiple.class);
    	            			           // intent.putExtra("songName",songName);
    	            			            Cursor cursor=makeArtistSongCursor(selectedArtistId);
    	            			            ArrayList<String> audioIdList = new ArrayList<String>();
    	            			          
    	         			        	 //  ArrayList<Uri> uris=new ArrayList<Uri>();
    	         			        	   while(cursor.moveToNext()){
    	         			        		   Long id=cursor.getLong(0);
    	         			        		   audioIdList.add(id.toString()); 
    	         			        	   }
    	            			            intent.putExtra("selectedIdList", audioIdList);
    	            			            startActivity(intent);*/
    	            			    	   
    	            			    	    startActivity(UtilFunctions.addSongstoPlaylist(getActivity(),selectedArtistId,"artist"));
    	            			    	   
    	            			    	
    	            			    	   break;
    	            			           case R.id.cnt_mnu_edit:
    	            			        	   
    	                                     // changeSongInfo(selectedId);
    	            			        			        	   
    	            			               //Toast.makeText(this, "Edit :" , Toast.LENGTH_SHORT).show();
    	            			        	   
    	            			               break;
    	            			           case R.id.cnt_mnu_delete:
    	            			        	/*   AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
    	               			       		builder.setTitle("Confirm Delete");
    	               			       		builder.setMessage("Are you sure you want to delete the selected songs?");
    	               			       		builder.setCancelable(true);
    	               			       		builder.setPositiveButton("Yes", 
    	               			               new DialogInterface.OnClickListener() {
    	               			                   public void onClick(DialogInterface dialog, int id) {
    	               			                   	   ContentResolver resolver=getActivity().getApplicationContext().getContentResolver();
    	               			                   	 Cursor cursor=makeArtistSongCursor(selectedArtistId);
    	               			                     while(cursor.moveToNext()){
    	                      			        		   Long songId=cursor.getLong(0);
    	               			                   	  
    	               			                   	 
    	               			                   	   String songName=FirstFragment.idToName.get(songId);
    	               			                   	   int rowsDeleted	=resolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media._ID + " LIKE \"" + songId + "\"", null);
    	               			                   	   File file = new File(FirstFragment.idToPath.get(songId));
    	               			                   	   boolean deleted = file.delete();
    	               			    	        		  }
    	               			                   	   Toast.makeText(getActivity(),  " Artist deleted ",Toast.LENGTH_SHORT).show();
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
    	            			        	   UtilFunctions.deleteSinglePopUp(getContext(), getActivity(), selectedArtistId.toString(),"Are you sure you want to delete the selected Artist?"," 1 Artist Deleted","artist");
    	            			        	   break;
    	            			           case R.id.cnt_mnu_share:
    	            			        	   
    	            			        	  
    		            			        	 startActivity(UtilFunctions.shareSingle(getActivity(), selectedArtistId, "artist"));
    	            			        		  
    	            			        
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
    	    	 final int size=selIdList.size();
    	    	 Cursor cursor=null;
    	    	switch(item.getItemId()){
    	    	case R.id.itemShuffle:
       	    		UtilFunctions.playSelectedSongfromMultiChoice(getActivity(), selIdList, "artist", true);
       	    		break;
    	    	
    	    		case R.id.itemPlay:
       	    		
       	    		UtilFunctions.playSelectedSongfromMultiChoice(getActivity(), selIdList, "artist",false);
       	    	break;	
    	    		case R.id.itemPlayNext:
    	           		UtilFunctions.playSongsNextfromMultiChoice(getActivity(), selIdList, "artist");
    	           		break;
    	           	case R.id.itemAddQueue:
    	           		
    	           		UtilFunctions.AddToQueuefromMultiChoice(getActivity(), selIdList, "artist");
    	           		break;	
    	    	case R.id.itemShare:
    	        	
    		        	// sharingIntent.
    		         
    		        	 startActivity(UtilFunctions.shareMultiple(getActivity(), selIdList, "artist"));	  
    		        	 break;
    		        	 
    	        case R.id.itemDelete:
    	        	/*
    	        	AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
    	    		builder.setTitle("Confirm Delete");
    	    		builder.setMessage("Are you sure you want to delete the selected Artists?");
    	    		builder.setCancelable(true);
    	    		builder.setPositiveButton("Yes", 
    	            new DialogInterface.OnClickListener() {
    	                public void onClick(DialogInterface dialog, int id) {
    	                	   ContentResolver resolver=getActivity().getApplicationContext().getContentResolver();
    	                	   for(int i=0;i<size;i++)
    	 	        		  {
    	                	  
    	                	   String selectedArtistId=selIdList.get(i);
    	                	   Cursor cursor=makeArtistSongCursor(Long.parseLong(selectedArtistId));
    	                	   while(cursor.moveToNext()){
    	            	    	   Long songId=cursor.getLong(0);
    	                	       String songName=FirstFragment.idToName.get(songId);
    	                	       int rowsDeleted	=resolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media._ID + " LIKE \"" + songId + "\"", null);
    	                	       File file = new File(FirstFragment.idToPath.get(songId));
    	                	       boolean deleted = file.delete();
    	 	        		    }
    	 	        		  }
    	                	   Toast.makeText(getActivity(), size+ " Albums deleted ",Toast.LENGTH_SHORT).show();
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
    	            alert11.show();*/
    	        	
    	        	UtilFunctions.deleteMultiplePopUp(getContext(), getActivity(), selIdList, "Are you sure you want to delete the selected artists?","Artists deleted","artist");
    	        	
    	        	break;
    	        case R.id.itemToPlaylist :
    	          /*
    	        	Intent intent=new Intent(getActivity(),PlaylistSelectionMultiple.class);
    		           // intent.putExtra("songName",songName);
    	        	    ArrayList<String> entireIdList=new ArrayList<String>(); // arrayList obtained by getting all the ids from the selected albums
    	        	    for(int i=0;i<size;i++)
    	        	    {
    	 	              String albumId=selIdList.get(i);
    	 	              cursor=makeArtistSongCursor(Long.parseLong(albumId));
    	 	              while(cursor.moveToNext()){
    	    	    	     Long id=cursor.getLong(0);
    	    	    	     entireIdList.add(id.toString());
    	 	              }
    	        	    }
    		            intent.putExtra("selectedIdList",entireIdList);*/
    		            startActivity(UtilFunctions.addMultipletoPlaylist(getActivity(), selIdList, "artist"));
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
    
       NestedCardListView listView = (NestedCardListView) v.findViewById(R.id.thirdFragList);
       listView.setFastScrollEnabled(true);
       if (listView!=null){
           listView.setAdapter(mCardArrayAdapter);
           listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
           
       }
       
     

        return v;
    }
    
    private final Cursor makeArtistSongCursor(final Long artistId) {
		final StringBuilder selection = new StringBuilder();
		selection.append(AudioColumns.IS_MUSIC + "=1");
		selection.append(" AND " + AudioColumns.TITLE + " != ''");
		selection.append(" AND " + AudioColumns.ARTIST_ID + "=" + artistId);
		return getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
		new String[] {
		/* 0 */ BaseColumns._ID,
		/* 1 */ AudioColumns.TITLE,
		/* 2 */ AudioColumns.ARTIST,
		/* 3 */ AudioColumns.ARTIST
		}, selection.toString(), null, MediaStore.Audio.Media.TRACK + ", " + MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		}

  
    public static ThirdFragment newInstance(String text) {

        ThirdFragment f = new ThirdFragment();
        return f;
    }
}