package com.Project100Pi.themusicplayer;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.R.color;
import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Audio.GenresColumns;
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




public class FourthFragment extends Fragment  {
	 CardArrayMultiChoiceAdapter mCardArrayAdapter = null;
	  static HashMap<Long, String> idTogenreInfo =  new HashMap<Long,String>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
    	ArrayList<Card> cards = new ArrayList<Card>();
    	String genreName="";
    	Long genreId;
    	int noOfSongs=0;
    	String[] projection = new String[] {BaseColumns._ID, GenresColumns.NAME, };
    	String selection = null;
    	String[] selectionArgs = null;
    	String sortOrder = MediaStore.Audio.Genres.DEFAULT_SORT_ORDER;
    	Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sortOrder);
        final ArrayList<String> genresList = new ArrayList<String>();
        final ArrayList<Long> genresIdList = new ArrayList<Long>();
       while (cursor.moveToNext()) {
            genreName = cursor.getString(1);  
            genreId = cursor.getLong(0);
          /*  String[] proj2={MediaStore.Audio.Media.TITLE};
			 Uri uri = MediaStore.Audio.Genres.Members.getContentUri("external", genreId);
			Cursor cur =getActivity(). getApplicationContext().getContentResolver().query(uri, proj2, null,null,null);
		    noOfSongs=cur.getCount();*/
            genresList.add(genreName);
            genresIdList.add(genreId);
            
            Card card = new Card(this.getActivity());
            idTogenreInfo.put(genreId, noOfSongs+" Tracks");
            //Create a CardHeader
            CardHeader header = new CardHeader(this.getActivity());
      
            //Add Header to card
            card.addCardHeader(header);
            header.setTitle(genreName);
            card.setClickable(true);
            card.setId(genreId.toString());
            card.setInnerLayout(R.layout.genre_layout);
            card.setOnClickListener(new OnCardClickListener() {
				
				@Override
				public void onClick(Card card, View view) {
					// TODO Auto-generated method stub
					
					 Intent intent=new Intent(getActivity(),SongsUnder.class);
			            intent.putExtra("X","Genre");
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
       View v =inflater.inflate(R.layout.fourth_frag, container, false);
		/*
       Button shuffleFourthFrag = (Button) v.findViewById(R.id.shuffle_fourth_frag);
       shuffleFourthFrag.setOnClickListener(new OnClickListener() {
 		
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
   			    Long id = genresIdList.get(position);
   			    String noOfSongs = idTogenreInfo.get((id));
			LinearLayout cardLayout = (LinearLayout) v.findViewById(R.id.card_main_layout);
			if(position%2 == 0 ) {
				cardLayout.setBackgroundColor(Color.parseColor("#3D3D3D"));
			}else{
				cardLayout.setBackgroundColor(Color.parseColor("#484848"));
			}
   		    	TextView artistTitle = (TextView) v.findViewById(R.id.genre_total);
   		    	artistTitle.setText(noOfSongs);
   		    	
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
   			             final Long selectedGenreId=genresIdList.get(currPosition);
   			          //  Toast.makeText(getActivity(), songName +"and position is" + currPosition, Toast.LENGTH_LONG).show();
   			            
   	            	    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
   	            			
   	            			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
   							@Override
   	            			public boolean onMenuItemClick(MenuItem item) {
   								
   	            			             	
   	            			       switch(item.getItemId()){
   	            			    case R.id.cnt_menu_play:
	            			    	   
	            			    	  UtilFunctions.playSelectedSongsfromChoice(getActivity(), selectedGenreId, "genre",false);
	            			    	   
	            			    	   break;
   	            			 case R.id.cnt_menu_play_next:
           			    	  
	            			    	UtilFunctions.playSongsNextfromChoice(getActivity(), selectedGenreId, "genre");
	            			    	   
	            			    	   break;
	            			    	   
	            			       case R.id.cnt_menu_add_queue:
	            			    		UtilFunctions.addToQueuefromChoice(getActivity(), selectedGenreId, "genre");
	            			    	   break;
   	            			       case R.id.addToPlaylist:
   	            			    	   /*
   	            			    	Intent intent=new Intent(getActivity(),PlaylistSelectionMultiple.class);

   	            				         String[] proj2={MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE};
   	            				          Uri uri = MediaStore.Audio.Genres.Members.getContentUri("external", selectedGenreId);
   	            				         Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(uri, proj2, null,null,null);
	            			           // intent.putExtra("songName",songName);
	            			           
	            			           ArrayList<String> audioIdList = new ArrayList<String>();
	            			          
	         			        	 //  ArrayList<Uri> uris=new ArrayList<Uri>();
	         			        	   while(cursor.moveToNext()){
	         			        		   Long id=cursor.getLong(0);
	         			        		   audioIdList.add(id.toString()); 
	         			        	   }
	            			            intent.putExtra("selectedIdList", audioIdList);
	            			            */
	            			            startActivity(UtilFunctions.addSongstoPlaylist(getActivity(), selectedGenreId,"genre"));
   	            			    	  
   	            			    	 
   	            			    	   
   	            			    	
   	            			    	   break;
   	            			    	   
   	            			           case R.id.cnt_mnu_edit:
   	            			        	   
   	                                     // changeSongInfo(selectedId);
   	            			        			        	   
   	            			               //Toast.makeText(this, "Edit :" , Toast.LENGTH_SHORT).show();
   	            			        	   
   	            			               break;
   	            			           case R.id.cnt_mnu_delete:
   	            			        	UtilFunctions.deleteSinglePopUp(getContext(), getActivity(), selectedGenreId.toString(),"Are you sure you want to delete the selected Genre?"," 1 Genre Deleted","genre");
   	            			        	  
   	            			        	   break;
   	            			           case R.id.cnt_mnu_share:
   	            			        	   
   	            			        	startActivity(UtilFunctions.shareSingle(getActivity(), selectedGenreId, "genre"));
   	            			        		  
   	            			        
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
   	    		UtilFunctions.playSelectedSongfromMultiChoice(getActivity(), selIdList, "genre", true);
   	    		break;
   	    	case R.id.itemPlay:
   	    		
   	    		UtilFunctions.playSelectedSongfromMultiChoice(getActivity(), selIdList, "genre",false);
   	    	break;	
   	    	case R.id.itemPlayNext:
           		UtilFunctions.playSongsNextfromMultiChoice(getActivity(), selIdList, "genre");
           		break;
           	case R.id.itemAddQueue:
           		
           		UtilFunctions.AddToQueuefromMultiChoice(getActivity(), selIdList, "genre");
           		break;	
   	        case R.id.itemShare:
   	            startActivity(UtilFunctions.shareMultiple(getActivity(), selIdList, "genre"));	 
   		        break; 	 
   	        case R.id.itemDelete:   	        	
   	        	UtilFunctions.deleteMultiplePopUp(getContext(), getActivity(), selIdList, "Are you sure you want to delete the selected genres?","Genres deleted","genre");
   	        	break;
   	        case R.id.itemToPlaylist :
   	           // startActivity(UtilFunctions.addMultipletoPlaylist(getActivity(), selIdList, "genre"));
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
       NestedCardListView listView = (NestedCardListView) v.findViewById(R.id.fourthFragList);
       listView.setFastScrollEnabled(true);
       if (listView!=null){
           listView.setAdapter(mCardArrayAdapter);
           listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
          
       }
       
    
        return v;
    }

    public static FourthFragment newInstance(String text) {

        FourthFragment f = new FourthFragment();
        return f;
    }
    private final Cursor makeGenreSongCursor(final Long genreId) {
		final StringBuilder selection = new StringBuilder();
		selection.append(AudioColumns.IS_MUSIC + "=1");
		selection.append(" AND " + AudioColumns.TITLE + " != ''");
		selection.append(" AND " + BaseColumns._ID + "=" + genreId);
		return getActivity().getApplicationContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
		new String[] {
		/* 0 */ BaseColumns._ID,
		/* 1 */ AudioColumns.TITLE
		}, selection.toString(), null, MediaStore.Audio.Media.TRACK + ", " + MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		}
}