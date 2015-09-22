
package com.Project100Pi.themusicplayer;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardArrayMultiChoiceAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.base.CardViewWrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

import android.graphics.Color;
import android.net.Uri;
import android.R.integer;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FifthFragment extends Fragment {
	Button button;
	ArrayList<String> playlistnames=null;
	ArrayList<Long> playlistIdLists=null;
	ArrayList<Card> cards = null;
	View v = null;
	CardArrayMultiChoiceAdapter mCardArrayAdapter;

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	
	    Long playListId;
	
        final String[] PROJECTION_PLAYLIST = new String[] {
        	    MediaStore.Audio.Playlists._ID,
        	    MediaStore.Audio.Playlists.NAME,
        	    MediaStore.Audio.Playlists.DATA
        	};
        
        Cursor cursor=getActivity().getApplicationContext().getContentResolver().query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, PROJECTION_PLAYLIST, null, null,MediaStore.Audio.Playlists.DATE_MODIFIED);
        playlistnames=new ArrayList<String>();
        playlistIdLists=new ArrayList<Long>();
        while (cursor.moveToNext()) {
        	playlistnames.add(cursor.getString(1));
        	playlistIdLists.add(cursor.getLong(0));
        }
      
     
	v = inflater.inflate(R.layout.fifth_frag, container, false);
	
    /*
    button = (Button) v.findViewById(R.id.more_button);
	
  
	button.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View view) {
			showInputDialog();
		}
	});
	*/
	  populateCards();
    
    return v;
}

   private void showInputDialog() {
	// get prompts.xml view
		LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
		View promptView = layoutInflater.inflate(R.layout.dialog_box, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
		alertDialogBuilder.setView(promptView);

		final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
		// setup a dialog window
		alertDialogBuilder.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						 
						
					    createNewPlayList(editText.getText().toString());
					    populateCards();
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
   
   public static void addToPlaylist(ContentResolver resolver, int audioId) {

       String[] cols = new String[] {
               "count(*)"
       };
       Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", audioId);
       Cursor cur = resolver.query(uri, cols, null, null, null);
       cur.moveToFirst();
       final int base = cur.getInt(0);
       cur.close();
       ContentValues values = new ContentValues();
       values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, Integer.valueOf(base + audioId));
       values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audioId);
       resolver.insert(uri, values);
   }

    public  void createNewPlayList(String name)
    {
    	  ContentValues mInserts = new ContentValues();
          mInserts.put(MediaStore.Audio.Playlists.NAME, name);
          mInserts.put(MediaStore.Audio.Playlists.DATE_ADDED, System.currentTimeMillis());
          mInserts.put(MediaStore.Audio.Playlists.DATE_MODIFIED, System.currentTimeMillis());
          ContentResolver mContentResolver=getActivity().getApplicationContext().getContentResolver();
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
              playlistnames.add(name);
              playlistIdLists.add((long) mPlaylistId);

          }
    }
public static FifthFragment newInstance(String text) {

    FifthFragment f = new FifthFragment();
    return f;
  }

