package com.Project100Pi.themusicplayer;

public class ArtistInfo {

	String artistName;
	int noOfAlbums,noOfSongs,sNo;
	Long artistId;

	public ArtistInfo(int no,long id,String name,int albums,int songs){
		this.sNo = no;
		this.artistId = id;
		this.artistName = name;
		this.noOfAlbums = albums;
		this.noOfSongs = songs;
	}
	public String getArtistName() {
		return artistName;
	}

	public int getNoOfSongs() {
		return noOfSongs;
	}

	public int getNoOfAlbums() {
		return noOfAlbums;
	}

	public Long getArtistId() {
		return artistId;
	}

	public int getsNo() {
		return sNo;
	}

	public void setNoOfSongs(int noOfSongs) {
		this.noOfSongs = noOfSongs;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public void setsNo(int sNo) {
		this.sNo = sNo;
	}

	public void setArtistId(long artistId) {
		this.artistId = artistId;
	}

	public void setNoOfAlbums(int noOfAlbums) {
		this.noOfAlbums = noOfAlbums;
	}

	public ArtistInfo(){}
}
