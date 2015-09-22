package com.Project100Pi.themusicplayer;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.HashMap;


public class SearchResultTestActivity extends Activity implements ClickInterface{

    ArrayList<String> titleRes = null;
    ArrayList<String> albumRes = null;
    ArrayList<String> artistRes = null;
    ArrayList<String> albumIdRes = null;
    ArrayList<String> artistIdRes = null;
    ArrayList<String> songIdRes = null;
    ArrayList<String> fullSearchRes = null;
    HashMap<Long, AlbumInfo> idToalbumInfo = null;
    HashMap<Long, ArtistInfo> idToartistInfo = null;
    static ArrayList<String> pathList=null;
    static HashMap<Long, songInfo> idToSongInfo =  null;

    RecyclerView albumRecycler,artistRecycler,trackRecycler;
    ArrayList<AlbumInfo> albums ;
    AlbumRecyclerAdapter ara;
    trackRecyclerAdapter tra;
    ArrayList<TrackObject> tracks;
    ArrayList<ArtistInfo> artists ;
    ArtistRecyclerAdapter arra;

    ArrayList<String> trackIdList = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_test);
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

        trackIdList = new ArrayList<String >();

        albumRecycler = (RecyclerView)findViewById(R.id.search_album_recycler);
       // albumRecycler.setHasFixedSize(true);
        MyLinearLayoutManager albumManager = new MyLinearLayoutManager(getApplicationContext());
        albumRecycler.setLayoutManager(albumManager);

        artistRecycler = (RecyclerView)findViewById(R.id.search_artist_recycler);
      //  artistRecycler.setHasFixedSize(true);
        MyLinearLayoutManager artistManager = new MyLinearLayoutManager(getApplicationContext());
        artistRecycler.setLayoutManager(artistManager);

        trackRecycler = (RecyclerView)findViewById(R.id.search_track_recycler);
      //  trackRecycler.setHasFixedSize(true);
        MyLinearLayoutManager trackManager = new MyLinearLayoutManager(getApplicationContext());
        trackRecycler.setLayoutManager(trackManager);

        albums = new ArrayList<AlbumInfo>();
        tracks = new ArrayList<TrackObject>();
        artists = new ArrayList<ArtistInfo>();
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
                if (query.length() > 1) {
                    doQuery(query);
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub
                //Toast.makeText(getBaseContext(), newText,
                //	Toast.LENGTH_SHORT).show();
                if (newText.length() > 1) {
                    doQuery(newText);
                }
                return true;
            }

        });

        return true;
    }

    public void populateSearchList(){

        ara = new AlbumRecyclerAdapter(this,albums,SearchResultTestActivity.this);
        albumRecycler.setAdapter(ara);
        arra = new ArtistRecyclerAdapter(this,artists,SearchResultTestActivity.this);
        artistRecycler.setAdapter(arra);
        tra = new trackRecyclerAdapter(this,tracks,trackIdList,SearchResultTestActivity.this);
        trackRecycler.setAdapter(tra);
    }
    public void doQuery(String query) {
        trackIdList.clear();
        tracks.clear();
        albums.clear();
        artists.clear();

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
        String title="";
        String path="";
        String trackAlbum = "";
        String trackArtist = "";
        String trackDuration  = "";
        Long id=null;
        int i = 0;
        while (cursor.moveToNext()) {
            try{
                title= cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                id=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                trackAlbum = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                trackArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                trackDuration = UtilFunctions.convertSecondsToHMmSs(Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));
                TrackObject currTrack =  new TrackObject(i,id.toString(),title,trackArtist,trackDuration,path);
                tracks.add(currTrack);
                trackIdList.add(id.toString());
                i++;
            }catch(Exception e){
                continue;
            }
        }
        String[] albumProjection = new String[] {BaseColumns._ID, MediaStore.Audio.AlbumColumns.ALBUM, MediaStore.Audio.AlbumColumns.ARTIST, MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS, MediaStore.Audio.AlbumColumns.ALBUM_ART };

        cursor = getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                albumProjection,
                MediaStore.Audio.AlbumColumns.ALBUM + " LIKE \"%" + query + "%\"",
                null,
                MediaStore.Audio.AlbumColumns.ALBUM + " ASC");
        // String title = null;
         i = 0;
        String albumName="";
        String artistName = "";
        String albumArtPath = "";
        Long albumId;
        int noOfSongs;
        while (cursor.moveToNext()) {
            albumName = cursor.getString(1);
            albumId = cursor.getLong(0);
            artistName = cursor.getString(2);
            noOfSongs = cursor.getInt(3);
            albumArtPath = cursor.getString(4);
            AlbumInfo thisAlbum = new AlbumInfo(i,albumId,albumName,artistName,albumArtPath,noOfSongs);
            albums.add(thisAlbum);
            i++;
        }

       // ara = new AlbumRecyclerAdapter(this,albums,SearchResultTestActivity.this);
        String[] artistProjection = new String[] {BaseColumns._ID, MediaStore.Audio.ArtistColumns.ARTIST, MediaStore.Audio.ArtistColumns.NUMBER_OF_TRACKS, MediaStore.Audio.ArtistColumns.NUMBER_OF_ALBUMS };
        cursor = getContentResolver().query(
                MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                artistProjection,
                MediaStore.Audio.ArtistColumns.ARTIST + " LIKE \"%" + query + "%\"",
                null,
                MediaStore.Audio.ArtistColumns.ARTIST + " ASC");
         artistName="";
        Long artistId;
        noOfSongs = 0;
        i=0;
        int noOfAlbums;

        while (cursor.moveToNext()) {
            artistName = cursor.getString(1);
            noOfSongs = cursor.getInt(2);
            noOfAlbums = cursor.getInt(3);
            artistId = cursor.getLong(0);
            ArtistInfo thisSong = new ArtistInfo(i,artistId,artistName,noOfAlbums,noOfSongs);
            artists.add(thisSong);
            i++;
            //Toast.makeText(getApplicationContext(),title, Toast.LENGTH_LONG).show();

        }
        populateSearchList();

    }

    @Override
    public void onItemClicked(int position) {

    }

    @Override
    public boolean onItemLongClicked(int position) {
        return false;
    }
}