public void populateCards(){
	
	cards = new ArrayList<Card>();
	int size = playlistnames.size();
	for(int i = 0;i<size;i++){
		
		 Card card = new Card(this.getActivity());

         //Create a CardHeader
         CardHeader header = new CardHeader(this.getActivity());
   
         //Add Header to card
         card.addCardHeader(header);
         header.setTitle( playlistnames.get(i));
         card.setTitle( playlistnames.get(i));
         card.setClickable(true);
         card.setId(playlistIdLists.get(i).toString());
         card.setOnClickListener(new OnCardClickListener() {

			@Override
			public void onClick(Card card, View arg1) {
				 Long playlistId=Long.parseLong(card.getId());
			       Cursor cursor = null;
			       Intent intent=new Intent(getActivity(),PlaylistUnder.class);
		            intent.putExtra("X","PlayList");
		            intent.putExtra("id", playlistId);
		            startActivity(intent);   
			}

				 // Have to write query to get the playlist songs
				
				// TODO Auto-generated method stub
				
			
         
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
				    
				    	//CardListView myListView=(CardListView)v.getParent();
				          // final int currPos=myListView.getPositionForView(v);
				            //final Long selectedId=idList.get(currPos);
				    	
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
				            final String songName=playlistnames.get(currPosition);
				             final Long selectedId=playlistIdLists.get(currPosition);
				            Toast.makeText(getActivity(), songName +"and position is" + currPosition, Toast.LENGTH_LONG).show();
				            
		            	    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
		            			
		            			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
								@Override
		            			public boolean onMenuItemClick(MenuItem item) {
									
		            			             	
		            			       switch(item.getItemId()){
		            			       
		            			       case R.id.cnt_menu_play:
    	            			    	   
 		            			    	  UtilFunctions.playSelectedSongsfromChoice(getActivity(), selectedId, "playlist",false);
 		            			    	   
 		            			    	   break;
		            			       case R.id.cnt_menu_play_next:
		                			    	  
			            			    	UtilFunctions.playSongsNextfromChoice(getActivity(), selectedId, "playlist");
			            			    	   
			            			    	   break;
			            			    	   
			            			       case R.id.cnt_menu_add_queue:
			            			    		UtilFunctions.addToQueuefromChoice(getActivity(), selectedId, "playlist");
			            			    	   break;
		            			           case R.id.cnt_mnu_edit:
		            			        	   
	                                       
		            			        		LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
		            			        		View promptView = layoutInflater.inflate(R.layout.dialog_box, null);
		            			        		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
		            			        		alertDialogBuilder.setView(promptView);
		            			        	//	Toast.makeText(activity,"Id is "+id,Toast.LENGTH_SHORT).show();
		            			        		TextView textView = (TextView) promptView.findViewById(R.id.textView);
		            			        		textView.setText("Edit PlayList Name");
		            			        		final EditText editTitleText = (EditText) promptView.findViewById(R.id.edittext);
		            			        		editTitleText.setText(songName);
		            			        	
		            			        		//final EditText editAlbumText=(EditText) promptView.findViewById(R.id.edittext);
		            			        		// setup a dialog window
		            			        		alertDialogBuilder.setCancelable(false)
		            			        				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		            			        					public void onClick(DialogInterface dialog, int id) {
		            			        						
		            			        						ContentResolver thisContentResolver = getActivity().getContentResolver();
		            			        						UtilFunctions.renamePlaylist(thisContentResolver, selectedId, editTitleText.getText().toString());
		            			    				            Toast.makeText(getActivity(), "PlayList Renamed", Toast.LENGTH_LONG).show();

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
		            			        	 
		            			        	   
		            			               //Toast.makeText(this, "Edit :" , Toast.LENGTH_SHORT).show();
		            			        	   
		            			               break;
		            			           case R.id.cnt_mnu_delete:
		            			        	  
		            			        		AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
		            			        		builder.setTitle("Confirm Delete");
		            			        		builder.setMessage("Are you sure you want to delete the Playlist?");
		            			        		builder.setCancelable(true);
		            			        		builder.setPositiveButton("Yes", 
		            			                new DialogInterface.OnClickListener() {
		            			                    public void onClick(DialogInterface dialog, int thisid) {
		            			                    	   ContentResolver resolver=getActivity().getApplicationContext().getContentResolver();
		            			                    	   deletePlaylist(resolver, selectedId);
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
		            			        		
		            			        		
		            	 						//ContentValues mInserts = new ContentValues();
		            	 					 
		            	 						
		            	 						//String songName=idToName.get(id);
		            	 					//	Toast.makeText(getActivity(),"songName  is "+songName,Toast.LENGTH_SHORT).show();

		            	 						//mInserts.put(MediaStore.Audio.Media.TITLE, value);
		            	 						
		            	 				     
		            			        	   
		            			        	   
		            			        	   break;
		            			           case R.id.cnt_mnu_share:
		            			        	  
		            			        	   startActivity(UtilFunctions.shareSingle(getActivity(), selectedId, "playlist"));  
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
	        	switch(item.getItemId()){
	        	case R.id.itemShuffle:
       	    		UtilFunctions.playSelectedSongfromMultiChoice(getActivity(), selIdList, "playlist", true);
       	    		break;
	        	
	        		case R.id.itemPlay:
       	    		
       	    		UtilFunctions.playSelectedSongfromMultiChoice(getActivity(), selIdList, "playlist",false);
       	    	break;	
	        		case R.id.itemPlayNext:
	               		UtilFunctions.playSongsNextfromMultiChoice(getActivity(), selIdList, "playlist");
	               		break;
	               	case R.id.itemAddQueue:
	               		
	               		UtilFunctions.AddToQueuefromMultiChoice(getActivity(), selIdList, "playlist");
	               		break;	
	            case R.id.itemShare:
	            	   
			        	 startActivity(UtilFunctions.shareMultiple(getActivity(), selIdList,"playlist"));	  
			        	 break;
			        	 
	            case R.id.itemDelete:
	            	/*
	            	AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
	        		builder.setTitle("Confirm Delete");
	        		builder.setMessage("Are you sure you want to delete the selected songs?");
	        		builder.setCancelable(true);
	        		builder.setPositiveButton("Yes", 
	                new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int id) {
	                    	   ContentResolver resolver=getActivity().getApplicationContext().getContentResolver();
	                    	   for(int i=0;i<size;i++)
	     	        		  {
	                    	  
	                    	   String selectedId=selIdList.get(i);
	                    	   String songName=idToName.get(Long.parseLong(selectedId));
	                    	   int rowsDeleted	=resolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media._ID + " LIKE \"" + selectedId + "\"", null);
	                    	   File file = new File(idToPath.get(Long.parseLong(selectedId)));
	                    	   boolean deleted = file.delete();
	     	        		  }
	                    	   Toast.makeText(getActivity(), size+ " Songs deleted ",Toast.LENGTH_SHORT).show();
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
	    
     NestedCardListView listView = (NestedCardListView) v.findViewById(R.id.fifthFragList);
     listView.setFastScrollEnabled(true);
     if (listView!=null){
         listView.setAdapter(mCardArrayAdapter);
         listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
         
     }
}
public static void deletePlaylist(ContentResolver resolver, long id)
{
Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, id);
resolver.delete(uri, null, null);
}

public  Cursor makePlaylistSongCursor(long id)
{
	  String[] projection1 = {
		      MediaStore.Audio.Playlists.Members.AUDIO_ID,
              MediaStore.Audio.Playlists.Members.TITLE,
              MediaStore.Audio.Playlists.Members.PLAY_ORDER,
              MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER
              
           };
       Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", id);
          Cursor cursor =getActivity().getApplicationContext().getContentResolver().query(
              uri,
              projection1,
              MediaStore.Audio.Playlists.Members.PLAYLIST_ID+ " = "+id+"",
              null,
              MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER );
          
          return cursor;
}
@Override
public void onStop(){	
	super.onStop();
}
}
