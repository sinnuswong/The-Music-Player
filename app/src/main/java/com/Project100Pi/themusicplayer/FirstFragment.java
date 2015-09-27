package com.Project100Pi.themusicplayer;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener;
import it.gmariotti.cardslib.library.view.base.CardViewWrapper;


public class FirstFragment extends Fragment {
	   ArrayList<String> titleList =null;
	   static ArrayList<String> idList = null;
	   static ArrayList<String> pathList=null;
	   static HashMap<Long, String> idToPath =new HashMap<Long, String>();
	   static HashMap<Long, String> idToName =new HashMap<Long, String>();
	   static HashMap< String,Long> nametoId =new HashMap<String,Long>();
	   static HashMap<Long, songInfo> idToSongInfo =  new HashMap<Long,songInfo>();
	   Long mGenreId = (long) 0;
	   MyIndexerAdapter mCardArrayAdapter = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
    	final ArrayList<Card> cards = new ArrayList<Card>();
    	String title="";
    	String path="";
    	String trackAlbum = "";
    	String trackArtist = "";
    	String trackDuration  = "";
        Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(
        	    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
        	    null, 
        	    null, 
        	    null, 
        	    MediaStore.Audio.Media.TITLE + " ASC");

 
        titleList = new ArrayList<String>();
     idList = new ArrayList<String>();
     pathList=new ArrayList<String>();
        Long id=null;
       while (cursor.moveToNext()) {
    	   Card card = new Card(this.getActivity());
    	   try{
             title= cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
             id=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
             path=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            trackAlbum = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            trackArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            trackDuration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)); 
            songInfo thisSong = new songInfo();
            thisSong.songName = title;
            thisSong.path = path;
            thisSong.albumName = trackAlbum;
            thisSong.artistName = trackArtist;
            thisSong.songDuration = UtilFunctions.convertSecondsToHMmSs(Long.parseLong(trackDuration));
            idList.add(id.toString());
            titleList.add(title);
            pathList.add(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
            idToSongInfo.put(id, thisSong);
             idToPath.put(id, path);
             idToName.put(id,title);
             nametoId.put(title, id);
             
             card.setId(id.toString());
            //Create a CardHeader
            CardHeader header = new CardHeader(this.getActivity());
      
            //Add Header to card
            card.addCardHeader(header);
            
            header.setTitle(title);
            
            card.setInnerLayout(R.layout.track_layout);
  
        card.setOnClickListener(new OnCardClickListener() {
				
				@Override
				public void onClick(Card card, View view) {
					// TODO Auto-generated method stub
				
			            UtilFunctions.playSelectedSongs(getActivity(), idList, cards.indexOf(card), false);
				}
			});
        
          card.setOnLongClickListener(new Card.OnLongCardClickListener() {
            @Override
            public boolean onLongClick(Card card, View view) {
                return mCardArrayAdapter.startActionMode(getActivity());

            }
       });
        
    	   }catch(Exception e){
    		   continue;
    	   } 
            cards.add(card);
          //  path.put(title,fullpath);
           
        }
		int height = cards.size() *35;
	///	Toast.makeText(getActivity().getApplicationContext(),height+"",Toast.LENGTH_LONG).show();
		//MainActivity.pager.getLayoutParams().height = cards.size() * 35;
   	//View v = inflater.inflate(R.layout.first_frag, container, false);
        View v =inflater.inflate(R.layout.first_frag, container, false);
		/*
       Button shuffleFirstFrag = (Button) v.findViewById(R.id.shufle_first_frag);
       shuffleFirstFrag.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			UtilFunctions.playSelectedSongs(getActivity(), idList, 0, true);
		}
	});
	*/

       final NestedCardListView listView = (NestedCardListView) v.findViewById(R.id.firstFragList);
       listView.setFastScrollEnabled(true);

	//	ViewCompat.setNestedScrollingEnabled(listView,true);

		/*
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			listView.setNestedScrollingEnabled(true);
		}
		*/
		mCardArrayAdapter = new MyIndexerAdapter(this.getActivity(),cards){
    
    	@Override
		 public View getView( final int position, View convertView, final ViewGroup parent) {
    		
    		
				
			    View v =super.getView(position, convertView, parent);
			    Long id = Long.parseLong(idList.get(position));
			    songInfo thisSong = idToSongInfo.get((id));
		    	//TextView albumTitle = (TextView) v.findViewById(R.id.album_title);
		    //	albumTitle.setText(thisSong.albumName);
			LinearLayout cardLayout = (LinearLayout) v.findViewById(R.id.card_main_layout);
			if(position%2 == 0 ) {
				cardLayout.setBackgroundColor(Color.parseColor("#3D3D3D"));
			}else{
				cardLayout.setBackgroundColor(Color.parseColor("#484848"));
			}

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
			//    Toast.makeText(getActivity()," position is " + position, Toast.LENGTH_LONG).show();
	            
			    
			   
			    button.setOnClickListener(new OnClickListener() {
	                public void onClick(View v) {
	                	
	                	PopupMenu popupMenu = new PopupMenu(getContext(),v); 
	            	    popupMenu.inflate(R.menu.long_click_actions);
	            	    View parentRow=(View) v.getParent();
			            NestedCardListView myListView=(NestedCardListView) parentRow.getParent();
			            final int currPosition=myListView.getPositionForView(parentRow);
			            final String songName=titleList.get(currPosition);
			             final Long selectedId=Long.parseLong(idList.get(currPosition));
			             final ArrayList<String> selectedIdList=new ArrayList<String>();
			             selectedIdList.add(selectedId.toString()); // created to send the ArrayList to UtilFunctions.deletePopUp
			             final ArrayList<String> selSongNameList = new ArrayList<String>();
			             selSongNameList.add(songName);
			            Toast.makeText(getActivity(), songName +"and position is" + currPosition, Toast.LENGTH_LONG).show();
			            
	            	    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
	            			
	            			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
							@Override
	            			public boolean onMenuItemClick(MenuItem item) {
								
	            			             	
	            			       switch(item.getItemId()){
	            			       case R.id.cnt_menu_play:
	            			    	   
	            			    	  UtilFunctions.playSelectedSongs(getActivity(),idList,currPosition,false);
	            			    	   
	            			    	   break;
	            			       case R.id.cnt_menu_play_next:
	            			    	  
	            			    	UtilFunctions.playSongsNext(getActivity(),selectedIdList);
	            			    	   
	            			    	   break;
	            			    	   
	            			       case R.id.cnt_menu_add_queue:
	            			    		UtilFunctions.addToQueueSongs(getActivity(), selectedIdList);
	            			    	   break;
	            			       case R.id.addToPlaylist:
	            			    	  
	            			    	 //   Adapter menuInfo=(AdapterContextMenuInfo) item.getMenuInfo();
	            			    	  //  int pos=menuInfo.position;
	            			            
	            			          //  int posi=mCardArrayAdapter.getPosition(this);
	            			           // Toast.makeText(getActivity(),"First Visible position is " + firstVisiblePos, Toast.LENGTH_LONG).show();
	            			          //  Toast.makeText(getActivity()," position is " + position, Toast.LENGTH_LONG).show();
	            			           
	            			            
	            			          
	            			           Intent intent=new Intent(getActivity(),PlaylistSelection.class);
	            			            intent.putExtra("songName",songName);
	            			            intent.putExtra("selectedId",selectedId);
	            			            startActivity(intent);
	            			          /*
	            			            selsongname.add(songname);
	            			            String songpath=(String)allsongs.contents.get(songname);
	            			            selsongpath.add(songpath);
	            			           Toast.makeText(getActivity(), selsongname.get(0) +' '+selsongpath.get(0), Toast.LENGTH_LONG).show();
	            			            
		            			          //  intent.putExtra("art",(Parcelable)allsongs);
		            			        intent.putExtra("songname",selsongname);
		            			    	intent.putExtra("songpath",selsongpath);
		            			        startActivity(intent);*/
	            			    	   
	            			    	
	            			    	   break;
	            			           case R.id.cnt_mnu_edit:
	            			        	   
                                         UtilFunctions.changeSongInfo(getActivity(),selectedId);
	            			         	  
	            			        	 
	            			        	   
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
	            			                    	   ContentResolver resolver=getActivity().getApplicationContext().getContentResolver();
	            			                    	   int rowsDeleted	=resolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media.TITLE + " LIKE \"" + songName + "\"", null);
	            			                    	   File file = new File(idToPath.get(selectedId));
	            			                    	   boolean deleted = file.delete();
	            			                    	   Toast.makeText(getActivity(),"File Deleted "+deleted,Toast.LENGTH_SHORT).show();
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
	            			        	     UtilFunctions.deletePopUp(getContext(), getActivity(), selectedIdList, "Are you sure you want to delete the selected song?","Song deleted");
	            			        		
	            	 						//ContentValues mInserts = new ContentValues();
	            	 					 
	            	 						
	            	 						//String songName=idToName.get(id);
	            	 					//	Toast.makeText(getActivity(),"songName  is "+songName,Toast.LENGTH_SHORT).show();

	            	 						//mInserts.put(MediaStore.Audio.Media.TITLE, value);
	            	 						
	            	 				     
	            			        	   
	            			        	   
	            			        	   break;
	            			           case R.id.cnt_mnu_share:
	            			        	   Intent sharingIntent=new Intent(Intent.ACTION_SEND);
	            			        	   String path=pathList.get(currPosition);
	            			        	   path="file://"+path;
	            			        	   Toast.makeText(getActivity(),"Path is "+path,Toast.LENGTH_SHORT).show();
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
        	case R.id.itemShuffle:
        		UtilFunctions.playSelectedSongs(getActivity(), selIdList, 0, true);
        		break;
        	case R.id.itemPlay:
        		/*
        		 Intent intent=new Intent(getActivity(),PlayActivity.class);
				 	intent.putExtra("do", "Play");
		           intent.putExtra("position", 0);
		            intent.putStringArrayListExtra("playingList", selsongNameList);
		            startActivity(intent); 
		            */
        		UtilFunctions.playSelectedSongs(getActivity(),selIdList,0,false);
        		break;
        		
        	case R.id.itemPlayNext:
        		UtilFunctions.playSongsNext(getActivity(), selIdList);
		  
        		
        		break;
        	case R.id.itemAddQueue:
        		
        		UtilFunctions.addToQueueSongs(getActivity(), selIdList);
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
            	
            	UtilFunctions.deletePopUp(getContext(), getActivity(), selIdList, "Are you sure you want to delete the selected Songs?","Songs deleted");
            	break;
            case R.id.itemToPlaylist :
            
            	 Intent intent=new Intent(getActivity(),PlayListSelectionTest.class);
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
       if (listView!=null){
           listView.setAdapter(mCardArrayAdapter);
           listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
       }
       
       listView.setOnItemClickListener(new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			Toast.makeText(getActivity(), "Hi there", Toast.LENGTH_LONG).show();
			// TODO Auto-generated method stub
			
		}
    	   
	});


		Toast.makeText(getActivity().getApplicationContext(),"First Fragment finished",Toast.LENGTH_LONG).show();
       
  
    	return v;
    	
    }


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


	}

	public static FirstFragment newInstance(String text) {

        FirstFragment f = new FirstFragment();
        return f;
    }
}

