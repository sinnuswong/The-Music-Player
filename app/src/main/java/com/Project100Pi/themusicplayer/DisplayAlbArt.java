package com.Project100Pi.themusicplayer;


import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;


public class DisplayAlbArt extends ListActivity {

	 MediaPlayer mp=new MediaPlayer();
		protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.display_albart);

	       RelativeLayout lView = new RelativeLayout(this);
	      //  setContentView(R.layout.);

	       Intent i = getIntent();
	       final SongsAlbArt artist=(SongsAlbArt)i.getParcelableExtra("art");
	       ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,  R.layout.listview_layout,R.id.artist_noOfSongs, new ArrayList<String>(artist.contents.keySet()));
		      // setListAdapter(adapter);
		     //  ListView listView = (ListView) findViewById(R.id.android.list);
		         final ListView listView=getListView();
		       listView.setAdapter(adapter);
		       
	  listView.setOnItemClickListener(new OnItemClickListener() {
		    	   
	          
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
	         
	                 
	                  // ListView Clicked item value
	                  Intent intent=new Intent(DisplayAlbArt.this,PlayActivity.class);
	                 intent.putExtra("art",(Parcelable)artist);
	                 intent.putExtra("position", position);
	                 startActivity(intent);
	                 
				}

				
			
	       });    
	       
	   }


	

	}
