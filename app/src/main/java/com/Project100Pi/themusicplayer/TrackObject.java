package com.Project100Pi.themusicplayer;

/**
 * Created by BalachandranAR on 8/24/2015.
 */
public class TrackObject {
    private String trackName,trackArtist,trackDuration,trackPath,trackId;
    private int sNo;
    TrackObject(int sNo,String id,String name,String artist,String duration,String path){
        this.trackName = name;
        this.trackArtist = artist;
        this.trackDuration = duration;
        this.trackId = id;
        this.trackPath = path;
        this.sNo = sNo;
    }

    public String getTrackName(){
        return this.trackName;
    }

    public String getTrackArtist() {
        return trackArtist;
    }

    public String getTrackDuration() {
        return trackDuration;
    }

    public void setTrackArtist(String trackArtist) {
        this.trackArtist = trackArtist;
    }

    public void setTrackDuration(String trackDuration) {
        this.trackDuration = trackDuration;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public void setsNo(int sNo) {
        this.sNo = sNo;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public void setTrackPath(String trackPath) {
        this.trackPath = trackPath;
    }

    public int getsNo() {
        return sNo;
    }

    public String getTrackId() {
        return trackId;
    }

    public String getTrackPath() {
        return trackPath;
    }
}
