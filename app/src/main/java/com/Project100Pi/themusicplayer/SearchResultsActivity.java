package com.Project100Pi.themusicplayer;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener;
import it.gmariotti.cardslib.library.prototypes.SectionedCardAdapter;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.prototypes.CardSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AlbumColumns;
import android.provider.MediaStore.Audio.ArtistColumns;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.PopupMenu;

import android.widget.TextView;
import android.widget.Toast;
import android.widget.SearchView.OnQueryTextListener;

public class SearchResultsActivity extends AppCompatActivity {

	ArrayList<String> titleRes = null;
	ArrayList<String> albumRes = null;
	ArrayList<String> artistRes = null;
	ArrayList<String> albumIdRes = null;
	ArrayList<String> artistIdRes = null;
	ArrayList<String> songIdRes = null;
	ArrayList<String> fullSearchRes = null;
	ArrayList<Card> cards = null;
	 HashMap<Long, AlbumInfo> idToalbumInfo = null;
	 HashMap<Long, ArtistInfo> idToartistInfo = null;
	 static ArrayList<String> pathList=null;
	   static HashMap<Long, songInfo> idToSongInfo =  null;
	Toolbar mToolbar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		//setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
   idToalbumInfo =  new HashMap<Long,AlbumInfo>();
   idToartistInfo =  new HashMap<Long,ArtistInfo>();
   pathList = new ArrayList<String>();
   idToSongInfo = new HashMap<Long,songInfo>();
        titleRes = new ArrayList<String>();
    	 albumRes = new ArrayList<String>();
    	 artistRes = new ArrayList<String>();
    	 songIdRes = new ArrayList<String>();
       albumIdRes =new ArrayList<String>();
    	artistIdRes =new ArrayList<String>();
    	fullSearchRes = new ArrayList<String>();
    	 cards = new ArrayList<Card>();
    }
    

    
    public class MySectionedCardAdapter extends SectionedCardAdapter {

       
        public MySectionedCardAdapter(Context context, CardArrayAdapter cardArrayAdapter) {
            super(context, R.layout.carddemo_gplay_section_layout,cardArrayAdapter);
        }


       
        @Override
        protected View getSectionView(int position, View view, ViewGroup parent) {

            //Override this method to customize your section's view

            //Get the section
            CardSection section = (CardSection) getCardSections().get(position);
            

            if (section != null ) {
               // Set the title
                TextView title = (TextView) view.findViewById(R.id.carddemo_section_gplay_title);
               if (title != null)
                   title.setText(section.getTitle());

             
            }

            return view;
        }
    }
    
    public void populateSearchList(){
    	
    	cards.clear();
    	int size = fullSearchRes.size();
        for(int j=0;j<size;j++){
     	   
        	Card card = new Card(this);
            
            //Create a CardHeader
            CardHeader header = new CardHeader(this);
            card.setId(""+j);
            card.addCardHeader(header);
            header.setTitle(fullSearchRes.get(j));
            
            if(j >= 0  && j < albumRes.size()){
            	card.setInnerLayout(R.layout.album_layout);
            	 
            	 card.setOnClickListener(new OnCardClickListener() {
            		 @Override
     				public void onClick(Card card, View view) {
     					// TODO Auto-generated method stub
            			 int clickPos = Integer.parseInt(card.getId());
            			 Long id = Long.parseLong(albumIdRes.get(clickPos));
 						Intent intent=new Intent(SearchResultsActivity.this,SongsUnderTest.class);
 		            intent.putExtra("X","Album");
 		            intent.putExtra("id", id);
						 intent.putExtra("title",fullSearchRes.get(clickPos));
 		            startActivity(intent);  
 						
            	 }
            	
            });
            }
            else if(j >= albumRes.size()  && j < albumRes.size()+artistRes.size()){ 
            	
            	card.setInnerLayout(R.layout.artist_layout);
            	 card.setOnClickListener(new OnCardClickListener() {
            		 @Override
     				public void onClick(Card card, View view) {
     					// TODO Auto-generated method stub
            			 int clickPos = Integer.parseInt(card.getId());
            				Long id = Long.parseLong(artistIdRes.get(clickPos-albumRes.size()));
    						Intent intent=new Intent(SearchResultsActivity.this,SongsUnderTest.class);
    		            intent.putExtra("X","Artist");
    		            intent.putExtra("id", id);
						 intent.putExtra("title",fullSearchRes.get(clickPos));
    		            startActivity(intent);  
            	 }
            	
            });
            }
            else if(j >= albumRes.size()+artistRes.size() && j < albumRes.size()+titleRes.size()+artistRes.size()){
            	card.setInnerLayout(R.layout.track_layout);
            	 card.setOnClickListener(new OnCardClickListener() {
            		 @Override
     				public void onClick(Card card, View view) {
     					// TODO Auto-generated method stub

            			 int clickPos = Integer.parseInt(card.getId());
            			 ArrayList<String> idList = new ArrayList<String>();
     					idList.add(songIdRes.get(clickPos-albumRes.size()-artistRes.size()));
     					 Intent intent=new Intent(SearchResultsActivity.this,PlayActivity.class);
     					 	intent.putExtra("do", "Play");
     			           intent.putExtra("position", 0);
     			            intent.putStringArrayListExtra("playingList", idList);
     			            startActivity(intent);   
     					
     						
            	 }
            	
            });
            }
            
          
            //Add Header to card
           
            cards.add(card);
     	   
        }
        
       
    
    CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(this,cards){
		  @Override
			 public View getView( int position, View convertView, final ViewGroup parent) {				
				    View v =super.getView(position, convertView, parent);
				    if(albumRes.size()>0 && position >= 0  && position < albumRes.size()){
				    final Long albumId = Long.parseLong(albumIdRes.get(position));
    			    final AlbumInfo thisSong = idToalbumInfo.get((albumId));
    			    
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
    			//    Toast.makeText(SearchResultActivity.this," position is " + position, Toast.LENGTH_LONG).show();
    	            
    			    
    			   
    			    button.setOnClickListener(new OnClickListener() {
    	                public void onClick(View v) {
    	                	
    	                	PopupMenu popupMenu = new PopupMenu(getContext(),v); 
    	            	    popupMenu.inflate(R.menu.long_click_actions);
    	            	    View parentRow=(View) v.getParent();
    	            	    CardListView myView ;
    	            	     int currPosition = 0;
    	            	   
    			            myView=(CardListView) parentRow.getParent();
    			            currPosition=((AdapterView<ListAdapter>) myView).getPositionForView(parentRow)-1; // Here -1 becasue of the section header
    	            	   
    			           
    			           // final String songName=titleList.get(currPosition);
    			             final Long selectedAlbumId=Long.parseLong(albumIdRes.get(currPosition));
    			             final String selectedAlbumName = cards.get(currPosition).getCardHeader().getTitle();
    			          //  Toast.makeText(SearchResultActivity.this, songName +"and position is" + currPosition, Toast.LENGTH_LONG).show();
    			            
    	            	    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
    	            			
    	            			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    							@Override
    	            			public boolean onMenuItemClick(MenuItem item) {
    								
    	            			             	
    	            			       switch(item.getItemId()){
    	            			       case R.id.cnt_menu_play:
    	            			    	   
    		            			    	  UtilFunctions.playSelectedSongsfromChoice(SearchResultsActivity.this, selectedAlbumId, "album",false);
    		            			    	   
    		            			    	   break;
    		            			    	   
    	            			       case R.id.cnt_menu_play_next:
    		            			    	  
    		            			    	UtilFunctions.playSongsNextfromChoice(SearchResultsActivity.this, selectedAlbumId, "album");
    		            			    	   
    		            			    	   break;
    		            			    	   
    		            			       case R.id.cnt_menu_add_queue:
    		            			    		UtilFunctions.addToQueuefromChoice(SearchResultsActivity.this,selectedAlbumId, "album");
    		            			    	   break; 	   
    	            			       case R.id.addToPlaylist:
    	            			    	  
    	            			    	   startActivity(UtilFunctions.addSongstoPlaylist(SearchResultsActivity.this,selectedAlbumId,"album"));
    	            			    	
    	            			    	   break;
    	            			           case R.id.cnt_mnu_edit:
    	            			        	   
    	            			        	  //editAlbumInfo(selectedAlbumName, selectedAlbumId); // NOT WORKING
    	            			        	   
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
    	            			                   	   ContentResolver resolver=SearchResultActivity.this.getApplicationContext().getContentResolver();
    	            			                   	 Cursor cursor=makeAlbumSongCursor(selectedAlbumId);
    	            			                     while(cursor.moveToNext()){
    	                   			        		   Long songId=cursor.getLong(0);
    	            			                   	  
    	            			                   	 
    	            			                   	   String songName=FirstFragment.idToName.get(songId);
    	            			                   	   int rowsDeleted	=resolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media._ID + " LIKE \"" + songId + "\"", null);
    	            			                   	   File file = new File(FirstFragment.idToPath.get(songId));
    	            			                   	   boolean deleted = file.delete();
    	            			    	        		  }
    	            			                   	   Toast.makeText(SearchResultActivity.this,  " Album deleted ",Toast.LENGTH_SHORT).show();
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
    	            			        	   UtilFunctions.deleteSinglePopUp(getContext(), SearchResultsActivity.this, selectedAlbumId.toString(),"Are you sure you want to delete the selected Album?"," 1 Album Deleted","album");
    	            			        	  
    	            			        	   
    	            			        	   break;
    	            			           case R.id.cnt_mnu_share:
    	            			        	   

    	            			        	   startActivity(UtilFunctions.shareSingle(SearchResultsActivity.this, selectedAlbumId, "album"));	  
    	            			        
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
				}else if(artistRes.size()>0 && position >= albumRes.size()  && (position < albumRes.size()+artistRes.size())){
					position = position-(albumRes.size());
					Long id = Long.parseLong(artistIdRes.get(position));
    			    ArtistInfo thisSong = idToartistInfo.get((id));
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
    			//    Toast.makeText(SearchResultsActivity.this," position is " + position, Toast.LENGTH_LONG).show();
    	            
    			    
    			   
    			    button.setOnClickListener(new OnClickListener() {
    	                public void onClick(View v) {
    	                	
    	                	PopupMenu popupMenu = new PopupMenu(getContext(),v); 
    	            	    popupMenu.inflate(R.menu.long_click_actions);
    	            	    View parentRow=(View) v.getParent();
    			            CardListView myListView=(CardListView) parentRow.getParent();
    			            int currPosition;
    			            if(albumRes.size()>0){
    			         currPosition=myListView.getPositionForView(parentRow)-2-(albumRes.size());
    			            } else{
    			            	 currPosition=myListView.getPositionForView(parentRow)-1-(albumRes.size());
    			            }
    			           // final String songName=titleList.get(currPosition);
    			             final Long selectedArtistId=Long.parseLong(artistIdRes.get(currPosition));
    			             final Cursor cursor;
    			          //  Toast.makeText(SearchResultsActivity.this, songName +"and position is" + currPosition, Toast.LENGTH_LONG).show();
    			            
    	            	    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
    	            			
    	            			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    							@Override
    	            			public boolean onMenuItemClick(MenuItem item) {
    								
    	            				   Cursor cursor=null;    	
    	            			       switch(item.getItemId()){
    	            			       case R.id.cnt_menu_play:
    	            			    	   
 		            			    	  UtilFunctions.playSelectedSongsfromChoice(SearchResultsActivity.this, selectedArtistId, "artist",false);
 		            			    	   
 		            			    	   break;
    	            			       case R.id.cnt_menu_play_next:
    		            			    	  
   		            			    	UtilFunctions.playSongsNextfromChoice(SearchResultsActivity.this, selectedArtistId, "artist");
   		            			    	   
   		            			    	   break;
   		            			    	   
   		            			       case R.id.cnt_menu_add_queue:
   		            			    		UtilFunctions.addToQueuefromChoice(SearchResultsActivity.this,selectedArtistId, "artist");
   		            			    	   break;
    	            			       case R.id.addToPlaylist:
    	            			    
    	            			    	    startActivity(UtilFunctions.addSongstoPlaylist(SearchResultsActivity.this,selectedArtistId,"artist"));
    	            			    	   
    	            			    	
    	            			    	   break;
    	            			           case R.id.cnt_mnu_edit:
    	            			        	
    	            			               break;
    	            			           case R.id.cnt_mnu_delete:
    	            			        	
    	            			        	   UtilFunctions.deleteSinglePopUp(getContext(), SearchResultsActivity.this, selectedArtistId.toString(),"Are you sure you want to delete the selected Artist?"," 1 Artist Deleted","artist");
    	            			        	   break;
    	            			           case R.id.cnt_mnu_share:
    	            			        	   
    	            			        	  
    		            			        	 startActivity(UtilFunctions.shareSingle(SearchResultsActivity.this, selectedArtistId, "artist"));
    	            			        		  
    	            			        
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
				}else if(titleRes.size()>0 && position >= albumRes.size()+artistRes.size() && position < albumRes.size()+titleRes.size()+artistRes.size()){
					position = position-(albumRes.size()+artistRes.size());
					Long id = Long.parseLong(songIdRes.get(position));
				    songInfo thisSong = idToSongInfo.get((id));
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
				//    Toast.makeText(SearchResultsActivity.this," position is " + position, Toast.LENGTH_LONG).show();
		            
				    
				   
				    button.setOnClickListener(new OnClickListener() {
		                public void onClick(View v) {
		                	
		                	PopupMenu popupMenu = new PopupMenu(getContext(),v); 
		            	    popupMenu.inflate(R.menu.long_click_actions);
		            	    final View parentRow=(View) v.getParent();
				            
				            
				            
		            	    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
		            			
		            			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
								@Override
		            			public boolean onMenuItemClick(MenuItem item) {
		            				CardListView myListView=(CardListView) parentRow.getParent();
		            				int currPosition=0;
						            if(artistRes.size()>0 || albumRes.size()>0){
						       currPosition=myListView.getPositionForView(parentRow)-2-(albumRes.size()+artistRes.size());
						       if(artistRes.size()>0 && albumRes.size()>0){
						    	   currPosition -=1;
						       }
						            }else{
						            	currPosition=myListView.getPositionForView(parentRow)-1-(albumRes.size()+artistRes.size());
						            }
						            final String songName=titleRes.get(currPosition);
						             final Long selectedId=Long.parseLong(songIdRes.get(currPosition));
						             final ArrayList<String> selectedIdList=new ArrayList<String>();
						             selectedIdList.add(selectedId.toString()); // created to send the ArrayList to UtilFunctions.deletePopUp
						             final ArrayList<String> selSongNameList = new ArrayList<String>();
						             selSongNameList.add(songName);
		            			             	
		            			       switch(item.getItemId()){
		            			       case R.id.cnt_menu_play:
		            			    	   
		            			    	  UtilFunctions.playSelectedSongs(SearchResultsActivity.this,songIdRes,currPosition,false);
		            			    	   
		            			    	   break;
		            			       case R.id.cnt_menu_play_next:
		            			    	  
		            			    	UtilFunctions.playSongsNext(SearchResultsActivity.this,selectedIdList);
		            			    	   
		            			    	   break;
		            			    	   
		            			       case R.id.cnt_menu_add_queue:
		            			    		UtilFunctions.addToQueueSongs(SearchResultsActivity.this, selectedIdList);
		            			    	   break;
		            			       case R.id.addToPlaylist:
		            			    	  
		            			    
		            			           Intent intent=new Intent(SearchResultsActivity.this,PlayListSelectionTest.class);
		            			            intent.putExtra("songName",songName);
										   intent.putExtra("selectedIdList",selectedIdList);
		            			            startActivity(intent);
		            			    	
		            			    	   break;
		            			           case R.id.cnt_mnu_edit:
		            			        	   
	                                         UtilFunctions.changeSongInfo(SearchResultsActivity.this,selectedId);
		            			         	  
		            			        	 
		            			        	   
		            			              
		            			               break;
		            			           case R.id.cnt_mnu_delete:
		            			        	 
		            			        	     UtilFunctions.deletePopUp(getContext(), SearchResultsActivity.this, selectedIdList, "Are you sure you want to delete the selected song?","Song deleted");
		            			        	
		            			        	   
		            			        	   break;
		            			           case R.id.cnt_mnu_share:
		            			        	   Intent sharingIntent=new Intent(Intent.ACTION_SEND);
		            			        	   String path=pathList.get(currPosition);
		            			        	   path="file://"+path;
		            			        	   Toast.makeText(SearchResultsActivity.this,"Path is "+path,Toast.LENGTH_SHORT).show();
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
					return v;
		  }
	  };

    // Define your sections
    List<CardSection> sections =  new ArrayList<CardSection>();
    if(albumIdRes.size() != 0){
    sections.add(new CardSection(0,"ALBUMS"));
    }
    if(artistRes.size() != 0){
    sections.add(new CardSection(albumRes.size(),"ARTISTS"));
    }
    if(titleRes.size() !=0){
    sections.add(new CardSection(albumRes.size()+artistRes.size(),"TRACKS"));
    }
    CardSection[] dummy = new CardSection[sections.size()];

    //Define your Sectioned adapter
    MySectionedCardAdapter mAdapter = new MySectionedCardAdapter(this, mCardArrayAdapter);
    mAdapter.setCardSections(sections.toArray(dummy));

    CardListView listView = (CardListView) this.findViewById(R.id.searchList);
    if (listView!=null){
        //Use the external adapter.
        listView.setExternalAdapter(mAdapter,mCardArrayAdapter);
    }
    
    
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchview_layout, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
         SearchView searchView =
				 (SearchView) menu.findItem(R.id.search).getActionView();
         searchView.setIconified(false);
         searchView.setSearchableInfo(
                 searchManager.getSearchableInfo(getComponentName()));
         searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

           

			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				//  loadHistory(query);
				//Toast.makeText(getBaseContext(), query,
						//Toast.LENGTH_SHORT).show();
			if(query.length() >1)	{
            doQuery(query);
			}
		           
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				//Toast.makeText(getBaseContext(), newText,
					//	Toast.LENGTH_SHORT).show();
				if(newText.length() > 1){
					doQuery(newText);
				}
				return true;
			} 

         });

        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		int id = item.getItemId();
		switch (id) {

		case android.R.id.home:
		onBackPressed();
		return true;

		default:
		break;
		}
		return true;
	}

	public void doQuery(String query) {
    	
    	titleRes.clear();
    	albumIdRes.clear();
    	albumRes.clear();
    	artistIdRes.clear();
    	songIdRes.clear();
    	artistRes.clear();
    	fullSearchRes.clear();
    	
		 String projection[] = {
					MediaStore.Audio.Media.TITLE,
					MediaStore.Audio.Media.ALBUM,
					MediaStore.Audio.Media.ARTIST,
					MediaStore.Audio.Media._ID,
					MediaStore.Audio.Media.DURATION,
					MediaStore.Audio.Media.DATA
					
};
            Cursor cursor = getContentResolver().query(
	        	    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
	        	    projection, 
	        	    MediaStore.Audio.Media.TITLE + " LIKE \"%" + query + "%\" OR " + MediaStore.Audio.Media.ARTIST + " LIKE \"%" + query + "%\" OR "+ MediaStore.Audio.Media.ALBUM + " LIKE \"%" + query + "%\"", 
	        	    null, 
	        	    MediaStore.Audio.Media.TITLE + " ASC");
            String title = null;
            String thisId,path;
 
            while (cursor.moveToNext()) {
                 title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                 path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                 thisId = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                 songIdRes.add(thisId);
                 titleRes.add(title);
                 pathList.add(path);
                songInfo thisSong = new songInfo();
                thisSong.songName = title;
                thisSong.path = path;
                thisSong.albumName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                thisSong.artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                thisSong.songDuration = UtilFunctions.convertSecondsToHMmSs(Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));
                idToSongInfo.put(Long.parseLong(thisId), thisSong);
                 //Toast.makeText(getApplicationContext(),title, Toast.LENGTH_LONG).show();
               
            }
            
            cursor = getContentResolver().query(
           MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, 
     	   new String[] {BaseColumns._ID, AlbumColumns.ALBUM,AlbumColumns.ARTIST,AlbumColumns.NUMBER_OF_SONGS }, 
    	    AlbumColumns.ALBUM + " LIKE \"%" + query + "%\"", 
    	    null, 
    	    AlbumColumns.ALBUM + " ASC");
   // String title = null;
            Long id;
           
       while (cursor.moveToNext()) {
         title = cursor.getString(cursor.getColumnIndex(AlbumColumns.ALBUM));
         thisId = cursor.getString(cursor.getColumnIndex(BaseColumns._ID));
         albumIdRes.add(thisId);
         albumRes.add(title);
         AlbumInfo thisSong = new AlbumInfo();
         thisSong.albumName = title;
         thisSong.albumId = Long.parseLong(thisId);
         thisSong.artistName = cursor.getString(cursor.getColumnIndex(AlbumColumns.ARTIST));
         thisSong.noOfSongs = cursor.getInt(cursor.getColumnIndex(AlbumColumns.NUMBER_OF_SONGS));
         idToalbumInfo.put(thisSong.albumId, thisSong);
         //Toast.makeText(getApplicationContext(),title, Toast.LENGTH_LONG).show();
       
       }
         
       cursor = getContentResolver().query(
	           MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, 
	     	   new String[] { BaseColumns._ID,ArtistColumns.ARTIST,ArtistColumns.NUMBER_OF_ALBUMS,ArtistColumns.NUMBER_OF_TRACKS }, 
        	    ArtistColumns.ARTIST + " LIKE \"%" + query + "%\"", 
        	    null, 
        	    ArtistColumns.ARTIST + " ASC");
      // String title = null;

          while (cursor.moveToNext()) {
            title = cursor.getString(cursor.getColumnIndex(ArtistColumns.ARTIST));
            thisId = cursor.getString(cursor.getColumnIndex(BaseColumns._ID));
            artistIdRes.add(thisId);
            artistRes.add(title);
            ArtistInfo thisSong = new ArtistInfo();
            thisSong.artistId = Long.parseLong(thisId);
            thisSong.artistName=title;
            thisSong.noOfAlbums = cursor.getInt(cursor.getColumnIndex(ArtistColumns.NUMBER_OF_ALBUMS));
            thisSong.noOfSongs = cursor.getInt(cursor.getColumnIndex(ArtistColumns.NUMBER_OF_TRACKS));
            idToartistInfo.put(thisSong.artistId,thisSong);
            //Toast.makeText(getApplicationContext(),title, Toast.LENGTH_LONG).show();
          
          }
          fullSearchRes.addAll(albumRes);
          fullSearchRes.addAll(artistRes);
          fullSearchRes.addAll(titleRes);
          populateSearchList();
		
	}

   
    
}


