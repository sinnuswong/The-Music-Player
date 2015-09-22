package com.Project100Pi.themusicplayer;

/**
 * Created by BalachandranAR on 9/1/2015.
 */
public class PlaylistInfo {
    private String playlistName;
    private Long playlistId;
    private int sNo;

    public PlaylistInfo(int no,Long id,String name){
        this.playlistId = id;
        this.playlistName = name;
        this.sNo = no;
    }

    public void setsNo(int sNo) {
        this.sNo = sNo;
    }

    public void setPlaylistName(String playlistName) {
        playlistName = playlistName;
    }

    public int getsNo() {
        return sNo;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public Long getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(long playlistId) {
        this.playlistId = playlistId;
    }
}
