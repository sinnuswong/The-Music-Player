package com.Project100Pi.themusicplayer;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.squareup.picasso.Picasso;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AlbumColumns;
import android.provider.MediaStore.Audio.AudioColumns;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener;
import it.gmariotti.cardslib.library.internal.CardArrayMultiChoiceAdapter;
import it.gmariotti.cardslib.library.internal.CardGridArrayMultiChoiceAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardSection;
import it.gmariotti.cardslib.library.view.CardGridView;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.base.CardViewWrapper;




public class SecondFragment extends Fragment  {
	
   CardArrayMultiChoiceAdapter mCardArrayAdapter = null;
   CardGridArrayMultiChoiceAdapter mCardGridArrayAdapter = null;
   static HashMap<Long, AlbumInfo> idToalbumInfo =  new HashMap<Long,AlbumInfo>();
   ArrayList<Long> idList = new ArrayList<Long>();
   View v =null;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
    	
    	final ArrayList<Card> cards = new ArrayList<Card>();
    	final ArrayList<String> albumArtPaths = new ArrayList<String>();
    	String albumName="";
    	String artistName = "";
    	String albumArtPath = "";
    	Long albumId;
    	int noOfSongs;
    	String[] projection = new String[] {BaseColumns._ID, AlbumColumns.ALBUM,AlbumColumns.ARTIST,AlbumColumns.NUMBER_OF_SONGS,AlbumColumns.ALBUM_ART };
    	String selection = null;
    	String[] selectionArgs = null;
    	String sortOrder = MediaStore.Audio.Albums.DEFAULT_SORT_ORDER;
    	Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sortOrder);
        final ArrayList<String> albumsList = new ArrayList<String>();
        final ArrayList<Long> albumsIdList = new ArrayList<Long>();
 
        
    
       while (cursor.moveToNext()) {
            albumName = cursor.getString(1);
            albumId = cursor.getLong(0);
            artistName = cursor.getString(2);
            noOfSongs = cursor.getInt(3);
            albumArtPath = cursor.getString(4);
            albumArtPaths.add(albumArtPath);
            albumsList.add(albumName);
            albumsIdList.add(albumId);
            AlbumInfo thisSong = new AlbumInfo(1,albumId,albumName,artistName,albumArtPath,noOfSongs);
            thisSong.albumName = albumName;
            thisSong.albumId = albumId;
            thisSong.artistName = artistName;
            thisSong.noOfSongs = noOfSongs;
            idToalbumInfo.put(albumId, thisSong);
            idList.add(albumId);
            Card card = null;
            if(MainActivity.albumViewOption.equals("List")){
            	 card = new Card(this.getActivity());

                //Create a CardHeader
               
                card.setClickable(true);
                card.setId(albumId.toString());
            	CardHeader header = new CardHeader(this.getActivity());
                
                //Add Header to card
                card.addCardHeader(header);
           	header.setTitle(albumName);
            	 
            card.setInnerLayout(R.layout.album_layout);
            card.setOnLongClickListener(new Card.OnLongCardClickListener() {
                @Override
                public boolean onLongClick(Card card, View view) {
                    return mCardArrayAdapter.startActionMode(getActivity());

                }
           });
                       
            }else if(MainActivity.albumViewOption.equals("Grid")){
            card = new Card(this.getActivity());

                //Create a CardHeader
               
                card.setClickable(true);
                card.setId(albumId.toString());
                
               
            	card.setInnerLayout(R.layout.album_grid_inner_layout);
            	 card.setOnLongClickListener(new Card.OnLongCardClickListener() {
                     @Override
                     public boolean onLongClick(Card card, View view) {
                         return mCardGridArrayAdapter.startActionMode(getActivity());

                     }
                });
            }
            
 card.setOnClickListener(new OnCardClickListener() {
				
				@Override
				public void onClick(Card card, View view) {
					// TODO Auto-generated method stub
					
					 Intent intent=new Intent(getActivity(),SongsUnder.class);
			            intent.putExtra("X","Album");
			            intent.putExtra("id", Long.parseLong(card.getId()));
			            startActivity(intent);   
				}
			});
           
            cards.add(card);
          //  path.put(title,fullpath);
           
        }
        
   	//View v = inflater.inflate(R.layout.first_frag, container, false);
       if(MainActivity.albumViewOption.equals("List")){
    	   
      v =inflater.inflate(R.layout.second_frag, container, false);
		   /*
      Button shuffleSecondFrag = (Button) v.findViewById(R.id.shuffle_second_frag);
      shuffleSecondFrag.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			UtilFunctions.playSelectedSongs(getActivity(), FirstFragment.idList, 0, true);
		}
	});
	*/
      mCardArrayAdapter = new CardArrayMultiChoiceAdapter(this.getActivity(),cards){
   	   
    	    
		  

    		@Override
    		 public View getView( final int position, View convertView, final ViewGroup parent) {
    				
    			    View v =super.getView(position, convertView, parent);
    			    final Long albumId = idList.get(position);
    			    final AlbumInfo thisSong = idToalbumInfo.get((albumId));
				LinearLayout cardLayout = (LinearLayout) v.findViewById(R.id.card_main_layout);

				if(position%2 == 0 ) {
					cardLayout.setBackgroundColor(Color.parseColor("#3D3D3D"));
				}else{
					cardLayout.setBackgroundColor(Color.parseColor("#484848"));
				}
    			    
    		    	TextView artistTitle = (TextView) v.findViewById(R.id.album_artist_title);
    		    	artistTitle.setText(thisSong.artistName);
    		    	TextView durSong = (TextView) v.findViewById(R.id.album_total);
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
    	            	    CardListView myView ;
    	            	     int currPosition = 0;
    	            	   
    			            myView=(CardListView) parentRow.getParent();
    			            currPosition=((AdapterView<ListAdapter>) myView).getPositionForView(parentRow);
    	            	   
    			           
    			           // final String songName=titleList.get(currPosition);
    			             final Long selectedAlbumId=albumsIdList.get(currPosition);
    			             final String selectedAlbumName = cards.get(currPosition).getCardHeader().getTitle();
    			          //  Toast.makeText(getActivity(), songName +"and position is" + currPosition, Toast.LENGTH_LONG).show();
    			            
    	            	    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
    	            			
    	            			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    							@Override
    	            			public boolean onMenuItemClick(MenuItem item) {
    								
    	            			             	
    	            			       switch(item.getItemId()){
    	            			       case R.id.cnt_menu_play:
    	            			    	   
    		            			    	  UtilFunctions.playSelectedSongsfromChoice(getActivity(), selectedAlbumId, "album",false);
    		            			    	   
    		            			    	   break;
    		            			    	   
    	            			       case R.id.cnt_menu_play_next:
    		            			    	  
    		            			    	UtilFunctions.playSongsNextfromChoice(getActivity(), selectedAlbumId, "album");
    		            			    	   
    		            			    	   break;
    		            			    	   
    		            			       case R.id.cnt_menu_add_queue:
    		            			    		UtilFunctions.addToQueuefromChoice(getActivity(),selectedAlbumId, "album");
    		            			    	   break; 	   
    	            			       case R.id.addToPlaylist:
    	            			    	  
    	            			    	   startActivity(UtilFunctions.addSongstoPlaylist(getActivity(),selectedAlbumId,"album"));
    	            			    	
    	            			    	   break;
    	            			           case R.id.cnt_mnu_edit:
    	            			        	   
    	            			        	  editAlbumInfo(selectedAlbumName, selectedAlbumId); // NOT WORKING
    	            			        	   
    	            			               break;
    	            			           case R.id.cnt_mnu_delete:
    	            			        	   /*
    	            			        	   AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
    	            			       		builder.setTitle("Confirm Delete");
    	            			       		builder.setMessage("Are you sure you want to delete the selected songs?");
    	            			       		builder.setCancelable(true);
    	            			       		builder.setPositiveButton("Yes", 
    	            			               new DialogInterface.OnClickListener() {
    	            			                   public void onClick(DialogInterface dialog, int id) {
    	            			                   	   ContentResolver resolver=getActivity().getApplicationContext().getContentResolver();
    	            			                   	 Cursor cursor=makeAlbumSongCursor(selectedAlbumId);
    	            			                     while(cursor.moveToNext()){
    	                   			        		   Long songId=cursor.getLong(0);
    	            			                   	  
    	            			                   	 
    	            			                   	   String songName=FirstFragment.idToName.get(songId);
    	            			                   	   int rowsDeleted	=resolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media._ID + " LIKE \"" + songId + "\"", null);
    	            			                   	   File file = new File(FirstFragment.idToPath.get(songId));
    	            			                   	   boolean deleted = file.delete();
    	            			    	        		  }
    	            			                   	   Toast.makeText(getActivity(),  " Album deleted ",Toast.LENGTH_SHORT).show();
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
    	            			        	   UtilFunctions.deleteSinglePopUp(getContext(), getActivity(), selectedAlbumId.toString(),"Are you sure you want to delete the selected Album?"," 1 Album Deleted","album");
    	            			        	  
    	            			        	   
    	            			        	   break;
    	            			           case R.id.cnt_mnu_share:
    	            			        	   

    	            			        	   startActivity(UtilFunctions.shareSingle(getActivity(), selectedAlbumId, "album"));	  
    	            			        
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
    	    	
    	    		case R.id.itemPlay:
       	    		
       	    		UtilFunctions.playSelectedSongfromMultiChoice(getActivity(), selIdList, "album",false);
       	    	break;	
    	    	
    	    	 case R.id.itemPlayNext:
                  		UtilFunctions.playSongsNextfromMultiChoice(getActivity(), selIdList, "album");
                  		break;
                  	case R.id.itemAddQueue:
                  		
                  		UtilFunctions.AddToQueuefromMultiChoice(getActivity(), selIdList, "album");
                  		break;	  
    	    	
    	        case R.id.itemShare:
    	        	     startActivity(UtilFunctions.shareMultiple(getActivity(), selIdList, "album"));	  	  
    		        	 break;
    		        	 
    	        case R.id.itemDelete:
    	        	UtilFunctions.deleteMultiplePopUp(getContext(), getActivity(), selIdList, "Are you sure you want to delete the selected albums?","Albums deleted","album");    	        	
    	        	break;
    	        case R.id.itemToPlaylist :
    	        /*
    	        	Intent intent=new Intent(getActivity(),PlaylistSelectionMultiple.class);
    		           // intent.putExtra("songName",songName);
    	        	    ArrayList<String> entireIdList=new ArrayList<String>(); // arrayList obtained by getting all the ids from the selected albums
    	        	    for(int i=0;i<size;i++)
    	        	    {
    	 	              String albumId=selIdList.get(i);
    	 	              cursor=makeAlbumSongCursor(Long.parseLong(albumId));
    	 	              while(cursor.moveToNext()){
    	    	    	     Long id=cursor.getLong(0);
    	    	    	     entireIdList.add(id.toString());
    	 	              }
    	        	    }
    		            intent.putExtra("selectedIdList",entireIdList);
    		            startActivity(intent);*/
    	        	  startActivity(UtilFunctions.addMultipletoPlaylist(getActivity(), selIdList, "album"));
    	        	break;
    		        
    	    		
    	    	
    	    	}
    	       
    	        return true;
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
    	   
    	   NestedCardListView listView = (NestedCardListView) v.findViewById(R.id.secondFragList);
    	   listView.setFastScrollEnabled(true);
    	   if (listView!=null){
    	       listView.setAdapter(mCardArrayAdapter);
    	       listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
    	   }
      
       }
       else if(MainActivity.albumViewOption.equals("Grid")){
      	 
    	   v =inflater.inflate(R.layout.second_frag_grid, container, false);
    	   Button shuffleSecondFrag = (Button) v.findViewById(R.id.shuffle_second_frag_grid);
    	      shuffleSecondFrag.setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				// TODO Auto-generated method stub
    				UtilFunctions.playSelectedSongs(getActivity(), FirstFragment.idList, 0, true);
    			}
    		});
    	   mCardGridArrayAdapter = new CardGridArrayMultiChoiceAdapter(this.getActivity(),cards){
    	   	   
       	    
    		  
       		@Override
       		 public View getView( final int position, View convertView, final ViewGroup parent) {
       		
       		
       				
       			    View v =super.getView(position, convertView, parent); 
       			    final Long albumId = idList.get(position);
       			    final AlbumInfo thisSong = idToalbumInfo.get((albumId));
   
       			    	final TextView albumTitle = (TextView) v.findViewById(R.id.album_grid_title);
       			    	albumTitle.setText(thisSong.albumName);
       			    	TextView artistTitle = (TextView) v.findViewById(R.id.album_grid_artist);
       			    	artistTitle.setText(thisSong.artistName);
       			    	/*
       			    	Bitmap bitmap = CursorClass.albumArtCursor(getActivity().getApplicationContext(),id);
       			        ImageView view =(ImageView) v.findViewById(R.id.album_grid_art);
       			        view.setImageBitmap(bitmap);
       			  */
       			    	
       			    
       			    	final ImageView imView =(ImageView) v.findViewById(R.id.album_grid_art);
       			    	Picasso.with(getActivity().getApplicationContext()).load("file:///"+albumArtPaths.get(position)).into(imView);
       			    //	imView.setImageBitmap(loadedImage);
       			    	
       			    	
       			 
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
       	            	    CardGridView myView ;
       	            	     int currPosition = 0;
       	            	   
       	            	    	 myView = (CardGridView) parentRow.getParent();
       	            	    	 currPosition=((AdapterView<ListAdapter>) myView).getPositionForView(parentRow);
       	            	   
       			           
       			           // final String songName=titleList.get(currPosition);
       			             final Long selectedAlbumId=albumsIdList.get(currPosition);
       			          final String selectedAlbumName = albumTitle.getText().toString();
       			          //  Toast.makeText(getActivity(), songName +"and position is" + currPosition, Toast.LENGTH_LONG).show();
       			            
       	            	    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
       	            			
       	            			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
       							@Override
       	            			public boolean onMenuItemClick(MenuItem item) {
       								
       	            			             	
       	            			       switch(item.getItemId()){
       	            			    case R.id.cnt_menu_play:
 	            			    	   
		            			    	  UtilFunctions.playSelectedSongsfromChoice(getActivity(), selectedAlbumId, "album",false);
		            			    	   
		            			    	   break;
		            			    
       	            			 case R.id.cnt_menu_play_next:
	            			    	  
		            			    	UtilFunctions.playSongsNextfromChoice(getActivity(), selectedAlbumId, "album");
		            			    	   
		            			    	   break;
		            			    	   
		            			       case R.id.cnt_menu_add_queue:
		            			    		UtilFunctions.addToQueuefromChoice(getActivity(),selectedAlbumId, "album");
		            			    	   break; 	 	   
       	            			 
       	            			    case R.id.addToPlaylist:
  	            			    	  
 	            			    	   startActivity(UtilFunctions.addSongstoPlaylist(getActivity(),selectedAlbumId,"album"));
 	            			    	
 	            			    	   break;
 	            			           case R.id.cnt_mnu_edit:
 	            			        	   
 	                                     editAlbumInfo(selectedAlbumName, selectedAlbumId); // NOT WORKING
 	            			        	   
 	            			               break;
 	            			           case R.id.cnt_mnu_delete:
 	            			        	   /*
 	            			        	   AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
 	            			       		builder.setTitle("Confirm Delete");
 	            			       		builder.setMessage("Are you sure you want to delete the selected songs?");
 	            			       		builder.setCancelable(true);
 	            			       		builder.setPositiveButton("Yes", 
 	            			               new DialogInterface.OnClickListener() {
 	            			                   public void onClick(DialogInterface dialog, int id) {
 	            			                   	   ContentResolver resolver=getActivity().getApplicationContext().getContentResolver();
 	            			                   	 Cursor cursor=makeAlbumSongCursor(selectedAlbumId);
 	            			                     while(cursor.moveToNext()){
 	                   			        		   Long songId=cursor.getLong(0);
 	            			                   	  
 	            			                   	 
 	            			                   	   String songName=FirstFragment.idToName.get(songId);
 	            			                   	   int rowsDeleted	=resolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media._ID + " LIKE \"" + songId + "\"", null);
 	            			                   	   File file = new File(FirstFragment.idToPath.get(songId));
 	            			                   	   boolean deleted = file.delete();
 	            			    	        		  }
 	            			                   	   Toast.makeText(getActivity(),  " Album deleted ",Toast.LENGTH_SHORT).show();
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
 	            			        	   UtilFunctions.deleteSinglePopUp(getContext(), getActivity(), selectedAlbumId.toString(),"Are you sure you want to delete the selected Album?"," 1 Album Deleted","album");
 	            			        	  
 	            			        	   
 	            			        	   break;
 	            			           case R.id.cnt_mnu_share:
 	            			        	   

 	            			        	   startActivity(UtilFunctions.shareSingle(getActivity(), selectedAlbumId, "album"));	  
 	            			        
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
             //It is very important to call the super method!
             super.onCreateActionMode(mode, menu);


             ActionMode mActionMode = mode; // to manage in your Fragment/Activity

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
       	    		UtilFunctions.playSelectedSongfromMultiChoice(getActivity(), selIdList, "album", true);
       	    		break;
       	    	case R.id.itemPlay:
       	    		
       	    		UtilFunctions.playSelectedSongfromMultiChoice(getActivity(), selIdList, "album",false);
       	    	break;	
       	     case R.id.itemPlayNext:
           		UtilFunctions.playSongsNextfromMultiChoice(getActivity(), selIdList, "album");
           		break;
           	case R.id.itemAddQueue:
           		
           		UtilFunctions.AddToQueuefromMultiChoice(getActivity(), selIdList, "album");
           		break;	 
       	     case R.id.itemShare:
        	     startActivity(UtilFunctions.shareMultiple(getActivity(), selIdList, "album"));	  	  
	        	 break;
	        	 
        case R.id.itemDelete:
        	UtilFunctions.deleteMultiplePopUp(getContext(), getActivity(), selIdList, "Are you sure you want to delete the selected albums?","Albums deleted","album");    	        	
        	break;
        case R.id.itemToPlaylist :
        /*
        	Intent intent=new Intent(getActivity(),PlaylistSelectionMultiple.class);
	           // intent.putExtra("songName",songName);
        	    ArrayList<String> entireIdList=new ArrayList<String>(); // arrayList obtained by getting all the ids from the selected albums
        	    for(int i=0;i<size;i++)
        	    {
 	              String albumId=selIdList.get(i);
 	              cursor=makeAlbumSongCursor(Long.parseLong(albumId));
 	              while(cursor.moveToNext()){
    	    	     Long id=cursor.getLong(0);
    	    	     entireIdList.add(id.toString());
 	              }
        	    }
	            intent.putExtra("selectedIdList",entireIdList);
	            startActivity(intent);*/
        	  startActivity(UtilFunctions.addMultipletoPlaylist(getActivity(), selIdList, "album"));
        	break;
	        
    		
    	
    	}
       
        return true;
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

       	        SparseBooleanArray checked = mCardGridView.getCheckedItemPositions();
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
       	   
       	 CardGridView gridView = (CardGridView) v.findViewById(R.id.secondFragGrid);
         if (gridView!=null){
              gridView.setAdapter(mCardGridArrayAdapter);
              gridView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
       }
       
      
  
        }
    	 
       
	
       return v;
}

       
    private final Cursor makeAlbumSongCursor(final Long albumId) {
		final StringBuilder selection = new StringBuilder();
		selection.append(AudioColumns.IS_MUSIC + "=1");
		selection.append(" AND " + AudioColumns.TITLE + " != ''");
		selection.append(" AND " + AudioColumns.ALBUM_ID + "=" + albumId);
		return getActivity().getApplicationContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
		new String[] {
		/* 0 */ BaseColumns._ID,
		/* 1 */ AudioColumns.TITLE,
		/* 2 */ AudioColumns.ALBUM,
		/* 3 */ AudioColumns.ALBUM
		}, selection.toString(), null, MediaStore.Audio.Media.TRACK + ", " + MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		}
    
    private void editAlbumInfo(String albumName, final Long selectedAlbumId){
    	 LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
 		View promptView = layoutInflater.inflate(R.layout.dialog_box, null);
 		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
 		alertDialogBuilder.setView(promptView);
 	//	Toast.makeText(activity,"Id is "+id,Toast.LENGTH_SHORT).show();
 		TextView textView = (TextView) promptView.findViewById(R.id.textView);
 		textView.setText("Edit Album Name");
 		final EditText editTitleText = (EditText) promptView.findViewById(R.id.edittext);
 		editTitleText.setText(albumName);
 	
 		//final EditText editAlbumText=(EditText) promptView.findViewById(R.id.edittext);
 		// setup a dialog window
 		alertDialogBuilder.setCancelable(false)
 				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
 					public void onClick(DialogInterface dialog, int id) {
 						String albumChange = editTitleText.getText().toString();
 						
 						ContentValues mInserts = new ContentValues();
 					    ContentResolver resolver=getActivity().getApplicationContext().getContentResolver();
 						Uri albumUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
 						//String songName=idToName.get(id);
 					//	Toast.makeText(activity,"songName  is "+songName,Toast.LENGTH_SHORT).show();

 						mInserts.put(MediaStore.Audio.Albums.ALBUM, albumChange);
 				
 				       int rowsUpdated	=resolver.update(albumUri, mInserts, MediaStore.Audio.Albums._ID + " LIKE \"" + selectedAlbumId + "\"", null);
 				       Toast.makeText(getActivity(),"Album Name Changed",Toast.LENGTH_SHORT).show();
 					
 						
 						
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

    public static SecondFragment newInstance(String text) {

        SecondFragment f = new SecondFragment();
        return f;
    }
    
}
