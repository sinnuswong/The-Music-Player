package com.Project100Pi.themusicplayer;

/**
 * Created by BalachandranAR on 9/1/2015.
 */
public class GenreInfo {
    private String genreName;
    private Long genreId;
    private int sNo;

    public GenreInfo(int no,Long id,String name){
        this.genreId = id;
        this.genreName = name;
        this.sNo = no;
    }

    public void setsNo(int sNo) {
        this.sNo = sNo;
    }

    public void setGenreName(String genreName) {
        genreName = genreName;
    }

    public int getsNo() {
        return sNo;
    }

    public String getGenreName() {
        return genreName;
    }

    public Long getGenreId() {
        return genreId;
    }

    public void setGenreId(long genreId) {
        this.genreId = genreId;
    }
}
