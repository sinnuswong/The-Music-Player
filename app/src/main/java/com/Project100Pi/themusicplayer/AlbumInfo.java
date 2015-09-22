package com.Project100Pi.themusicplayer;


public class AlbumInfo {

	String albumName,artistName,albumArtPath;
	Long albumId;
	int noOfSongs,sNo;

	public  AlbumInfo(){

	}
	public AlbumInfo(int no,Long id,String album,String artist,String artPath,int noOfSongs){

		this.sNo = no;
		this.albumName = album;
		this.albumId = id;
		this.artistName = artist;
		this.noOfSongs = noOfSongs;
		this.albumArtPath = artPath;
	}

	public int getsNo() {
		return sNo;
	}

	public int getNoOfSongs() {
		return noOfSongs;
	}

	public Long getAlbumId() {
		return albumId;
	}

	public String getAlbumName() {
		return albumName;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setsNo(int sNo) {
		this.sNo = sNo;
	}

	public void setAlbumId(Long albumId) {
		this.albumId = albumId;
	}

	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public void setNoOfSongs(int noOfSongs) {
		this.noOfSongs = noOfSongs;
	}

	public String getAlbumArtPath() {
		return albumArtPath;
	}

	public void setAlbumArtPath(String albumArtPath) {
		this.albumArtPath = albumArtPath;
	}
}
